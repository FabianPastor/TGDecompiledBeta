package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.PhotoViewer;

public class SearchDownloadsContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    DownloadsAdapter adapter = new DownloadsAdapter();
    private final int currentAccount;
    ArrayList<MessageObject> currentLoadingFiles = new ArrayList<>();
    ArrayList<MessageObject> currentLoadingFilesTmp = new ArrayList<>();
    int downloadingFilesEndRow = -1;
    int downloadingFilesHeader = -1;
    int downloadingFilesStartRow = -1;
    StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public boolean hasCurrentDownload;
    RecyclerItemsEnterAnimator itemsEnterAnimator;
    String lastQueryString;
    Runnable lastSearchRunnable;
    private final FlickerLoadingView loadingView;
    /* access modifiers changed from: private */
    public final FilteredSearchView.MessageHashId messageHashIdTmp = new FilteredSearchView.MessageHashId(0, 0);
    Activity parentActivity;
    BaseFragment parentFragment;
    int recentFilesEndRow = -1;
    int recentFilesHeader = -1;
    int recentFilesStartRow = -1;
    ArrayList<MessageObject> recentLoadingFiles = new ArrayList<>();
    ArrayList<MessageObject> recentLoadingFilesTmp = new ArrayList<>();
    public RecyclerListView recyclerListView;
    int rowCount;
    String searchQuery;
    FilteredSearchView.UiCallback uiCallback;

    public SearchDownloadsContainer(BaseFragment baseFragment, int i) {
        super(baseFragment.getParentActivity());
        this.parentFragment = baseFragment;
        this.parentActivity = baseFragment.getParentActivity();
        this.currentAccount = i;
        BlurredRecyclerView blurredRecyclerView = new BlurredRecyclerView(getContext());
        this.recyclerListView = blurredRecyclerView;
        addView(blurredRecyclerView);
        this.recyclerListView.setLayoutManager(new LinearLayoutManager(this, baseFragment.getParentActivity()) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.recyclerListView.setAdapter(this.adapter);
        this.recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(SearchDownloadsContainer.this.parentActivity.getCurrentFocus());
                }
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SearchDownloadsContainer$$ExternalSyntheticLambda4(this));
        this.recyclerListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new SearchDownloadsContainer$$ExternalSyntheticLambda5(this));
        this.itemsEnterAnimator = new RecyclerItemsEnterAnimator(this.recyclerListView, true);
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(getContext());
        this.loadingView = flickerLoadingView;
        addView(flickerLoadingView);
        flickerLoadingView.setUseHeaderOffset(true);
        flickerLoadingView.setViewType(3);
        flickerLoadingView.setVisibility(8);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(getContext(), flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        addView(stickerEmptyView);
        this.recyclerListView.setEmptyView(this.emptyView);
        FileLoader.getInstance(i).getCurrentLoadingFiles(this.currentLoadingFiles);
        update(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i) {
        MessageObject access$400 = this.adapter.getMessage(i);
        if (access$400 != null) {
            if (this.uiCallback.actionModeShowing()) {
                this.uiCallback.toggleItemSelection(access$400, view, 0);
                this.messageHashIdTmp.set(access$400.getId(), access$400.getDialogId());
                this.adapter.notifyItemChanged(i);
                return;
            }
            if (view instanceof Cell) {
                SharedDocumentCell sharedDocumentCell = ((Cell) view).sharedDocumentCell;
                MessageObject message = sharedDocumentCell.getMessage();
                TLRPC$Document document = message.getDocument();
                if (sharedDocumentCell.isLoaded()) {
                    if (message.isRoundVideo() || message.isVoice()) {
                        MediaController.getInstance().playMessage(message);
                        return;
                    } else if (message.canPreviewDocument()) {
                        PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(message);
                        PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                        PhotoViewer.getInstance().openPhoto((ArrayList<MessageObject>) arrayList, 0, 0, 0, (PhotoViewer.PhotoViewerProvider) new PhotoViewer.EmptyPhotoViewerProvider());
                        return;
                    } else {
                        AndroidUtilities.openDocument(message, this.parentActivity, this.parentFragment);
                    }
                } else if (!sharedDocumentCell.isLoading()) {
                    access$400.putInDownloadsStore = true;
                    AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(document, access$400, 0, 0);
                    sharedDocumentCell.updateFileExistIcon(true);
                } else {
                    AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().cancelLoadFile(document);
                    sharedDocumentCell.updateFileExistIcon(true);
                }
                update(true);
            }
            if (view instanceof SharedAudioCell) {
                ((SharedAudioCell) view).didPressedButton();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$1(View view, int i) {
        MessageObject access$400 = this.adapter.getMessage(i);
        if (access$400 == null) {
            return false;
        }
        if (!this.uiCallback.actionModeShowing()) {
            this.uiCallback.showActionMode();
        }
        if (!this.uiCallback.actionModeShowing()) {
            return true;
        }
        this.uiCallback.toggleItemSelection(access$400, view, 0);
        this.messageHashIdTmp.set(access$400.getId(), access$400.getDialogId());
        this.adapter.notifyItemChanged(i);
        return true;
    }

    public void update(boolean z) {
        if (TextUtils.isEmpty(this.searchQuery) || isEmptyDownloads()) {
            if (this.rowCount == 0) {
                this.itemsEnterAnimator.showItemsAnimated(0);
            }
            FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(this.currentLoadingFilesTmp);
            FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(this.recentLoadingFilesTmp);
            for (int i = 0; i < this.currentLoadingFiles.size(); i++) {
                this.currentLoadingFiles.get(i).setQuery((String) null);
            }
            for (int i2 = 0; i2 < this.recentLoadingFiles.size(); i2++) {
                this.recentLoadingFiles.get(i2).setQuery((String) null);
            }
            this.lastQueryString = null;
            updateListInternal(z, this.currentLoadingFilesTmp, this.recentLoadingFilesTmp);
            if (this.rowCount == 0) {
                this.emptyView.showProgress(false, false);
                this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewDownloads", NUM));
                this.emptyView.subtitle.setVisibility(8);
            }
            this.emptyView.setStickerType(9);
            return;
        }
        this.emptyView.setStickerType(1);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(arrayList);
        FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(arrayList2);
        String lowerCase = this.searchQuery.toLowerCase();
        boolean equals = lowerCase.equals(this.lastQueryString);
        this.lastQueryString = lowerCase;
        Utilities.searchQueue.cancelRunnable(this.lastSearchRunnable);
        DispatchQueue dispatchQueue = Utilities.searchQueue;
        SearchDownloadsContainer$$ExternalSyntheticLambda3 searchDownloadsContainer$$ExternalSyntheticLambda3 = new SearchDownloadsContainer$$ExternalSyntheticLambda3(this, arrayList, lowerCase, arrayList2);
        this.lastSearchRunnable = searchDownloadsContainer$$ExternalSyntheticLambda3;
        dispatchQueue.postRunnable(searchDownloadsContainer$$ExternalSyntheticLambda3, equals ? 0 : 300);
        this.recentLoadingFilesTmp.clear();
        this.currentLoadingFilesTmp.clear();
        if (!equals) {
            this.emptyView.showProgress(true, true);
            updateListInternal(z, this.currentLoadingFilesTmp, this.recentLoadingFilesTmp);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$update$3(ArrayList arrayList, String str, ArrayList arrayList2) {
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            if (FileLoader.getDocumentFileName(((MessageObject) arrayList.get(i)).getDocument()).toLowerCase().contains(str)) {
                MessageObject messageObject = new MessageObject(this.currentAccount, ((MessageObject) arrayList.get(i)).messageOwner, false, false);
                messageObject.mediaExists = ((MessageObject) arrayList.get(i)).mediaExists;
                messageObject.setQuery(this.searchQuery);
                arrayList3.add(messageObject);
            }
        }
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            if (FileLoader.getDocumentFileName(((MessageObject) arrayList2.get(i2)).getDocument()).toLowerCase().contains(str)) {
                MessageObject messageObject2 = new MessageObject(this.currentAccount, ((MessageObject) arrayList2.get(i2)).messageOwner, false, false);
                messageObject2.mediaExists = ((MessageObject) arrayList2.get(i2)).mediaExists;
                messageObject2.setQuery(this.searchQuery);
                arrayList4.add(messageObject2);
            }
        }
        AndroidUtilities.runOnUIThread(new SearchDownloadsContainer$$ExternalSyntheticLambda2(this, str, arrayList3, arrayList4));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$update$2(String str, ArrayList arrayList, ArrayList arrayList2) {
        if (str.equals(this.lastQueryString)) {
            if (this.rowCount == 0) {
                this.itemsEnterAnimator.showItemsAnimated(0);
            }
            updateListInternal(true, arrayList, arrayList2);
            if (this.rowCount == 0) {
                this.emptyView.showProgress(false, true);
                this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", NUM));
                this.emptyView.subtitle.setVisibility(0);
                this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
            }
        }
    }

    private boolean isEmptyDownloads() {
        return DownloadController.getInstance(this.currentAccount).downloadingFiles.isEmpty() && DownloadController.getInstance(this.currentAccount).recentDownloadingFiles.isEmpty();
    }

    private void updateListInternal(boolean z, ArrayList<MessageObject> arrayList, ArrayList<MessageObject> arrayList2) {
        RecyclerView.ViewHolder childViewHolder;
        if (z) {
            final int i = this.downloadingFilesHeader;
            final int i2 = this.downloadingFilesStartRow;
            final int i3 = this.downloadingFilesEndRow;
            final int i4 = this.recentFilesHeader;
            final int i5 = this.recentFilesStartRow;
            final int i6 = this.recentFilesEndRow;
            final int i7 = this.rowCount;
            final ArrayList arrayList3 = new ArrayList(this.currentLoadingFiles);
            final ArrayList arrayList4 = new ArrayList(this.recentLoadingFiles);
            updateRows(arrayList, arrayList2);
            DiffUtil.calculateDiff(new DiffUtil.Callback() {
                public int getOldListSize() {
                    return i7;
                }

                public int getNewListSize() {
                    return SearchDownloadsContainer.this.rowCount;
                }

                public boolean areItemsTheSame(int i, int i2) {
                    MessageObject messageObject;
                    if (i >= 0 && i2 >= 0) {
                        if (i == i && i2 == SearchDownloadsContainer.this.downloadingFilesHeader) {
                            return true;
                        }
                        if (i == i4 && i2 == SearchDownloadsContainer.this.recentFilesHeader) {
                            return true;
                        }
                    }
                    int i3 = i2;
                    MessageObject messageObject2 = null;
                    if (i < i3 || i >= i3) {
                        int i4 = i5;
                        messageObject = (i < i4 || i >= i6) ? null : (MessageObject) arrayList4.get(i - i4);
                    } else {
                        messageObject = (MessageObject) arrayList3.get(i - i3);
                    }
                    SearchDownloadsContainer searchDownloadsContainer = SearchDownloadsContainer.this;
                    int i5 = searchDownloadsContainer.downloadingFilesStartRow;
                    if (i2 < i5 || i2 >= searchDownloadsContainer.downloadingFilesEndRow) {
                        int i6 = searchDownloadsContainer.recentFilesStartRow;
                        if (i2 >= i6 && i2 < searchDownloadsContainer.recentFilesEndRow) {
                            messageObject2 = searchDownloadsContainer.recentLoadingFiles.get(i2 - i6);
                        }
                    } else {
                        messageObject2 = searchDownloadsContainer.currentLoadingFiles.get(i2 - i5);
                    }
                    if (messageObject2 == null || messageObject == null || messageObject2.getDocument().id != messageObject.getDocument().id) {
                        return false;
                    }
                    return true;
                }

                public boolean areContentsTheSame(int i, int i2) {
                    return areItemsTheSame(i, i2);
                }
            }).dispatchUpdatesTo((RecyclerView.Adapter) this.adapter);
            for (int i8 = 0; i8 < this.recyclerListView.getChildCount(); i8++) {
                View childAt = this.recyclerListView.getChildAt(i8);
                int childAdapterPosition = this.recyclerListView.getChildAdapterPosition(childAt);
                if (childAdapterPosition >= 0 && (childViewHolder = this.recyclerListView.getChildViewHolder(childAt)) != null && !childViewHolder.shouldIgnore()) {
                    if (childAt instanceof GraySectionCell) {
                        this.adapter.onBindViewHolder(childViewHolder, childAdapterPosition);
                    } else if (childAt instanceof Cell) {
                        Cell cell = (Cell) childAt;
                        cell.sharedDocumentCell.updateFileExistIcon(true);
                        this.messageHashIdTmp.set(cell.sharedDocumentCell.getMessage().getId(), cell.sharedDocumentCell.getMessage().getDialogId());
                        cell.sharedDocumentCell.setChecked(this.uiCallback.isSelected(this.messageHashIdTmp), true);
                    }
                }
            }
            return;
        }
        updateRows(arrayList, arrayList2);
        this.adapter.notifyDataSetChanged();
    }

    private void updateRows(ArrayList<MessageObject> arrayList, ArrayList<MessageObject> arrayList2) {
        this.currentLoadingFiles.clear();
        this.currentLoadingFiles.addAll(arrayList);
        this.recentLoadingFiles.clear();
        this.recentLoadingFiles.addAll(arrayList2);
        int i = 0;
        this.rowCount = 0;
        this.downloadingFilesHeader = -1;
        this.downloadingFilesStartRow = -1;
        this.downloadingFilesEndRow = -1;
        this.recentFilesHeader = -1;
        this.recentFilesStartRow = -1;
        this.recentFilesEndRow = -1;
        this.hasCurrentDownload = false;
        if (!this.currentLoadingFiles.isEmpty()) {
            int i2 = this.rowCount;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.downloadingFilesHeader = i2;
            this.downloadingFilesStartRow = i3;
            int size = i3 + this.currentLoadingFiles.size();
            this.rowCount = size;
            this.downloadingFilesEndRow = size;
            while (true) {
                if (i >= this.currentLoadingFiles.size()) {
                    break;
                } else if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentLoadingFiles.get(i).getFileName())) {
                    this.hasCurrentDownload = true;
                    break;
                } else {
                    i++;
                }
            }
        }
        if (!this.recentLoadingFiles.isEmpty()) {
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.recentFilesHeader = i4;
            this.recentFilesStartRow = i5;
            int size2 = i5 + this.recentLoadingFiles.size();
            this.rowCount = size2;
            this.recentFilesEndRow = size2;
        }
    }

    public void search(String str) {
        this.searchQuery = str;
        update(false);
    }

    private class DownloadsAdapter extends RecyclerListView.SelectionAdapter {
        private DownloadsAdapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new GraySectionCell(viewGroup.getContext());
            } else if (i == 1) {
                view = new Cell(SearchDownloadsContainer.this, viewGroup.getContext());
            } else {
                view = new SharedAudioCell(this, viewGroup.getContext()) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        return MediaController.getInstance().playMessage(messageObject);
                    }
                };
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            int i2;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
                SearchDownloadsContainer searchDownloadsContainer = SearchDownloadsContainer.this;
                if (i == searchDownloadsContainer.downloadingFilesHeader) {
                    String string = LocaleController.getString("Downloading", NUM);
                    if (SearchDownloadsContainer.this.hasCurrentDownload) {
                        i2 = NUM;
                        str = "PauseAll";
                    } else {
                        i2 = NUM;
                        str = "ResumeAll";
                    }
                    graySectionCell.setText(string, LocaleController.getString(str, i2), new View.OnClickListener() {
                        public void onClick(View view) {
                            for (int i = 0; i < SearchDownloadsContainer.this.currentLoadingFiles.size(); i++) {
                                MessageObject messageObject = SearchDownloadsContainer.this.currentLoadingFiles.get(i);
                                if (SearchDownloadsContainer.this.hasCurrentDownload) {
                                    AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().cancelLoadFile(messageObject.getDocument());
                                } else {
                                    AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(messageObject.getDocument(), messageObject, 0, 0);
                                }
                            }
                            SearchDownloadsContainer.this.update(true);
                        }
                    });
                } else if (i == searchDownloadsContainer.recentFilesHeader) {
                    graySectionCell.setText(LocaleController.getString("RecentlyDownloaded", NUM), LocaleController.getString("Settings", NUM), new View.OnClickListener() {
                        public void onClick(View view) {
                            SearchDownloadsContainer.this.showSettingsDialog();
                        }
                    });
                }
            } else {
                MessageObject message = getMessage(i);
                if (message != null) {
                    boolean z = false;
                    if (itemViewType == 1) {
                        Cell cell = (Cell) viewHolder.itemView;
                        cell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                        int id = cell.sharedDocumentCell.getMessage() == null ? 0 : cell.sharedDocumentCell.getMessage().getId();
                        cell.sharedDocumentCell.setDocument(message, true);
                        SearchDownloadsContainer.this.messageHashIdTmp.set(cell.sharedDocumentCell.getMessage().getId(), cell.sharedDocumentCell.getMessage().getDialogId());
                        SharedDocumentCell sharedDocumentCell = cell.sharedDocumentCell;
                        SearchDownloadsContainer searchDownloadsContainer2 = SearchDownloadsContainer.this;
                        boolean isSelected = searchDownloadsContainer2.uiCallback.isSelected(searchDownloadsContainer2.messageHashIdTmp);
                        if (id == message.getId()) {
                            z = true;
                        }
                        sharedDocumentCell.setChecked(isSelected, z);
                    } else if (itemViewType == 2) {
                        SharedAudioCell sharedAudioCell = (SharedAudioCell) viewHolder.itemView;
                        sharedAudioCell.setMessageObject(message, true);
                        int id2 = sharedAudioCell.getMessage() == null ? 0 : sharedAudioCell.getMessage().getId();
                        SearchDownloadsContainer searchDownloadsContainer3 = SearchDownloadsContainer.this;
                        boolean isSelected2 = searchDownloadsContainer3.uiCallback.isSelected(searchDownloadsContainer3.messageHashIdTmp);
                        if (id2 == message.getId()) {
                            z = true;
                        }
                        sharedAudioCell.setChecked(isSelected2, z);
                    }
                }
            }
        }

        public int getItemViewType(int i) {
            SearchDownloadsContainer searchDownloadsContainer = SearchDownloadsContainer.this;
            if (i == searchDownloadsContainer.downloadingFilesHeader || i == searchDownloadsContainer.recentFilesHeader) {
                return 0;
            }
            MessageObject message = getMessage(i);
            if (message != null && message.isMusic()) {
                return 2;
            }
            return 1;
        }

        /* access modifiers changed from: private */
        public MessageObject getMessage(int i) {
            SearchDownloadsContainer searchDownloadsContainer = SearchDownloadsContainer.this;
            int i2 = searchDownloadsContainer.downloadingFilesStartRow;
            if (i >= i2 && i < searchDownloadsContainer.downloadingFilesEndRow) {
                return searchDownloadsContainer.currentLoadingFiles.get(i - i2);
            }
            int i3 = searchDownloadsContainer.recentFilesStartRow;
            if (i < i3 || i >= searchDownloadsContainer.recentFilesEndRow) {
                return null;
            }
            return searchDownloadsContainer.recentLoadingFiles.get(i - i3);
        }

        public int getItemCount() {
            return SearchDownloadsContainer.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 2;
        }
    }

    /* access modifiers changed from: private */
    public void showSettingsDialog() {
        if (this.parentFragment != null && this.parentActivity != null) {
            BottomSheet bottomSheet = new BottomSheet(this.parentActivity, false);
            Activity parentActivity2 = this.parentFragment.getParentActivity();
            LinearLayout linearLayout = new LinearLayout(parentActivity2);
            linearLayout.setOrientation(1);
            StickerImageView stickerImageView = new StickerImageView(parentActivity2, this.currentAccount);
            stickerImageView.setStickerNum(9);
            stickerImageView.getImageReceiver().setAutoRepeat(1);
            linearLayout.addView(stickerImageView, LayoutHelper.createLinear(144, 144, 1, 0, 16, 0, 0));
            TextView textView = new TextView(parentActivity2);
            textView.setGravity(1);
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            textView.setTextSize(1, 24.0f);
            textView.setText(LocaleController.getString("DownloadedFiles", NUM));
            linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 30.0f, 21.0f, 0.0f));
            TextView textView2 = new TextView(parentActivity2);
            textView2.setGravity(1);
            textView2.setTextSize(1, 15.0f);
            textView2.setTextColor(Theme.getColor("dialogTextHint"));
            textView2.setText(LocaleController.formatString("DownloadedFilesMessage", NUM, new Object[0]));
            linearLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
            TextView textView3 = new TextView(parentActivity2);
            textView3.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            textView3.setGravity(17);
            textView3.setTextSize(1, 14.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView3.setText(LocaleController.getString("ManageDeviceStorage", NUM));
            textView3.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            textView3.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), 120)));
            linearLayout.addView(textView3, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
            TextView textView4 = new TextView(parentActivity2);
            textView4.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            textView4.setGravity(17);
            textView4.setTextSize(1, 14.0f);
            textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView4.setText(LocaleController.getString("ClearDownloadsList", NUM));
            textView4.setTextColor(Theme.getColor("featuredStickers_addButton"));
            textView4.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor("featuredStickers_addButton"), 120)));
            linearLayout.addView(textView4, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 0.0f, 16.0f, 16.0f));
            NestedScrollView nestedScrollView = new NestedScrollView(parentActivity2);
            nestedScrollView.addView(linearLayout);
            bottomSheet.setCustomView(nestedScrollView);
            bottomSheet.show();
            textView3.setOnClickListener(new SearchDownloadsContainer$$ExternalSyntheticLambda1(this, bottomSheet));
            textView4.setOnClickListener(new SearchDownloadsContainer$$ExternalSyntheticLambda0(this, bottomSheet));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSettingsDialog$4(BottomSheet bottomSheet, View view) {
        bottomSheet.dismiss();
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            baseFragment.presentFragment(new CacheControlActivity());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSettingsDialog$5(BottomSheet bottomSheet, View view) {
        bottomSheet.dismiss();
        DownloadController.getInstance(this.currentAccount).clearRecentDownloadedFiles();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onDownloadingFilesChanged);
        if (getVisibility() == 0) {
            DownloadController.getInstance(this.currentAccount).clearUnviewedDownloads();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.onDownloadingFilesChanged);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.onDownloadingFilesChanged) {
            if (getVisibility() == 0) {
                DownloadController.getInstance(this.currentAccount).clearUnviewedDownloads();
            }
            update(true);
        }
    }

    private class Cell extends FrameLayout {
        SharedDocumentCell sharedDocumentCell;

        public Cell(SearchDownloadsContainer searchDownloadsContainer, Context context) {
            super(context);
            SharedDocumentCell sharedDocumentCell2 = new SharedDocumentCell(context, 2);
            this.sharedDocumentCell = sharedDocumentCell2;
            sharedDocumentCell2.rightDateTextView.setVisibility(8);
            addView(this.sharedDocumentCell);
        }
    }

    public void setUiCallback(FilteredSearchView.UiCallback uiCallback2) {
        this.uiCallback = uiCallback2;
    }

    public void setKeyboardHeight(int i, boolean z) {
        this.emptyView.setKeyboardHeight(i, z);
    }
}
