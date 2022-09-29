package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$Message;
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
    boolean checkingFilesExist;
    /* access modifiers changed from: private */
    public final int currentAccount;
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
        this.recyclerListView = new BlurredRecyclerView(getContext());
        new ItemTouchHelper(new TouchHelperCallback()).attachToRecyclerView(this.recyclerListView);
        addView(this.recyclerListView);
        this.recyclerListView.setLayoutManager(new LinearLayoutManager(this, baseFragment.getParentActivity()) {
            public boolean supportsPredictiveItemAnimations() {
                return true;
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
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SearchDownloadsContainer$$ExternalSyntheticLambda6(this, i));
        this.recyclerListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new SearchDownloadsContainer$$ExternalSyntheticLambda7(this));
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
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, View view, int i2) {
        TLRPC$Chat chat;
        MessageObject access$500 = this.adapter.getMessage(i2);
        if (access$500 != null) {
            boolean z = false;
            if (this.uiCallback.actionModeShowing()) {
                this.uiCallback.toggleItemSelection(access$500, view, 0);
                this.messageHashIdTmp.set(access$500.getId(), access$500.getDialogId());
                this.adapter.notifyItemChanged(i2);
                if (!this.uiCallback.actionModeShowing()) {
                    DownloadsAdapter downloadsAdapter = this.adapter;
                    downloadsAdapter.notifyItemRangeChanged(0, downloadsAdapter.getItemCount());
                    return;
                }
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
                    }
                    boolean canPreviewDocument = message.canPreviewDocument();
                    if (!canPreviewDocument) {
                        TLRPC$Message tLRPC$Message = message.messageOwner;
                        boolean z2 = tLRPC$Message != null && tLRPC$Message.noforwards;
                        if (message.isFromChat() && (chat = MessagesController.getInstance(i).getChat(Long.valueOf(-message.getFromChatId()))) != null) {
                            z2 = chat.noforwards;
                        }
                        if (canPreviewDocument || z2) {
                            z = true;
                        }
                        canPreviewDocument = z;
                    }
                    if (canPreviewDocument) {
                        PhotoViewer.getInstance().setParentActivity(this.parentFragment);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(message);
                        PhotoViewer.getInstance().setParentActivity(this.parentFragment);
                        PhotoViewer.getInstance().openPhoto((ArrayList<MessageObject>) arrayList, 0, 0, 0, (PhotoViewer.PhotoViewerProvider) new PhotoViewer.EmptyPhotoViewerProvider());
                        return;
                    }
                    AndroidUtilities.openDocument(message, this.parentActivity, this.parentFragment);
                } else if (!sharedDocumentCell.isLoading()) {
                    access$500.putInDownloadsStore = true;
                    AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(document, access$500, 0, 0);
                    sharedDocumentCell.updateFileExistIcon(true);
                    DownloadController.getInstance(i).updateFilesLoadingPriority();
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
        MessageObject access$500 = this.adapter.getMessage(i);
        if (access$500 == null) {
            return false;
        }
        if (!this.uiCallback.actionModeShowing()) {
            this.uiCallback.showActionMode();
            DownloadsAdapter downloadsAdapter = this.adapter;
            downloadsAdapter.notifyItemRangeChanged(0, downloadsAdapter.getItemCount());
        }
        if (!this.uiCallback.actionModeShowing()) {
            return true;
        }
        this.uiCallback.toggleItemSelection(access$500, view, 0);
        if (!this.uiCallback.actionModeShowing()) {
            DownloadsAdapter downloadsAdapter2 = this.adapter;
            downloadsAdapter2.notifyItemRangeChanged(0, downloadsAdapter2.getItemCount());
        }
        this.messageHashIdTmp.set(access$500.getId(), access$500.getDialogId());
        return true;
    }

    private void checkFilesExist() {
        if (!this.checkingFilesExist) {
            this.checkingFilesExist = true;
            Utilities.searchQueue.postRunnable(new SearchDownloadsContainer$$ExternalSyntheticLambda2(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkFilesExist$3() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(arrayList);
        FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(arrayList2);
        for (int i = 0; i < arrayList.size(); i++) {
            if (FileLoader.getInstance(this.currentAccount).getPathToMessage(((MessageObject) arrayList.get(i)).messageOwner).exists()) {
                arrayList3.add((MessageObject) arrayList.get(i));
            }
        }
        for (int i2 = 0; i2 < arrayList2.size(); i2++) {
            if (!FileLoader.getInstance(this.currentAccount).getPathToMessage(((MessageObject) arrayList2.get(i2)).messageOwner).exists()) {
                arrayList4.add((MessageObject) arrayList2.get(i2));
            }
        }
        AndroidUtilities.runOnUIThread(new SearchDownloadsContainer$$ExternalSyntheticLambda5(this, arrayList3, arrayList4));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkFilesExist$2(ArrayList arrayList, ArrayList arrayList2) {
        for (int i = 0; i < arrayList.size(); i++) {
            DownloadController.getInstance(this.currentAccount).onDownloadComplete((MessageObject) arrayList.get(i));
        }
        if (!arrayList2.isEmpty()) {
            DownloadController.getInstance(this.currentAccount).deleteRecentFiles(arrayList2);
        }
        this.checkingFilesExist = false;
        update(true);
    }

    public void update(boolean z) {
        DownloadsAdapter downloadsAdapter = this.adapter;
        downloadsAdapter.notifyItemRangeChanged(0, downloadsAdapter.getItemCount());
        if (TextUtils.isEmpty(this.searchQuery) || isEmptyDownloads()) {
            if (this.rowCount == 0) {
                this.itemsEnterAnimator.showItemsAnimated(0);
            }
            if (this.checkingFilesExist) {
                this.currentLoadingFilesTmp.clear();
                this.recentLoadingFilesTmp.clear();
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
                this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewDownloads", R.string.SearchEmptyViewDownloads));
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
        SearchDownloadsContainer$$ExternalSyntheticLambda4 searchDownloadsContainer$$ExternalSyntheticLambda4 = new SearchDownloadsContainer$$ExternalSyntheticLambda4(this, arrayList, lowerCase, arrayList2);
        this.lastSearchRunnable = searchDownloadsContainer$$ExternalSyntheticLambda4;
        dispatchQueue.postRunnable(searchDownloadsContainer$$ExternalSyntheticLambda4, equals ? 0 : 300);
        this.recentLoadingFilesTmp.clear();
        this.currentLoadingFilesTmp.clear();
        if (!equals) {
            this.emptyView.showProgress(true, true);
            updateListInternal(z, this.currentLoadingFilesTmp, this.recentLoadingFilesTmp);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$update$5(ArrayList arrayList, String str, ArrayList arrayList2) {
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
        AndroidUtilities.runOnUIThread(new SearchDownloadsContainer$$ExternalSyntheticLambda3(this, str, arrayList3, arrayList4));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$update$4(String str, ArrayList arrayList, ArrayList arrayList2) {
        if (str.equals(this.lastQueryString)) {
            if (this.rowCount == 0) {
                this.itemsEnterAnimator.showItemsAnimated(0);
            }
            updateListInternal(true, arrayList, arrayList2);
            if (this.rowCount == 0) {
                this.emptyView.showProgress(false, true);
                this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", R.string.SearchEmptyViewTitle2));
                this.emptyView.subtitle.setVisibility(0);
                this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
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
        Iterator<MessageObject> it = arrayList.iterator();
        while (it.hasNext()) {
            MessageObject next = it.next();
            if (!next.isRoundVideo() && !next.isVoice()) {
                this.currentLoadingFiles.add(next);
            }
        }
        this.recentLoadingFiles.clear();
        Iterator<MessageObject> it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            MessageObject next2 = it2.next();
            if (!next2.isRoundVideo() && !next2.isVoice()) {
                this.recentLoadingFiles.add(next2);
            }
        }
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

        /* JADX WARNING: Removed duplicated region for block: B:30:0x00a4  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x010e  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            /*
                r8 = this;
                int r0 = r9.getItemViewType()
                if (r0 != 0) goto L_0x0083
                android.view.View r9 = r9.itemView
                org.telegram.ui.Cells.GraySectionCell r9 = (org.telegram.ui.Cells.GraySectionCell) r9
                org.telegram.ui.Components.SearchDownloadsContainer r0 = org.telegram.ui.Components.SearchDownloadsContainer.this
                int r1 = r0.downloadingFilesHeader
                if (r10 != r1) goto L_0x0065
                int r10 = org.telegram.messenger.R.string.Downloading
                java.lang.String r0 = "Downloading"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r0, r10)
                java.lang.CharSequence r0 = r9.getText()
                boolean r0 = r0.equals(r10)
                java.lang.String r1 = "PauseAll"
                java.lang.String r2 = "ResumeAll"
                if (r0 == 0) goto L_0x0046
                org.telegram.ui.Components.SearchDownloadsContainer r10 = org.telegram.ui.Components.SearchDownloadsContainer.this
                boolean r10 = r10.hasCurrentDownload
                if (r10 == 0) goto L_0x0035
                int r10 = org.telegram.messenger.R.string.PauseAll
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r1, r10)
                goto L_0x003b
            L_0x0035:
                int r10 = org.telegram.messenger.R.string.ResumeAll
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r2, r10)
            L_0x003b:
                org.telegram.ui.Components.SearchDownloadsContainer r0 = org.telegram.ui.Components.SearchDownloadsContainer.this
                boolean r0 = r0.hasCurrentDownload
                r9.setRightText(r10, r0)
                goto L_0x0163
            L_0x0046:
                org.telegram.ui.Components.SearchDownloadsContainer r0 = org.telegram.ui.Components.SearchDownloadsContainer.this
                boolean r0 = r0.hasCurrentDownload
                if (r0 == 0) goto L_0x0055
                int r0 = org.telegram.messenger.R.string.PauseAll
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                goto L_0x005b
            L_0x0055:
                int r0 = org.telegram.messenger.R.string.ResumeAll
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            L_0x005b:
                org.telegram.ui.Components.SearchDownloadsContainer$DownloadsAdapter$2 r1 = new org.telegram.ui.Components.SearchDownloadsContainer$DownloadsAdapter$2
                r1.<init>()
                r9.setText(r10, r0, r1)
                goto L_0x0163
            L_0x0065:
                int r0 = r0.recentFilesHeader
                if (r10 != r0) goto L_0x0163
                int r10 = org.telegram.messenger.R.string.RecentlyDownloaded
                java.lang.String r0 = "RecentlyDownloaded"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r0, r10)
                int r0 = org.telegram.messenger.R.string.Settings
                java.lang.String r1 = "Settings"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                org.telegram.ui.Components.SearchDownloadsContainer$DownloadsAdapter$3 r1 = new org.telegram.ui.Components.SearchDownloadsContainer$DownloadsAdapter$3
                r1.<init>()
                r9.setText(r10, r0, r1)
                goto L_0x0163
            L_0x0083:
                org.telegram.messenger.MessageObject r1 = r8.getMessage(r10)
                if (r1 == 0) goto L_0x0163
                org.telegram.ui.Components.SearchDownloadsContainer r2 = org.telegram.ui.Components.SearchDownloadsContainer.this
                org.telegram.ui.FilteredSearchView$UiCallback r2 = r2.uiCallback
                boolean r2 = r2.actionModeShowing()
                r3 = 0
                r4 = 1
                if (r2 == 0) goto L_0x00a1
                org.telegram.ui.Components.SearchDownloadsContainer r2 = org.telegram.ui.Components.SearchDownloadsContainer.this
                int r5 = r2.downloadingFilesStartRow
                if (r10 < r5) goto L_0x00a1
                int r2 = r2.downloadingFilesEndRow
                if (r10 >= r2) goto L_0x00a1
                r10 = 1
                goto L_0x00a2
            L_0x00a1:
                r10 = 0
            L_0x00a2:
                if (r0 != r4) goto L_0x010e
                android.view.View r9 = r9.itemView
                org.telegram.ui.Components.SearchDownloadsContainer$Cell r9 = (org.telegram.ui.Components.SearchDownloadsContainer.Cell) r9
                java.lang.String r0 = "windowBackgroundWhite"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r9.setBackgroundColor(r0)
                org.telegram.ui.Cells.SharedDocumentCell r0 = r9.sharedDocumentCell
                org.telegram.messenger.MessageObject r0 = r0.getMessage()
                if (r0 != 0) goto L_0x00bb
                r0 = 0
                goto L_0x00c5
            L_0x00bb:
                org.telegram.ui.Cells.SharedDocumentCell r0 = r9.sharedDocumentCell
                org.telegram.messenger.MessageObject r0 = r0.getMessage()
                int r0 = r0.getId()
            L_0x00c5:
                org.telegram.ui.Cells.SharedDocumentCell r2 = r9.sharedDocumentCell
                r2.setDocument(r1, r4)
                org.telegram.ui.Components.SearchDownloadsContainer r2 = org.telegram.ui.Components.SearchDownloadsContainer.this
                org.telegram.ui.FilteredSearchView$MessageHashId r2 = r2.messageHashIdTmp
                org.telegram.ui.Cells.SharedDocumentCell r5 = r9.sharedDocumentCell
                org.telegram.messenger.MessageObject r5 = r5.getMessage()
                int r5 = r5.getId()
                org.telegram.ui.Cells.SharedDocumentCell r6 = r9.sharedDocumentCell
                org.telegram.messenger.MessageObject r6 = r6.getMessage()
                long r6 = r6.getDialogId()
                r2.set(r5, r6)
                org.telegram.ui.Cells.SharedDocumentCell r2 = r9.sharedDocumentCell
                org.telegram.ui.Components.SearchDownloadsContainer r5 = org.telegram.ui.Components.SearchDownloadsContainer.this
                org.telegram.ui.FilteredSearchView$UiCallback r6 = r5.uiCallback
                org.telegram.ui.FilteredSearchView$MessageHashId r5 = r5.messageHashIdTmp
                boolean r5 = r6.isSelected(r5)
                int r6 = r1.getId()
                if (r0 != r6) goto L_0x00fd
                r6 = 1
                goto L_0x00fe
            L_0x00fd:
                r6 = 0
            L_0x00fe:
                r2.setChecked(r5, r6)
                org.telegram.ui.Cells.SharedDocumentCell r9 = r9.sharedDocumentCell
                int r1 = r1.getId()
                if (r0 != r1) goto L_0x010a
                r3 = 1
            L_0x010a:
                r9.showReorderIcon(r10, r3)
                goto L_0x0163
            L_0x010e:
                r2 = 2
                if (r0 != r2) goto L_0x0163
                android.view.View r9 = r9.itemView
                org.telegram.ui.Cells.SharedAudioCell r9 = (org.telegram.ui.Cells.SharedAudioCell) r9
                org.telegram.messenger.MessageObject r0 = r9.getMessage()
                if (r0 != 0) goto L_0x011d
                r0 = 0
                goto L_0x0125
            L_0x011d:
                org.telegram.messenger.MessageObject r0 = r9.getMessage()
                int r0 = r0.getId()
            L_0x0125:
                r9.setMessageObject(r1, r4)
                org.telegram.ui.Components.SearchDownloadsContainer r2 = org.telegram.ui.Components.SearchDownloadsContainer.this
                org.telegram.ui.FilteredSearchView$MessageHashId r2 = r2.messageHashIdTmp
                org.telegram.messenger.MessageObject r5 = r9.getMessage()
                int r5 = r5.getId()
                org.telegram.messenger.MessageObject r6 = r9.getMessage()
                long r6 = r6.getDialogId()
                r2.set(r5, r6)
                org.telegram.ui.Components.SearchDownloadsContainer r2 = org.telegram.ui.Components.SearchDownloadsContainer.this
                org.telegram.ui.FilteredSearchView$UiCallback r5 = r2.uiCallback
                org.telegram.ui.FilteredSearchView$MessageHashId r2 = r2.messageHashIdTmp
                boolean r2 = r5.isSelected(r2)
                int r5 = r1.getId()
                if (r0 != r5) goto L_0x0155
                r5 = 1
                goto L_0x0156
            L_0x0155:
                r5 = 0
            L_0x0156:
                r9.setChecked(r2, r5)
                int r1 = r1.getId()
                if (r0 != r1) goto L_0x0160
                r3 = 1
            L_0x0160:
                r9.showReorderIcon(r10, r3)
            L_0x0163:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SearchDownloadsContainer.DownloadsAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
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
            textView.setText(LocaleController.getString("DownloadedFiles", R.string.DownloadedFiles));
            linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 30.0f, 21.0f, 0.0f));
            TextView textView2 = new TextView(parentActivity2);
            textView2.setGravity(1);
            textView2.setTextSize(1, 15.0f);
            textView2.setTextColor(Theme.getColor("dialogTextHint"));
            textView2.setText(LocaleController.formatString("DownloadedFilesMessage", R.string.DownloadedFilesMessage, new Object[0]));
            linearLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
            TextView textView3 = new TextView(parentActivity2);
            textView3.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            textView3.setGravity(17);
            textView3.setTextSize(1, 14.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView3.setText(LocaleController.getString("ManageDeviceStorage", R.string.ManageDeviceStorage));
            textView3.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            textView3.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), 120)));
            linearLayout.addView(textView3, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
            TextView textView4 = new TextView(parentActivity2);
            textView4.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            textView4.setGravity(17);
            textView4.setTextSize(1, 14.0f);
            textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView4.setText(LocaleController.getString("ClearDownloadsList", R.string.ClearDownloadsList));
            textView4.setTextColor(Theme.getColor("featuredStickers_addButton"));
            textView4.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor("featuredStickers_addButton"), 120)));
            linearLayout.addView(textView4, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 0.0f, 16.0f, 16.0f));
            NestedScrollView nestedScrollView = new NestedScrollView(parentActivity2);
            nestedScrollView.addView(linearLayout);
            bottomSheet.setCustomView(nestedScrollView);
            bottomSheet.show();
            if (Build.VERSION.SDK_INT >= 23) {
                AndroidUtilities.setLightStatusBar(bottomSheet.getWindow(), !Theme.isCurrentThemeDark());
                AndroidUtilities.setLightNavigationBar(bottomSheet.getWindow(), !Theme.isCurrentThemeDark());
            }
            textView3.setOnClickListener(new SearchDownloadsContainer$$ExternalSyntheticLambda0(this, bottomSheet));
            textView4.setOnClickListener(new SearchDownloadsContainer$$ExternalSyntheticLambda1(this, bottomSheet));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSettingsDialog$6(BottomSheet bottomSheet, View view) {
        bottomSheet.dismiss();
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            baseFragment.presentFragment(new CacheControlActivity());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSettingsDialog$7(BottomSheet bottomSheet, View view) {
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
        checkFilesExist();
        update(false);
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.sharedDocumentCell.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        }
    }

    public void setUiCallback(FilteredSearchView.UiCallback uiCallback2) {
        this.uiCallback = uiCallback2;
    }

    public void setKeyboardHeight(int i, boolean z) {
        this.emptyView.setKeyboardHeight(i, z);
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return SearchDownloadsContainer.this.uiCallback.actionModeShowing();
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (!(viewHolder.getAdapterPosition() >= SearchDownloadsContainer.this.downloadingFilesStartRow && viewHolder.getAdapterPosition() < SearchDownloadsContainer.this.downloadingFilesEndRow)) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (!(viewHolder2.getAdapterPosition() >= SearchDownloadsContainer.this.downloadingFilesStartRow && viewHolder2.getAdapterPosition() < SearchDownloadsContainer.this.downloadingFilesEndRow)) {
                return false;
            }
            int adapterPosition = viewHolder.getAdapterPosition();
            int adapterPosition2 = viewHolder2.getAdapterPosition();
            SearchDownloadsContainer searchDownloadsContainer = SearchDownloadsContainer.this;
            int i = searchDownloadsContainer.downloadingFilesStartRow;
            int i2 = adapterPosition - i;
            int i3 = adapterPosition2 - i;
            searchDownloadsContainer.currentLoadingFiles.indexOf(Integer.valueOf(adapterPosition - i));
            SearchDownloadsContainer searchDownloadsContainer2 = SearchDownloadsContainer.this;
            searchDownloadsContainer2.currentLoadingFiles.get(adapterPosition - searchDownloadsContainer2.downloadingFilesStartRow);
            MessageObject messageObject = SearchDownloadsContainer.this.currentLoadingFiles.get(i2);
            MessageObject messageObject2 = SearchDownloadsContainer.this.currentLoadingFiles.get(i3);
            SearchDownloadsContainer.this.currentLoadingFiles.set(i2, messageObject2);
            SearchDownloadsContainer.this.currentLoadingFiles.set(i3, messageObject);
            DownloadController.getInstance(SearchDownloadsContainer.this.currentAccount).swapLoadingPriority(messageObject, messageObject2);
            SearchDownloadsContainer.this.adapter.notifyItemMoved(adapterPosition, adapterPosition2);
            return false;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                SearchDownloadsContainer.this.recyclerListView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }
}
