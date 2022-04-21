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
import org.telegram.tgnet.TLRPC;
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

    public SearchDownloadsContainer(BaseFragment fragment, int currentAccount2) {
        super(fragment.getParentActivity());
        this.parentFragment = fragment;
        this.parentActivity = fragment.getParentActivity();
        this.currentAccount = currentAccount2;
        BlurredRecyclerView blurredRecyclerView = new BlurredRecyclerView(getContext());
        this.recyclerListView = blurredRecyclerView;
        addView(blurredRecyclerView);
        this.recyclerListView.setLayoutManager(new LinearLayoutManager(fragment.getParentActivity()) {
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        });
        this.recyclerListView.setAdapter(this.adapter);
        this.recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(SearchDownloadsContainer.this.parentActivity.getCurrentFocus());
                }
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SearchDownloadsContainer$$ExternalSyntheticLambda6(this));
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
        FileLoader.getInstance(currentAccount2).getCurrentLoadingFiles(this.currentLoadingFiles);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-SearchDownloadsContainer  reason: not valid java name */
    public /* synthetic */ void m4310lambda$new$0$orgtelegramuiComponentsSearchDownloadsContainer(View view, int position) {
        MessageObject messageObject = this.adapter.getMessage(position);
        if (messageObject != null) {
            if (this.uiCallback.actionModeShowing()) {
                this.uiCallback.toggleItemSelection(messageObject, view, 0);
                this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
                this.adapter.notifyItemChanged(position);
                return;
            }
            if (view instanceof Cell) {
                SharedDocumentCell cell = ((Cell) view).sharedDocumentCell;
                MessageObject message = cell.getMessage();
                TLRPC.Document document = message.getDocument();
                if (cell.isLoaded()) {
                    if (message.isRoundVideo() || message.isVoice()) {
                        MediaController.getInstance().playMessage(message);
                        return;
                    } else if (message.canPreviewDocument()) {
                        PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                        ArrayList<MessageObject> documents = new ArrayList<>();
                        documents.add(message);
                        PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                        PhotoViewer.getInstance().openPhoto(documents, 0, 0, 0, (PhotoViewer.PhotoViewerProvider) new PhotoViewer.EmptyPhotoViewerProvider());
                        return;
                    } else {
                        AndroidUtilities.openDocument(message, this.parentActivity, this.parentFragment);
                    }
                } else if (!cell.isLoading()) {
                    messageObject.putInDownloadsStore = true;
                    AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(document, messageObject, 0, 0);
                    cell.updateFileExistIcon(true);
                } else {
                    AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().cancelLoadFile(document);
                    cell.updateFileExistIcon(true);
                }
                update(true);
            }
            if (view instanceof SharedAudioCell) {
                ((SharedAudioCell) view).didPressedButton();
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-SearchDownloadsContainer  reason: not valid java name */
    public /* synthetic */ boolean m4311lambda$new$1$orgtelegramuiComponentsSearchDownloadsContainer(View view, int position) {
        MessageObject messageObject = this.adapter.getMessage(position);
        if (messageObject == null) {
            return false;
        }
        if (!this.uiCallback.actionModeShowing()) {
            this.uiCallback.showActionMode();
        }
        if (!this.uiCallback.actionModeShowing()) {
            return true;
        }
        this.uiCallback.toggleItemSelection(messageObject, view, 0);
        this.messageHashIdTmp.set(messageObject.getId(), messageObject.getDialogId());
        this.adapter.notifyItemChanged(position);
        return true;
    }

    private void checkFilesExist() {
        if (!this.checkingFilesExist) {
            this.checkingFilesExist = true;
            Utilities.searchQueue.postRunnable(new SearchDownloadsContainer$$ExternalSyntheticLambda2(this));
        }
    }

    /* renamed from: lambda$checkFilesExist$3$org-telegram-ui-Components-SearchDownloadsContainer  reason: not valid java name */
    public /* synthetic */ void m4309x6715ce55() {
        ArrayList<MessageObject> currentLoadingFiles2 = new ArrayList<>();
        ArrayList<MessageObject> recentLoadingFiles2 = new ArrayList<>();
        ArrayList<MessageObject> moveToRecent = new ArrayList<>();
        ArrayList<MessageObject> removeFromRecent = new ArrayList<>();
        FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(currentLoadingFiles2);
        FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(recentLoadingFiles2);
        for (int i = 0; i < currentLoadingFiles2.size(); i++) {
            if (FileLoader.getPathToMessage(currentLoadingFiles2.get(i).messageOwner).exists()) {
                moveToRecent.add(currentLoadingFiles2.get(i));
            }
        }
        for (int i2 = 0; i2 < recentLoadingFiles2.size(); i2++) {
            if (!FileLoader.getPathToMessage(recentLoadingFiles2.get(i2).messageOwner).exists()) {
                removeFromRecent.add(recentLoadingFiles2.get(i2));
            }
        }
        AndroidUtilities.runOnUIThread(new SearchDownloadsContainer$$ExternalSyntheticLambda5(this, moveToRecent, removeFromRecent));
    }

    /* renamed from: lambda$checkFilesExist$2$org-telegram-ui-Components-SearchDownloadsContainer  reason: not valid java name */
    public /* synthetic */ void m4308xd9db1cd4(ArrayList moveToRecent, ArrayList removeFromRecent) {
        for (int i = 0; i < moveToRecent.size(); i++) {
            DownloadController.getInstance(this.currentAccount).onDownloadComplete((MessageObject) moveToRecent.get(i));
        }
        if (removeFromRecent.isEmpty() == 0) {
            DownloadController.getInstance(this.currentAccount).deleteRecentFiles(removeFromRecent);
        }
        this.checkingFilesExist = false;
        update(true);
    }

    public void update(boolean animated) {
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
            updateListInternal(animated, this.currentLoadingFilesTmp, this.recentLoadingFilesTmp);
            if (this.rowCount == 0) {
                this.emptyView.showProgress(false, false);
                this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewDownloads", NUM));
                this.emptyView.subtitle.setVisibility(8);
            }
            this.emptyView.setStickerType(9);
            return;
        }
        this.emptyView.setStickerType(1);
        ArrayList<MessageObject> currentLoadingFilesTmp2 = new ArrayList<>();
        ArrayList<MessageObject> recentLoadingFilesTmp2 = new ArrayList<>();
        FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(currentLoadingFilesTmp2);
        FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(recentLoadingFilesTmp2);
        String q = this.searchQuery.toLowerCase();
        boolean sameQuery = q.equals(this.lastQueryString);
        this.lastQueryString = q;
        Utilities.searchQueue.cancelRunnable(this.lastSearchRunnable);
        DispatchQueue dispatchQueue = Utilities.searchQueue;
        SearchDownloadsContainer$$ExternalSyntheticLambda4 searchDownloadsContainer$$ExternalSyntheticLambda4 = new SearchDownloadsContainer$$ExternalSyntheticLambda4(this, currentLoadingFilesTmp2, q, recentLoadingFilesTmp2);
        this.lastSearchRunnable = searchDownloadsContainer$$ExternalSyntheticLambda4;
        dispatchQueue.postRunnable(searchDownloadsContainer$$ExternalSyntheticLambda4, sameQuery ? 0 : 300);
        this.recentLoadingFilesTmp.clear();
        this.currentLoadingFilesTmp.clear();
        if (!sameQuery) {
            this.emptyView.showProgress(true, true);
            updateListInternal(animated, this.currentLoadingFilesTmp, this.recentLoadingFilesTmp);
        }
    }

    /* renamed from: lambda$update$5$org-telegram-ui-Components-SearchDownloadsContainer  reason: not valid java name */
    public /* synthetic */ void m4315x22f5var_(ArrayList currentLoadingFilesTmp2, String q, ArrayList recentLoadingFilesTmp2) {
        ArrayList<MessageObject> currentLoadingFilesRes = new ArrayList<>();
        ArrayList<MessageObject> recentLoadingFilesRes = new ArrayList<>();
        for (int i = 0; i < currentLoadingFilesTmp2.size(); i++) {
            if (FileLoader.getDocumentFileName(((MessageObject) currentLoadingFilesTmp2.get(i)).getDocument()).toLowerCase().contains(q)) {
                MessageObject messageObject = new MessageObject(this.currentAccount, ((MessageObject) currentLoadingFilesTmp2.get(i)).messageOwner, false, false);
                messageObject.mediaExists = ((MessageObject) currentLoadingFilesTmp2.get(i)).mediaExists;
                messageObject.setQuery(this.searchQuery);
                currentLoadingFilesRes.add(messageObject);
            }
        }
        for (int i2 = 0; i2 < recentLoadingFilesTmp2.size(); i2++) {
            if (FileLoader.getDocumentFileName(((MessageObject) recentLoadingFilesTmp2.get(i2)).getDocument()).toLowerCase().contains(q)) {
                MessageObject messageObject2 = new MessageObject(this.currentAccount, ((MessageObject) recentLoadingFilesTmp2.get(i2)).messageOwner, false, false);
                messageObject2.mediaExists = ((MessageObject) recentLoadingFilesTmp2.get(i2)).mediaExists;
                messageObject2.setQuery(this.searchQuery);
                recentLoadingFilesRes.add(messageObject2);
            }
        }
        AndroidUtilities.runOnUIThread(new SearchDownloadsContainer$$ExternalSyntheticLambda3(this, q, currentLoadingFilesRes, recentLoadingFilesRes));
    }

    /* renamed from: lambda$update$4$org-telegram-ui-Components-SearchDownloadsContainer  reason: not valid java name */
    public /* synthetic */ void m4314x95bb3ed1(String q, ArrayList currentLoadingFilesRes, ArrayList recentLoadingFilesRes) {
        if (q.equals(this.lastQueryString)) {
            if (this.rowCount == 0) {
                this.itemsEnterAnimator.showItemsAnimated(0);
            }
            updateListInternal(true, currentLoadingFilesRes, recentLoadingFilesRes);
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

    private void updateListInternal(boolean animated, ArrayList<MessageObject> currentLoadingFilesTmp2, ArrayList<MessageObject> recentLoadingFilesTmp2) {
        ArrayList<MessageObject> oldDownloadingLoadingFiles;
        ArrayList<MessageObject> arrayList = currentLoadingFilesTmp2;
        ArrayList<MessageObject> arrayList2 = recentLoadingFilesTmp2;
        if (animated) {
            int oldDownloadingFilesHeader = this.downloadingFilesHeader;
            int oldDownloadingFilesStartRow = this.downloadingFilesStartRow;
            int oldDownloadingFilesEndRow = this.downloadingFilesEndRow;
            int oldRecentFilesHeader = this.recentFilesHeader;
            int oldRecentFilesStartRow = this.recentFilesStartRow;
            int oldRecentFilesEndRow = this.recentFilesEndRow;
            int oldRowCount = this.rowCount;
            final ArrayList<MessageObject> oldDownloadingLoadingFiles2 = new ArrayList<>(this.currentLoadingFiles);
            final ArrayList<MessageObject> oldRecentLoadingFiles = new ArrayList<>(this.recentLoadingFiles);
            updateRows(arrayList, arrayList2);
            final int i = oldRowCount;
            final int i2 = oldDownloadingFilesHeader;
            int i3 = oldRowCount;
            final int oldRowCount2 = oldRecentFilesHeader;
            int oldRecentFilesEndRow2 = oldRecentFilesEndRow;
            final int oldRecentFilesEndRow3 = oldDownloadingFilesStartRow;
            int oldRecentFilesStartRow2 = oldRecentFilesStartRow;
            final int oldRecentFilesStartRow3 = oldDownloadingFilesEndRow;
            int i4 = oldRecentFilesHeader;
            final int oldRecentFilesHeader2 = oldRecentFilesStartRow2;
            int i5 = oldDownloadingFilesEndRow;
            final int oldDownloadingFilesEndRow2 = oldRecentFilesEndRow2;
            DiffUtil.calculateDiff(new DiffUtil.Callback() {
                public int getOldListSize() {
                    return i;
                }

                public int getNewListSize() {
                    return SearchDownloadsContainer.this.rowCount;
                }

                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v18, resolved type: java.lang.Object} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: org.telegram.messenger.MessageObject} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v20, resolved type: java.lang.Object} */
                /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.messenger.MessageObject} */
                /* JADX WARNING: Multi-variable type inference failed */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public boolean areItemsTheSame(int r10, int r11) {
                    /*
                        r9 = this;
                        r0 = 1
                        if (r10 < 0) goto L_0x001b
                        if (r11 < 0) goto L_0x001b
                        int r1 = r3
                        if (r10 != r1) goto L_0x0010
                        org.telegram.ui.Components.SearchDownloadsContainer r1 = org.telegram.ui.Components.SearchDownloadsContainer.this
                        int r1 = r1.downloadingFilesHeader
                        if (r11 != r1) goto L_0x0010
                        return r0
                    L_0x0010:
                        int r1 = r4
                        if (r10 != r1) goto L_0x001b
                        org.telegram.ui.Components.SearchDownloadsContainer r1 = org.telegram.ui.Components.SearchDownloadsContainer.this
                        int r1 = r1.recentFilesHeader
                        if (r11 != r1) goto L_0x001b
                        return r0
                    L_0x001b:
                        r1 = 0
                        r2 = 0
                        int r3 = r5
                        if (r10 < r3) goto L_0x0031
                        int r4 = r6
                        if (r10 >= r4) goto L_0x0031
                        java.util.ArrayList r4 = r7
                        int r3 = r10 - r3
                        java.lang.Object r3 = r4.get(r3)
                        r1 = r3
                        org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
                        goto L_0x0044
                    L_0x0031:
                        int r3 = r8
                        if (r10 < r3) goto L_0x0044
                        int r4 = r9
                        if (r10 >= r4) goto L_0x0044
                        java.util.ArrayList r4 = r10
                        int r3 = r10 - r3
                        java.lang.Object r3 = r4.get(r3)
                        r1 = r3
                        org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
                    L_0x0044:
                        org.telegram.ui.Components.SearchDownloadsContainer r3 = org.telegram.ui.Components.SearchDownloadsContainer.this
                        int r3 = r3.downloadingFilesStartRow
                        if (r11 < r3) goto L_0x0062
                        org.telegram.ui.Components.SearchDownloadsContainer r3 = org.telegram.ui.Components.SearchDownloadsContainer.this
                        int r3 = r3.downloadingFilesEndRow
                        if (r11 >= r3) goto L_0x0062
                        org.telegram.ui.Components.SearchDownloadsContainer r3 = org.telegram.ui.Components.SearchDownloadsContainer.this
                        java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r3.currentLoadingFiles
                        org.telegram.ui.Components.SearchDownloadsContainer r4 = org.telegram.ui.Components.SearchDownloadsContainer.this
                        int r4 = r4.downloadingFilesStartRow
                        int r4 = r11 - r4
                        java.lang.Object r3 = r3.get(r4)
                        r2 = r3
                        org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
                        goto L_0x007f
                    L_0x0062:
                        org.telegram.ui.Components.SearchDownloadsContainer r3 = org.telegram.ui.Components.SearchDownloadsContainer.this
                        int r3 = r3.recentFilesStartRow
                        if (r11 < r3) goto L_0x007f
                        org.telegram.ui.Components.SearchDownloadsContainer r3 = org.telegram.ui.Components.SearchDownloadsContainer.this
                        int r3 = r3.recentFilesEndRow
                        if (r11 >= r3) goto L_0x007f
                        org.telegram.ui.Components.SearchDownloadsContainer r3 = org.telegram.ui.Components.SearchDownloadsContainer.this
                        java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r3.recentLoadingFiles
                        org.telegram.ui.Components.SearchDownloadsContainer r4 = org.telegram.ui.Components.SearchDownloadsContainer.this
                        int r4 = r4.recentFilesStartRow
                        int r4 = r11 - r4
                        java.lang.Object r3 = r3.get(r4)
                        r2 = r3
                        org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
                    L_0x007f:
                        r3 = 0
                        if (r2 == 0) goto L_0x0097
                        if (r1 == 0) goto L_0x0097
                        org.telegram.tgnet.TLRPC$Document r4 = r2.getDocument()
                        long r4 = r4.id
                        org.telegram.tgnet.TLRPC$Document r6 = r1.getDocument()
                        long r6 = r6.id
                        int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                        if (r8 != 0) goto L_0x0095
                        goto L_0x0096
                    L_0x0095:
                        r0 = 0
                    L_0x0096:
                        return r0
                    L_0x0097:
                        return r3
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SearchDownloadsContainer.AnonymousClass3.areItemsTheSame(int, int):boolean");
                }

                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return areItemsTheSame(oldItemPosition, newItemPosition);
                }
            }).dispatchUpdatesTo((RecyclerView.Adapter) this.adapter);
            int i6 = 0;
            while (i6 < this.recyclerListView.getChildCount()) {
                View child = this.recyclerListView.getChildAt(i6);
                int p = this.recyclerListView.getChildAdapterPosition(child);
                if (p >= 0) {
                    RecyclerView.ViewHolder holder = this.recyclerListView.getChildViewHolder(child);
                    if (holder == null) {
                        oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                    } else if (holder.shouldIgnore()) {
                        oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                    } else if (child instanceof GraySectionCell) {
                        this.adapter.onBindViewHolder(holder, p);
                        oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                    } else if (child instanceof Cell) {
                        Cell cell = (Cell) child;
                        cell.sharedDocumentCell.updateFileExistIcon(true);
                        oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                        this.messageHashIdTmp.set(cell.sharedDocumentCell.getMessage().getId(), cell.sharedDocumentCell.getMessage().getDialogId());
                        cell.sharedDocumentCell.setChecked(this.uiCallback.isSelected(this.messageHashIdTmp), true);
                    } else {
                        oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                    }
                } else {
                    oldDownloadingLoadingFiles = oldDownloadingLoadingFiles2;
                }
                i6++;
                oldDownloadingLoadingFiles2 = oldDownloadingLoadingFiles;
            }
            return;
        }
        updateRows(arrayList, arrayList2);
        this.adapter.notifyDataSetChanged();
    }

    private void updateRows(ArrayList<MessageObject> currentLoadingFilesTmp2, ArrayList<MessageObject> recentLoadingFilesTmp2) {
        this.currentLoadingFiles.clear();
        this.currentLoadingFiles.addAll(currentLoadingFilesTmp2);
        this.recentLoadingFiles.clear();
        this.recentLoadingFiles.addAll(recentLoadingFilesTmp2);
        this.rowCount = 0;
        this.downloadingFilesHeader = -1;
        this.downloadingFilesStartRow = -1;
        this.downloadingFilesEndRow = -1;
        this.recentFilesHeader = -1;
        this.recentFilesStartRow = -1;
        this.recentFilesEndRow = -1;
        this.hasCurrentDownload = false;
        if (!this.currentLoadingFiles.isEmpty()) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.downloadingFilesHeader = i;
            this.downloadingFilesStartRow = i2;
            int size = i2 + this.currentLoadingFiles.size();
            this.rowCount = size;
            this.downloadingFilesEndRow = size;
            int i3 = 0;
            while (true) {
                if (i3 >= this.currentLoadingFiles.size()) {
                    break;
                } else if (FileLoader.getInstance(this.currentAccount).isLoadingFile(this.currentLoadingFiles.get(i3).getFileName())) {
                    this.hasCurrentDownload = true;
                    break;
                } else {
                    i3++;
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

    public void search(String query) {
        this.searchQuery = query;
        update(false);
    }

    private class DownloadsAdapter extends RecyclerListView.SelectionAdapter {
        private DownloadsAdapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = new GraySectionCell(parent.getContext());
            } else if (viewType == 1) {
                view = new Cell(parent.getContext());
            } else {
                view = new SharedAudioCell(parent.getContext()) {
                    public boolean needPlayMessage(MessageObject messageObject) {
                        return MediaController.getInstance().playMessage(messageObject);
                    }
                };
                View view2 = view;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String str;
            int i;
            int type = holder.getItemViewType();
            if (type == 0) {
                GraySectionCell graySectionCell = (GraySectionCell) holder.itemView;
                if (position == SearchDownloadsContainer.this.downloadingFilesHeader) {
                    String string = LocaleController.getString("Downloading", NUM);
                    if (SearchDownloadsContainer.this.hasCurrentDownload) {
                        i = NUM;
                        str = "PauseAll";
                    } else {
                        i = NUM;
                        str = "ResumeAll";
                    }
                    graySectionCell.setText(string, LocaleController.getString(str, i), new View.OnClickListener() {
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
                } else if (position == SearchDownloadsContainer.this.recentFilesHeader) {
                    graySectionCell.setText(LocaleController.getString("RecentlyDownloaded", NUM), LocaleController.getString("Settings", NUM), new View.OnClickListener() {
                        public void onClick(View view) {
                            SearchDownloadsContainer.this.showSettingsDialog();
                        }
                    });
                }
            } else {
                MessageObject messageObject = getMessage(position);
                if (messageObject != null) {
                    boolean z = false;
                    if (type == 1) {
                        Cell view = (Cell) holder.itemView;
                        view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                        int oldId = view.sharedDocumentCell.getMessage() == null ? 0 : view.sharedDocumentCell.getMessage().getId();
                        view.sharedDocumentCell.setDocument(messageObject, true);
                        SearchDownloadsContainer.this.messageHashIdTmp.set(view.sharedDocumentCell.getMessage().getId(), view.sharedDocumentCell.getMessage().getDialogId());
                        SharedDocumentCell sharedDocumentCell = view.sharedDocumentCell;
                        boolean isSelected = SearchDownloadsContainer.this.uiCallback.isSelected(SearchDownloadsContainer.this.messageHashIdTmp);
                        if (oldId == messageObject.getId()) {
                            z = true;
                        }
                        sharedDocumentCell.setChecked(isSelected, z);
                    } else if (type == 2) {
                        SharedAudioCell sharedAudioCell = (SharedAudioCell) holder.itemView;
                        sharedAudioCell.setMessageObject(messageObject, true);
                        int oldId2 = sharedAudioCell.getMessage() == null ? 0 : sharedAudioCell.getMessage().getId();
                        boolean isSelected2 = SearchDownloadsContainer.this.uiCallback.isSelected(SearchDownloadsContainer.this.messageHashIdTmp);
                        if (oldId2 == messageObject.getId()) {
                            z = true;
                        }
                        sharedAudioCell.setChecked(isSelected2, z);
                    }
                }
            }
        }

        public int getItemViewType(int position) {
            if (position == SearchDownloadsContainer.this.downloadingFilesHeader || position == SearchDownloadsContainer.this.recentFilesHeader) {
                return 0;
            }
            MessageObject messageObject = getMessage(position);
            if (messageObject != null && messageObject.isMusic()) {
                return 2;
            }
            return 1;
        }

        /* access modifiers changed from: private */
        public MessageObject getMessage(int position) {
            if (position >= SearchDownloadsContainer.this.downloadingFilesStartRow && position < SearchDownloadsContainer.this.downloadingFilesEndRow) {
                return SearchDownloadsContainer.this.currentLoadingFiles.get(position - SearchDownloadsContainer.this.downloadingFilesStartRow);
            }
            if (position < SearchDownloadsContainer.this.recentFilesStartRow || position >= SearchDownloadsContainer.this.recentFilesEndRow) {
                return null;
            }
            return SearchDownloadsContainer.this.recentLoadingFiles.get(position - SearchDownloadsContainer.this.recentFilesStartRow);
        }

        public int getItemCount() {
            return SearchDownloadsContainer.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1 || holder.getItemViewType() == 2;
        }
    }

    /* access modifiers changed from: private */
    public void showSettingsDialog() {
        if (this.parentFragment != null && this.parentActivity != null) {
            BottomSheet bottomSheet = new BottomSheet(this.parentActivity, false);
            Context context = this.parentFragment.getParentActivity();
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            StickerImageView imageView = new StickerImageView(context, this.currentAccount);
            imageView.setStickerNum(9);
            imageView.getImageReceiver().setAutoRepeat(1);
            linearLayout.addView(imageView, LayoutHelper.createLinear(144, 144, 1, 0, 16, 0, 0));
            TextView title = new TextView(context);
            title.setGravity(1);
            title.setTextColor(Theme.getColor("dialogTextBlack"));
            title.setTextSize(1, 24.0f);
            title.setText(LocaleController.getString("DownloadedFiles", NUM));
            linearLayout.addView(title, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 30.0f, 21.0f, 0.0f));
            TextView description = new TextView(context);
            description.setGravity(1);
            description.setTextSize(1, 15.0f);
            description.setTextColor(Theme.getColor("dialogTextHint"));
            description.setText(LocaleController.formatString("DownloadedFilesMessage", NUM, new Object[0]));
            linearLayout.addView(description, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
            TextView buttonTextView = new TextView(context);
            buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            buttonTextView.setGravity(17);
            buttonTextView.setTextSize(1, 14.0f);
            buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            buttonTextView.setText(LocaleController.getString("ManageDeviceStorage", NUM));
            buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), 120)));
            linearLayout.addView(buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
            TextView buttonTextView2 = new TextView(context);
            buttonTextView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            buttonTextView2.setGravity(17);
            buttonTextView2.setTextSize(1, 14.0f);
            buttonTextView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            buttonTextView2.setText(LocaleController.getString("ClearDownloadsList", NUM));
            buttonTextView2.setTextColor(Theme.getColor("featuredStickers_addButton"));
            buttonTextView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor("featuredStickers_addButton"), 120)));
            linearLayout.addView(buttonTextView2, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 0.0f, 16.0f, 16.0f));
            NestedScrollView scrollView = new NestedScrollView(context);
            scrollView.addView(linearLayout);
            bottomSheet.setCustomView(scrollView);
            bottomSheet.show();
            buttonTextView.setOnClickListener(new SearchDownloadsContainer$$ExternalSyntheticLambda0(this, bottomSheet));
            buttonTextView2.setOnClickListener(new SearchDownloadsContainer$$ExternalSyntheticLambda1(this, bottomSheet));
        }
    }

    /* renamed from: lambda$showSettingsDialog$6$org-telegram-ui-Components-SearchDownloadsContainer  reason: not valid java name */
    public /* synthetic */ void m4312x8655ff2(BottomSheet bottomSheet, View view) {
        bottomSheet.dismiss();
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            baseFragment.presentFragment(new CacheControlActivity());
        }
    }

    /* renamed from: lambda$showSettingsDialog$7$org-telegram-ui-Components-SearchDownloadsContainer  reason: not valid java name */
    public /* synthetic */ void m4313x95a01173(BottomSheet bottomSheet, View view) {
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.onDownloadingFilesChanged) {
            if (getVisibility() == 0) {
                DownloadController.getInstance(this.currentAccount).clearUnviewedDownloads();
            }
            update(true);
        }
    }

    private class Cell extends FrameLayout {
        SharedDocumentCell sharedDocumentCell;

        public Cell(Context context) {
            super(context);
            SharedDocumentCell sharedDocumentCell2 = new SharedDocumentCell(context, 2);
            this.sharedDocumentCell = sharedDocumentCell2;
            sharedDocumentCell2.rightDateTextView.setVisibility(8);
            addView(this.sharedDocumentCell);
        }
    }

    public void setUiCallback(FilteredSearchView.UiCallback callback) {
        this.uiCallback = callback;
    }

    public void setKeyboardHeight(int keyboardSize, boolean animated) {
        this.emptyView.setKeyboardHeight(keyboardSize, animated);
    }
}
