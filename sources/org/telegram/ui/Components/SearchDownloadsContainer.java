package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PhotoViewer;

public class SearchDownloadsContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    DownloadsAdapter adapter = new DownloadsAdapter();
    private final int currentAccount;
    ArrayList<MessageObject> currentLoadingFiles = new ArrayList<>();
    int downloadingFilesEndRow = -1;
    int downloadingFilesHeader = -1;
    int downloadingFilesStartRow = -1;
    /* access modifiers changed from: private */
    public boolean hasCurrentDownload;
    Activity parentActivity;
    BaseFragment parentFragment;
    int recentFilesEndRow = -1;
    int recentFilesHeader = -1;
    int recentFilesStartRow = -1;
    ArrayList<MessageObject> recentLoadingFiles = new ArrayList<>();
    public RecyclerListView recyclerListView;
    int rowCount;

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
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new SearchDownloadsContainer$$ExternalSyntheticLambda0(this));
        FileLoader.getInstance(i).getCurrentLoadingFiles(this.currentLoadingFiles);
        update(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view, int i) {
        if (view instanceof Cell) {
            SharedDocumentCell sharedDocumentCell = ((Cell) view).sharedDocumentCell;
            MessageObject message = sharedDocumentCell.getMessage();
            TLRPC$Document document = message.getDocument();
            if (sharedDocumentCell.isLoaded()) {
                if (message.canPreviewDocument()) {
                    PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(message);
                    PhotoViewer.getInstance().setParentActivity(this.parentActivity);
                    PhotoViewer.getInstance().openPhoto((ArrayList<MessageObject>) arrayList, 0, 0, 0, (PhotoViewer.PhotoViewerProvider) new PhotoViewer.EmptyPhotoViewerProvider());
                    return;
                }
                AndroidUtilities.openDocument(message, this.parentActivity, this.parentFragment);
            } else if (!sharedDocumentCell.isLoading()) {
                AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().loadFile(document, sharedDocumentCell.getMessage(), 0, 0);
                sharedDocumentCell.updateFileExistIcon(true);
            } else {
                AccountInstance.getInstance(UserConfig.selectedAccount).getFileLoader().cancelLoadFile(document);
                sharedDocumentCell.updateFileExistIcon(true);
            }
            update(true);
        }
    }

    public void update(boolean z) {
        RecyclerView.ViewHolder childViewHolder;
        if (z) {
            final int i = this.downloadingFilesHeader;
            final int i2 = this.downloadingFilesStartRow;
            final int i3 = this.downloadingFilesEndRow;
            final int i4 = this.recentFilesHeader;
            final int i5 = this.recentFilesStartRow;
            final int i6 = this.recentFilesEndRow;
            final int i7 = this.rowCount;
            final ArrayList arrayList = new ArrayList(this.currentLoadingFiles);
            final ArrayList arrayList2 = new ArrayList(this.recentLoadingFiles);
            updateRows();
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
                        messageObject = (i < i4 || i >= i6) ? null : (MessageObject) arrayList2.get(i - i4);
                    } else {
                        messageObject = (MessageObject) arrayList.get(i - i3);
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
                        ((Cell) childAt).sharedDocumentCell.updateFileExistIcon(true);
                    }
                }
            }
            return;
        }
        updateRows();
        this.adapter.notifyDataSetChanged();
    }

    private void updateRows() {
        FileLoader.getInstance(this.currentAccount).getCurrentLoadingFiles(this.currentLoadingFiles);
        FileLoader.getInstance(this.currentAccount).getRecentLoadingFiles(this.recentLoadingFiles);
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

    private class DownloadsAdapter extends RecyclerListView.SelectionAdapter {
        private DownloadsAdapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new GraySectionCell(viewGroup.getContext());
            } else {
                view = new Cell(SearchDownloadsContainer.this, viewGroup.getContext());
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            int i2;
            if (viewHolder.getItemViewType() == 0) {
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
                            BaseFragment baseFragment = SearchDownloadsContainer.this.parentFragment;
                            if (baseFragment != null) {
                                baseFragment.presentFragment(new CacheControlActivity());
                            }
                        }
                    });
                }
            } else {
                Cell cell = (Cell) viewHolder.itemView;
                cell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                SearchDownloadsContainer searchDownloadsContainer2 = SearchDownloadsContainer.this;
                int i3 = searchDownloadsContainer2.downloadingFilesStartRow;
                if (i < i3 || i >= searchDownloadsContainer2.downloadingFilesEndRow) {
                    int i4 = searchDownloadsContainer2.recentFilesStartRow;
                    if (i >= i4 && i < searchDownloadsContainer2.recentFilesEndRow) {
                        searchDownloadsContainer2.recentLoadingFiles.get(i - i4).checkMediaExistance();
                        SharedDocumentCell sharedDocumentCell = cell.sharedDocumentCell;
                        SearchDownloadsContainer searchDownloadsContainer3 = SearchDownloadsContainer.this;
                        sharedDocumentCell.setDocument(searchDownloadsContainer3.recentLoadingFiles.get(i - searchDownloadsContainer3.recentFilesStartRow), true);
                        return;
                    }
                    return;
                }
                searchDownloadsContainer2.currentLoadingFiles.get(i - i3).checkMediaExistance();
                SharedDocumentCell sharedDocumentCell2 = cell.sharedDocumentCell;
                SearchDownloadsContainer searchDownloadsContainer4 = SearchDownloadsContainer.this;
                sharedDocumentCell2.setDocument(searchDownloadsContainer4.currentLoadingFiles.get(i - searchDownloadsContainer4.downloadingFilesStartRow), true);
            }
        }

        public int getItemViewType(int i) {
            SearchDownloadsContainer searchDownloadsContainer = SearchDownloadsContainer.this;
            return (i == searchDownloadsContainer.downloadingFilesHeader || i == searchDownloadsContainer.recentFilesHeader) ? 0 : 1;
        }

        public int getItemCount() {
            return SearchDownloadsContainer.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onDownloadingFilesChanged);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.onDownloadingFilesChanged);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.onDownloadingFilesChanged) {
            update(true);
        }
    }

    private class Cell extends FrameLayout {
        SharedDocumentCell sharedDocumentCell;

        public Cell(SearchDownloadsContainer searchDownloadsContainer, Context context) {
            super(context);
            SharedDocumentCell sharedDocumentCell2 = new SharedDocumentCell(context, 2);
            this.sharedDocumentCell = sharedDocumentCell2;
            addView(sharedDocumentCell2);
        }
    }
}
