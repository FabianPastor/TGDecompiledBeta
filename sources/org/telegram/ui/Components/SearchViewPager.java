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
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;
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

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$showActionMode$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public SearchViewPager(Context context, final DialogsActivity dialogsActivity, int i, int i2, int i3, ChatPreviewDelegate chatPreviewDelegate2) {
        super(context);
        this.folderId = i3;
        this.parent = dialogsActivity;
        this.chatPreviewDelegate = chatPreviewDelegate2;
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        this.itemAnimator = defaultItemAnimator;
        defaultItemAnimator.setAddDuration(150);
        this.itemAnimator.setMoveDuration(350);
        this.itemAnimator.setChangeDuration(0);
        this.itemAnimator.setRemoveDuration(0);
        this.itemAnimator.setMoveInterpolator(new OvershootInterpolator(1.1f));
        this.itemAnimator.setTranslationInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.dialogsSearchAdapter = new DialogsSearchAdapter(context, i, i2, this.itemAnimator) {
            public void notifyDataSetChanged() {
                RecyclerListView recyclerListView;
                int currentItemCount = getCurrentItemCount();
                super.notifyDataSetChanged();
                if (!SearchViewPager.this.lastSearchScrolledToTop && (recyclerListView = SearchViewPager.this.searchListView) != null) {
                    recyclerListView.scrollToPosition(0);
                    boolean unused = SearchViewPager.this.lastSearchScrolledToTop = true;
                }
                if (getItemCount() == 0 && currentItemCount != 0 && !isSearching()) {
                    SearchViewPager.this.emptyView.showProgress(false, false);
                }
            }
        };
        this.fragmentView = (SizeNotifierFrameLayout) dialogsActivity.getFragmentView();
        AnonymousClass2 r11 = new BlurredRecyclerView(context) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                SearchViewPager searchViewPager = SearchViewPager.this;
                if (searchViewPager.dialogsSearchAdapter != null && searchViewPager.itemAnimator != null && SearchViewPager.this.searchLayoutManager != null && SearchViewPager.this.dialogsSearchAdapter.showMoreAnimation) {
                    canvas.save();
                    invalidate();
                    int itemCount = SearchViewPager.this.dialogsSearchAdapter.getItemCount() - 1;
                    int i = 0;
                    while (true) {
                        if (i >= getChildCount()) {
                            break;
                        }
                        View childAt = getChildAt(i);
                        if (getChildAdapterPosition(childAt) == itemCount) {
                            canvas.clipRect(0.0f, 0.0f, (float) getWidth(), ((float) childAt.getBottom()) + childAt.getTranslationY());
                            break;
                        }
                        i++;
                    }
                }
                super.dispatchDraw(canvas);
                SearchViewPager searchViewPager2 = SearchViewPager.this;
                if (!(searchViewPager2.dialogsSearchAdapter == null || searchViewPager2.itemAnimator == null || SearchViewPager.this.searchLayoutManager == null || !SearchViewPager.this.dialogsSearchAdapter.showMoreAnimation)) {
                    canvas.restore();
                }
                DialogsSearchAdapter dialogsSearchAdapter = SearchViewPager.this.dialogsSearchAdapter;
                if (dialogsSearchAdapter != null && dialogsSearchAdapter.showMoreHeader != null) {
                    canvas.save();
                    canvas.translate((float) SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.getLeft(), ((float) SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.getTop()) + SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.getTranslationY());
                    SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.draw(canvas);
                    canvas.restore();
                }
            }
        };
        this.searchListView = r11;
        r11.setItemAnimator(this.itemAnimator);
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
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(dialogsActivity.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int abs = Math.abs(SearchViewPager.this.searchLayoutManager.findLastVisibleItemPosition() - SearchViewPager.this.searchLayoutManager.findFirstVisibleItemPosition()) + 1;
                int itemCount = recyclerView.getAdapter().getItemCount();
                if (abs > 0 && SearchViewPager.this.searchLayoutManager.findLastVisibleItemPosition() == itemCount - 1 && !SearchViewPager.this.dialogsSearchAdapter.isMessagesSearchEndReached()) {
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
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        flickerLoadingView.setViewType(1);
        AnonymousClass4 r13 = new StickerEmptyView(context, flickerLoadingView, 1) {
            public void setVisibility(int i) {
                if (SearchViewPager.this.noMediaFiltersSearchView.getTag() != null) {
                    super.setVisibility(8);
                } else {
                    super.setVisibility(i);
                }
            }
        };
        this.emptyView = r13;
        r13.title.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.subtitle.setVisibility(8);
        this.emptyView.setVisibility(8);
        this.emptyView.addView(flickerLoadingView, 0);
        this.emptyView.showProgress(true, false);
        this.searchContainer.addView(this.emptyView);
        this.searchContainer.addView(this.searchListView);
        this.searchContainer.addView(this.noMediaFiltersSearchView);
        this.searchListView.setEmptyView(this.emptyView);
        this.searchListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                SearchViewPager.this.fragmentView.invalidateBlur();
            }
        });
        this.itemsEnterAnimator = new RecyclerItemsEnterAnimator(this.searchListView, true);
        ViewPagerAdapter viewPagerAdapter2 = new ViewPagerAdapter();
        this.viewPagerAdapter = viewPagerAdapter2;
        setAdapter(viewPagerAdapter2);
    }

    public void onTextChanged(String str) {
        View currentView = getCurrentView();
        boolean z = true;
        boolean z2 = !this.attached;
        if (!TextUtils.isEmpty(this.lastSearchString)) {
            z = z2;
        }
        this.lastSearchString = str;
        search(currentView, getCurrentPosition(), str, z);
    }

    /* access modifiers changed from: private */
    public void search(View view, int i, String str, boolean z) {
        boolean z2;
        View view2 = view;
        String str2 = str;
        long j = 0;
        long j2 = 0;
        long j3 = 0;
        boolean z3 = false;
        for (int i2 = 0; i2 < this.currentSearchFilters.size(); i2++) {
            FiltersView.MediaFilterData mediaFilterData = this.currentSearchFilters.get(i2);
            int i3 = mediaFilterData.filterType;
            if (i3 == 4) {
                TLObject tLObject = mediaFilterData.chat;
                if (tLObject instanceof TLRPC$User) {
                    j = ((TLRPC$User) tLObject).id;
                } else if (tLObject instanceof TLRPC$Chat) {
                    j = -((TLRPC$Chat) tLObject).id;
                }
            } else if (i3 == 6) {
                FiltersView.DateData dateData = mediaFilterData.dateData;
                long j4 = dateData.minDate;
                long j5 = dateData.maxDate;
                j2 = j4;
                j3 = j5;
            } else if (i3 == 7) {
                z3 = true;
            }
        }
        if (view2 == this.searchContainer) {
            if (j == 0 && j2 == 0 && j3 == 0) {
                this.lastSearchScrolledToTop = false;
                this.dialogsSearchAdapter.searchDialogs(str2, z3 ? 1 : 0);
                this.dialogsSearchAdapter.setFiltersDelegate(this.filteredSearchViewDelegate, false);
                this.noMediaFiltersSearchView.animate().setListener((Animator.AnimatorListener) null).cancel();
                this.noMediaFiltersSearchView.setDelegate((FilteredSearchView.Delegate) null, false);
                if (z) {
                    this.emptyView.showProgress(!this.dialogsSearchAdapter.isSearching(), false);
                    this.emptyView.showProgress(this.dialogsSearchAdapter.isSearching(), false);
                } else if (!this.dialogsSearchAdapter.hasRecentSearch()) {
                    this.emptyView.showProgress(this.dialogsSearchAdapter.isSearching(), true);
                }
                if (z) {
                    this.noMediaFiltersSearchView.setVisibility(8);
                } else if (this.noMediaFiltersSearchView.getVisibility() != 8) {
                    this.noMediaFiltersSearchView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            SearchViewPager.this.noMediaFiltersSearchView.setVisibility(8);
                        }
                    }).setDuration(150).start();
                }
                this.noMediaFiltersSearchView.setTag((Object) null);
            } else {
                this.noMediaFiltersSearchView.setTag(1);
                this.noMediaFiltersSearchView.setDelegate(this.filteredSearchViewDelegate, false);
                this.noMediaFiltersSearchView.animate().setListener((Animator.AnimatorListener) null).cancel();
                if (z) {
                    this.noMediaFiltersSearchView.setVisibility(0);
                    this.noMediaFiltersSearchView.setAlpha(1.0f);
                    z2 = z;
                } else {
                    if (this.noMediaFiltersSearchView.getVisibility() != 0) {
                        this.noMediaFiltersSearchView.setVisibility(0);
                        this.noMediaFiltersSearchView.setAlpha(0.0f);
                        z2 = true;
                    } else {
                        z2 = z;
                    }
                    this.noMediaFiltersSearchView.animate().alpha(1.0f).setDuration(150).start();
                }
                this.noMediaFiltersSearchView.search(j, j2, j3, (FiltersView.MediaFilterData) null, z3, str, z2);
                this.emptyView.setVisibility(8);
            }
            this.emptyView.setKeyboardHeight(this.keyboardSize, false);
            this.noMediaFiltersSearchView.setKeyboardHeight(this.keyboardSize, false);
        } else if (view2 instanceof FilteredSearchView) {
            FilteredSearchView filteredSearchView = (FilteredSearchView) view2;
            filteredSearchView.setKeyboardHeight(this.keyboardSize, false);
            filteredSearchView.search(j, j2, j3, FiltersView.filters[this.viewPagerAdapter.items.get(i).filterIndex], z3, str, z);
        } else if (view2 instanceof SearchDownloadsContainer) {
            SearchDownloadsContainer searchDownloadsContainer = (SearchDownloadsContainer) view2;
            searchDownloadsContainer.setKeyboardHeight(this.keyboardSize, false);
            searchDownloadsContainer.search(str2);
        }
    }

    public void onResume() {
        DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter2 != null) {
            dialogsSearchAdapter2.notifyDataSetChanged();
        }
    }

    public void removeSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        this.currentSearchFilters.remove(mediaFilterData);
    }

    public ArrayList<FiltersView.MediaFilterData> getCurrentSearchFilters() {
        return this.currentSearchFilters;
    }

    public void clear() {
        this.currentSearchFilters.clear();
    }

    public void setFilteredSearchViewDelegate(FilteredSearchView.Delegate delegate) {
        this.filteredSearchViewDelegate = delegate;
    }

    private void showActionMode(boolean z) {
        if (this.isActionModeShowed != z) {
            if (!z || !this.parent.getActionBar().isActionModeShowed()) {
                if (z && !this.parent.getActionBar().actionModeIsExist("search_view_pager")) {
                    ActionBarMenu createActionMode = this.parent.getActionBar().createActionMode(true, "search_view_pager");
                    NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
                    this.selectedMessagesCountTextView = numberTextView;
                    numberTextView.setTextSize(18);
                    this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    this.selectedMessagesCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
                    createActionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
                    this.selectedMessagesCountTextView.setOnTouchListener(SearchViewPager$$ExternalSyntheticLambda2.INSTANCE);
                    this.gotoItem = createActionMode.addItemWithWidth(200, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("AccDescrGoToMessage", NUM));
                    this.forwardItem = createActionMode.addItemWithWidth(201, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("Forward", NUM));
                    this.deleteItem = createActionMode.addItemWithWidth(202, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("Delete", NUM));
                }
                if (this.parent.getActionBar().getBackButton().getDrawable() instanceof MenuDrawable) {
                    this.parent.getActionBar().setBackButtonDrawable(new BackDrawable(false));
                }
                this.isActionModeShowed = z;
                if (z) {
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
                int size = this.viewsByType.size();
                for (int i2 = 0; i2 < size; i2++) {
                    View valueAt = this.viewsByType.valueAt(i2);
                    if (valueAt instanceof FilteredSearchView) {
                        ((FilteredSearchView) valueAt).update();
                    }
                }
            }
        }
    }

    public void onActionBarItemClick(int i) {
        if (i == 202) {
            BaseFragment baseFragment = this.parent;
            if (baseFragment != null && baseFragment.getParentActivity() != null) {
                ArrayList arrayList = new ArrayList(this.selectedFiles.values());
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parent.getParentActivity());
                builder.setTitle(LocaleController.formatPluralString("RemoveDocumentsTitle", this.selectedFiles.size(), new Object[0]));
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(AndroidUtilities.replaceTags(LocaleController.formatPluralString("RemoveDocumentsMessage", this.selectedFiles.size(), new Object[0]))).append("\n\n").append(LocaleController.getString("RemoveDocumentsAlertMessage", NUM));
                builder.setMessage(spannableStringBuilder);
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), SearchViewPager$$ExternalSyntheticLambda1.INSTANCE);
                builder.setPositiveButton(LocaleController.getString("Delete", NUM), new SearchViewPager$$ExternalSyntheticLambda0(this, arrayList));
                TextView textView = (TextView) builder.show().getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        } else if (i == 200) {
            if (this.selectedFiles.size() == 1) {
                goToMessage(this.selectedFiles.values().iterator().next());
            }
        } else if (i == 201) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putInt("dialogsType", 3);
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            dialogsActivity.setDelegate(new SearchViewPager$$ExternalSyntheticLambda3(this));
            this.parent.presentFragment(dialogsActivity);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActionBarItemClick$2(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        this.parent.getDownloadController().deleteRecentFiles(arrayList);
        hideActionMode();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActionBarItemClick$3(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        DialogsActivity dialogsActivity2 = dialogsActivity;
        ArrayList arrayList2 = arrayList;
        ArrayList arrayList3 = new ArrayList();
        for (FilteredSearchView.MessageHashId messageHashId : this.selectedFiles.keySet()) {
            arrayList3.add(this.selectedFiles.get(messageHashId));
        }
        this.selectedFiles.clear();
        showActionMode(false);
        if (arrayList.size() > 1 || ((Long) arrayList2.get(0)).longValue() == AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientUserId() || charSequence != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                long longValue = ((Long) arrayList2.get(i)).longValue();
                if (charSequence != null) {
                    AccountInstance.getInstance(this.currentAccount).getSendMessagesHelper().sendMessage(charSequence.toString(), longValue, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                }
                AccountInstance.getInstance(this.currentAccount).getSendMessagesHelper().sendMessage((ArrayList<MessageObject>) arrayList3, longValue, false, false, true, 0);
            }
            dialogsActivity.finishFragment();
            return;
        }
        long longValue2 = ((Long) arrayList2.get(0)).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(longValue2)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(longValue2));
        } else {
            if (DialogObject.isUserDialog(longValue2)) {
                bundle.putLong("user_id", longValue2);
            } else {
                bundle.putLong("chat_id", -longValue2);
            }
            if (!AccountInstance.getInstance(this.currentAccount).getMessagesController().checkCanOpenChat(bundle, dialogsActivity2)) {
                return;
            }
        }
        ChatActivity chatActivity = new ChatActivity(bundle);
        dialogsActivity2.presentFragment(chatActivity, true);
        chatActivity.showFieldPanelForForward(true, arrayList3);
    }

    public void goToMessage(MessageObject messageObject) {
        Bundle bundle = new Bundle();
        long dialogId = messageObject.getDialogId();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
        } else if (DialogObject.isUserDialog(dialogId)) {
            bundle.putLong("user_id", dialogId);
        } else {
            TLRPC$Chat chat = AccountInstance.getInstance(this.currentAccount).getMessagesController().getChat(Long.valueOf(-dialogId));
            if (!(chat == null || chat.migrated_to == null)) {
                bundle.putLong("migrated_to", dialogId);
                dialogId = -chat.migrated_to.channel_id;
            }
            bundle.putLong("chat_id", -dialogId);
        }
        bundle.putInt("message_id", messageObject.getId());
        this.parent.presentFragment(new ChatActivity(bundle));
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

    public void toggleItemSelection(MessageObject messageObject, View view, int i) {
        boolean z;
        FilteredSearchView.MessageHashId messageHashId = new FilteredSearchView.MessageHashId(messageObject.getId(), messageObject.getDialogId());
        if (this.selectedFiles.containsKey(messageHashId)) {
            this.selectedFiles.remove(messageHashId);
        } else if (this.selectedFiles.size() < 100) {
            this.selectedFiles.put(messageHashId, messageObject);
        } else {
            return;
        }
        int i2 = 0;
        if (this.selectedFiles.size() == 0) {
            showActionMode(false);
        } else {
            this.selectedMessagesCountTextView.setNumber(this.selectedFiles.size(), true);
            ActionBarMenuItem actionBarMenuItem = this.gotoItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(this.selectedFiles.size() == 1 ? 0 : 8);
            }
            if (this.deleteItem != null) {
                Iterator<FilteredSearchView.MessageHashId> it = this.selectedFiles.keySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = true;
                        break;
                    }
                    if (!this.selectedFiles.get(it.next()).isDownloadingFile) {
                        z = false;
                        break;
                    }
                }
                ActionBarMenuItem actionBarMenuItem2 = this.deleteItem;
                if (!z) {
                    i2 = 8;
                }
                actionBarMenuItem2.setVisibility(i2);
            }
        }
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(this.selectedFiles.containsKey(messageHashId), true);
        } else if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(i, this.selectedFiles.containsKey(messageHashId), true);
        } else if (view instanceof SharedLinkCell) {
            ((SharedLinkCell) view).setChecked(this.selectedFiles.containsKey(messageHashId), true);
        } else if (view instanceof SharedAudioCell) {
            ((SharedAudioCell) view).setChecked(this.selectedFiles.containsKey(messageHashId), true);
        } else if (view instanceof ContextLinkCell) {
            ((ContextLinkCell) view).setChecked(this.selectedFiles.containsKey(messageHashId), true);
        } else if (view instanceof DialogCell) {
            ((DialogCell) view).setChecked(this.selectedFiles.containsKey(messageHashId), true);
        }
    }

    public boolean isSelected(FilteredSearchView.MessageHashId messageHashId) {
        return this.selectedFiles.containsKey(messageHashId);
    }

    public void showActionMode() {
        showActionMode(true);
    }

    /* access modifiers changed from: protected */
    public void onItemSelected(View view, View view2, int i, int i2) {
        boolean z = true;
        if (i == 0) {
            if (this.noMediaFiltersSearchView.getVisibility() == 0) {
                this.noMediaFiltersSearchView.setDelegate(this.filteredSearchViewDelegate, false);
                this.dialogsSearchAdapter.setFiltersDelegate((FilteredSearchView.Delegate) null, false);
            } else {
                this.noMediaFiltersSearchView.setDelegate((FilteredSearchView.Delegate) null, false);
                this.dialogsSearchAdapter.setFiltersDelegate(this.filteredSearchViewDelegate, true);
            }
        } else if (view instanceof FilteredSearchView) {
            if (i2 != 0 || this.noMediaFiltersSearchView.getVisibility() == 0) {
                z = false;
            }
            ((FilteredSearchView) view).setDelegate(this.filteredSearchViewDelegate, z);
        }
        if (view2 instanceof FilteredSearchView) {
            ((FilteredSearchView) view2).setDelegate((FilteredSearchView.Delegate) null, false);
            return;
        }
        this.dialogsSearchAdapter.setFiltersDelegate((FilteredSearchView.Delegate) null, false);
        this.noMediaFiltersSearchView.setDelegate((FilteredSearchView.Delegate) null, false);
    }

    public void getThemeDescriptions(ArrayList<ThemeDescription> arrayList) {
        ArrayList<ThemeDescription> arrayList2 = arrayList;
        for (int i = 0; i < this.searchListView.getChildCount(); i++) {
            View childAt = this.searchListView.getChildAt(i);
            if ((childAt instanceof ProfileSearchCell) || (childAt instanceof DialogCell) || (childAt instanceof HashtagSearchCell)) {
                arrayList2.add(new ThemeDescription(childAt, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            }
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2) instanceof FilteredSearchView) {
                arrayList2.addAll(((FilteredSearchView) getChildAt(i2)).getThemeDescriptions());
            }
        }
        int size = this.viewsByType.size();
        for (int i3 = 0; i3 < size; i3++) {
            View valueAt = this.viewsByType.valueAt(i3);
            if (valueAt instanceof FilteredSearchView) {
                arrayList2.addAll(((FilteredSearchView) valueAt).getThemeDescriptions());
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
                int childCount = recyclerListView.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = recyclerListView.getChildAt(i2);
                    if (childAt instanceof DialogCell) {
                        ((DialogCell) childAt).update(0);
                    }
                }
            }
        }
        int size = this.viewsByType.size();
        for (int i3 = 0; i3 < size; i3++) {
            View valueAt = this.viewsByType.valueAt(i3);
            if (valueAt instanceof FilteredSearchView) {
                RecyclerListView recyclerListView2 = ((FilteredSearchView) valueAt).recyclerListView;
                int childCount2 = recyclerListView2.getChildCount();
                for (int i4 = 0; i4 < childCount2; i4++) {
                    View childAt2 = recyclerListView2.getChildAt(i4);
                    if (childAt2 instanceof DialogCell) {
                        ((DialogCell) childAt2).update(0);
                    }
                }
            }
        }
        FilteredSearchView filteredSearchView = this.noMediaFiltersSearchView;
        if (filteredSearchView != null) {
            RecyclerListView recyclerListView3 = filteredSearchView.recyclerListView;
            int childCount3 = recyclerListView3.getChildCount();
            for (int i5 = 0; i5 < childCount3; i5++) {
                View childAt3 = recyclerListView3.getChildAt(i5);
                if (childAt3 instanceof DialogCell) {
                    ((DialogCell) childAt3).update(0);
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

    public void setPosition(int i) {
        if (i >= 0) {
            super.setPosition(i);
            this.viewsByType.clear();
            ViewPagerFixed.TabsView tabsView = this.tabsView;
            if (tabsView != null) {
                tabsView.selectTabWithId(i, 1.0f);
            }
            invalidate();
        }
    }

    public void setKeyboardHeight(int i) {
        this.keyboardSize = i;
        boolean z = getVisibility() == 0 && getAlpha() > 0.0f;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2) instanceof FilteredSearchView) {
                ((FilteredSearchView) getChildAt(i2)).setKeyboardHeight(i, z);
            } else if (getChildAt(i2) == this.searchContainer) {
                this.emptyView.setKeyboardHeight(i, z);
                this.noMediaFiltersSearchView.setKeyboardHeight(i, z);
            } else if (getChildAt(i2) instanceof SearchDownloadsContainer) {
                ((SearchDownloadsContainer) getChildAt(i2)).setKeyboardHeight(i, z);
            }
        }
    }

    public void showOnlyDialogsAdapter(boolean z) {
        this.showOnlyDialogsAdapter = z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x007c, code lost:
        if (org.telegram.messenger.ChatObject.isChannel((long) r7, r11.currentAccount) != false) goto L_0x0080;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void messagesDeleted(long r12, java.util.ArrayList<java.lang.Integer> r14) {
        /*
            r11 = this;
            android.util.SparseArray<android.view.View> r0 = r11.viewsByType
            int r0 = r0.size()
            r1 = 0
            r2 = 0
        L_0x0008:
            if (r2 >= r0) goto L_0x001e
            android.util.SparseArray<android.view.View> r3 = r11.viewsByType
            java.lang.Object r3 = r3.valueAt(r2)
            android.view.View r3 = (android.view.View) r3
            boolean r4 = r3 instanceof org.telegram.ui.FilteredSearchView
            if (r4 == 0) goto L_0x001b
            org.telegram.ui.FilteredSearchView r3 = (org.telegram.ui.FilteredSearchView) r3
            r3.messagesDeleted(r12, r14)
        L_0x001b:
            int r2 = r2 + 1
            goto L_0x0008
        L_0x001e:
            r0 = 0
        L_0x001f:
            int r2 = r11.getChildCount()
            if (r0 >= r2) goto L_0x0039
            android.view.View r2 = r11.getChildAt(r0)
            boolean r2 = r2 instanceof org.telegram.ui.FilteredSearchView
            if (r2 == 0) goto L_0x0036
            android.view.View r2 = r11.getChildAt(r0)
            org.telegram.ui.FilteredSearchView r2 = (org.telegram.ui.FilteredSearchView) r2
            r2.messagesDeleted(r12, r14)
        L_0x0036:
            int r0 = r0 + 1
            goto L_0x001f
        L_0x0039:
            org.telegram.ui.FilteredSearchView r0 = r11.noMediaFiltersSearchView
            r0.messagesDeleted(r12, r14)
            java.util.HashMap<org.telegram.ui.FilteredSearchView$MessageHashId, org.telegram.messenger.MessageObject> r0 = r11.selectedFiles
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x00dd
            r0 = 0
            java.util.ArrayList r2 = new java.util.ArrayList
            java.util.HashMap<org.telegram.ui.FilteredSearchView$MessageHashId, org.telegram.messenger.MessageObject> r3 = r11.selectedFiles
            java.util.Set r3 = r3.keySet()
            r2.<init>(r3)
            r3 = 0
        L_0x0053:
            int r4 = r2.size()
            if (r3 >= r4) goto L_0x00aa
            java.lang.Object r4 = r2.get(r3)
            org.telegram.ui.FilteredSearchView$MessageHashId r4 = (org.telegram.ui.FilteredSearchView.MessageHashId) r4
            java.util.HashMap<org.telegram.ui.FilteredSearchView$MessageHashId, org.telegram.messenger.MessageObject> r5 = r11.selectedFiles
            java.lang.Object r5 = r5.get(r4)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            if (r5 == 0) goto L_0x00a7
            long r6 = r5.getDialogId()
            r8 = 0
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 >= 0) goto L_0x007f
            long r6 = -r6
            int r7 = (int) r6
            long r8 = (long) r7
            int r6 = r11.currentAccount
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r8, r6)
            if (r6 == 0) goto L_0x007f
            goto L_0x0080
        L_0x007f:
            r7 = 0
        L_0x0080:
            long r6 = (long) r7
            int r8 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r8 != 0) goto L_0x00a7
            r6 = 0
        L_0x0086:
            int r7 = r14.size()
            if (r6 >= r7) goto L_0x00a7
            int r7 = r5.getId()
            java.lang.Object r8 = r14.get(r6)
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r8 = r8.intValue()
            if (r7 != r8) goto L_0x00a4
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r0.add(r4)
        L_0x00a4:
            int r6 = r6 + 1
            goto L_0x0086
        L_0x00a7:
            int r3 = r3 + 1
            goto L_0x0053
        L_0x00aa:
            if (r0 == 0) goto L_0x00dd
            int r12 = r0.size()
            r13 = 0
        L_0x00b1:
            if (r13 >= r12) goto L_0x00bf
            java.util.HashMap<org.telegram.ui.FilteredSearchView$MessageHashId, org.telegram.messenger.MessageObject> r14 = r11.selectedFiles
            java.lang.Object r2 = r0.get(r13)
            r14.remove(r2)
            int r13 = r13 + 1
            goto L_0x00b1
        L_0x00bf:
            org.telegram.ui.Components.NumberTextView r12 = r11.selectedMessagesCountTextView
            java.util.HashMap<org.telegram.ui.FilteredSearchView$MessageHashId, org.telegram.messenger.MessageObject> r13 = r11.selectedFiles
            int r13 = r13.size()
            r14 = 1
            r12.setNumber(r13, r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r12 = r11.gotoItem
            if (r12 == 0) goto L_0x00dd
            java.util.HashMap<org.telegram.ui.FilteredSearchView$MessageHashId, org.telegram.messenger.MessageObject> r13 = r11.selectedFiles
            int r13 = r13.size()
            if (r13 != r14) goto L_0x00d8
            goto L_0x00da
        L_0x00d8:
            r1 = 8
        L_0x00da:
            r12.setVisibility(r1)
        L_0x00dd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SearchViewPager.messagesDeleted(long, java.util.ArrayList):void");
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

    public int getPositionForType(int i) {
        for (int i2 = 0; i2 < this.viewPagerAdapter.items.size(); i2++) {
            if (this.viewPagerAdapter.items.get(i2).type == 2 && this.viewPagerAdapter.items.get(i2).filterIndex == i) {
                return i2;
            }
        }
        return -1;
    }

    private class ViewPagerAdapter extends ViewPagerFixed.Adapter {
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

        public String getItemTitle(int i) {
            if (this.items.get(i).type == 0) {
                return LocaleController.getString("SearchAllChatsShort", NUM);
            }
            if (this.items.get(i).type == 1) {
                return LocaleController.getString("DownloadsTabs", NUM);
            }
            return FiltersView.filters[this.items.get(i).filterIndex].title;
        }

        public int getItemCount() {
            return this.items.size();
        }

        public View createView(int i) {
            if (i == 1) {
                return SearchViewPager.this.searchContainer;
            }
            if (i == 2) {
                SearchViewPager searchViewPager = SearchViewPager.this;
                SearchDownloadsContainer searchDownloadsContainer = new SearchDownloadsContainer(searchViewPager.parent, searchViewPager.currentAccount);
                searchDownloadsContainer.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                        super.onScrolled(recyclerView, i, i2);
                        SearchViewPager.this.fragmentView.invalidateBlur();
                    }
                });
                searchDownloadsContainer.setUiCallback(SearchViewPager.this);
                return searchDownloadsContainer;
            }
            FilteredSearchView filteredSearchView = new FilteredSearchView(SearchViewPager.this.parent);
            filteredSearchView.setChatPreviewDelegate(SearchViewPager.this.chatPreviewDelegate);
            filteredSearchView.setUiCallback(SearchViewPager.this);
            filteredSearchView.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    super.onScrolled(recyclerView, i, i2);
                    SearchViewPager.this.fragmentView.invalidateBlur();
                }
            });
            return filteredSearchView;
        }

        public int getItemViewType(int i) {
            if (this.items.get(i).type == 0) {
                return 1;
            }
            if (this.items.get(i).type == 1) {
                return 2;
            }
            return this.items.get(i).type + i;
        }

        public void bindView(View view, int i, int i2) {
            SearchViewPager searchViewPager = SearchViewPager.this;
            searchViewPager.search(view, i, searchViewPager.lastSearchString, true);
        }

        private class Item {
            int filterIndex;
            /* access modifiers changed from: private */
            public final int type;

            private Item(ViewPagerAdapter viewPagerAdapter, int i) {
                this.type = i;
            }
        }
    }
}
