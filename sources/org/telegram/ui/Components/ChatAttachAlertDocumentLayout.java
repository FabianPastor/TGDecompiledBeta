package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;

public class ChatAttachAlertDocumentLayout extends ChatAttachAlert.AttachAlertLayout {
    /* access modifiers changed from: private */
    public float additionalTranslationY;
    private boolean allowMusic;
    /* access modifiers changed from: private */
    public boolean canSelectOnlyImageFiles;
    /* access modifiers changed from: private */
    public File currentDir;
    /* access modifiers changed from: private */
    public DocumentSelectActivityDelegate delegate;
    /* access modifiers changed from: private */
    public StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public FiltersView filtersView;
    /* access modifiers changed from: private */
    public AnimatorSet filtersViewAnimator;
    private boolean hasFiles;
    /* access modifiers changed from: private */
    public ArrayList<HistoryEntry> history = new ArrayList<>();
    private boolean ignoreLayout;
    public boolean isSoundPicker;
    /* access modifiers changed from: private */
    public ArrayList<ListItem> items = new ArrayList<>();
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public FlickerLoadingView loadingView;
    private int maxSelectedFiles = -1;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ChatAttachAlertDocumentLayout$1$$ExternalSyntheticLambda0 chatAttachAlertDocumentLayout$1$$ExternalSyntheticLambda0 = new ChatAttachAlertDocumentLayout$1$$ExternalSyntheticLambda0(this);
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                ChatAttachAlertDocumentLayout.this.listView.postDelayed(chatAttachAlertDocumentLayout$1$$ExternalSyntheticLambda0, 1000);
            } else {
                chatAttachAlertDocumentLayout$1$$ExternalSyntheticLambda0.run();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0() {
            try {
                if (ChatAttachAlertDocumentLayout.this.currentDir == null) {
                    ChatAttachAlertDocumentLayout.this.listRoots();
                } else {
                    ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
                    boolean unused = chatAttachAlertDocumentLayout.listFiles(chatAttachAlertDocumentLayout.currentDir);
                }
                ChatAttachAlertDocumentLayout.this.updateSearchButton();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    };
    private boolean receiverRegistered = false;
    /* access modifiers changed from: private */
    public ArrayList<ListItem> recentItems = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean scrolling;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public HashMap<String, ListItem> selectedFiles = new HashMap<>();
    private ArrayList<String> selectedFilesOrder = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<FilteredSearchView.MessageHashId, MessageObject> selectedMessages = new HashMap<>();
    private boolean sendPressed;
    /* access modifiers changed from: private */
    public boolean sortByName;
    /* access modifiers changed from: private */
    public ActionBarMenuItem sortItem;

    public interface DocumentSelectActivityDelegate {

        /* renamed from: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$DocumentSelectActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didSelectPhotos(DocumentSelectActivityDelegate documentSelectActivityDelegate, ArrayList arrayList, boolean z, int i) {
            }

            public static void $default$startMusicSelectActivity(DocumentSelectActivityDelegate documentSelectActivityDelegate) {
            }
        }

        void didSelectFiles(ArrayList<String> arrayList, String str, ArrayList<MessageObject> arrayList2, boolean z, int i);

        void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i);

        void startDocumentSelectActivity();

        void startMusicSelectActivity();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    private static class ListItem {
        public String ext;
        public File file;
        public int icon;
        public String subtitle;
        public String thumb;
        public String title;

        private ListItem() {
            this.subtitle = "";
            this.ext = "";
        }
    }

    private static class HistoryEntry {
        File dir;
        String title;

        private HistoryEntry() {
        }
    }

    public ChatAttachAlertDocumentLayout(ChatAttachAlert chatAttachAlert, Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        this.allowMusic = i == 1;
        this.isSoundPicker = i == 2;
        this.sortByName = SharedConfig.sortFilesByName;
        loadRecentFiles();
        this.searching = false;
        if (!this.receiverRegistered) {
            this.receiverRegistered = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
            intentFilter.addAction("android.intent.action.MEDIA_EJECT");
            intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
            intentFilter.addAction("android.intent.action.MEDIA_NOFS");
            intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
            intentFilter.addAction("android.intent.action.MEDIA_SHARED");
            intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
            intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            intentFilter.addDataScheme("file");
            ApplicationLoader.applicationContext.registerReceiver(this.receiver, intentFilter);
        }
        ActionBarMenu createMenu = this.parentAlert.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = ChatAttachAlertDocumentLayout.this.searching = true;
                ChatAttachAlertDocumentLayout.this.sortItem.setVisibility(8);
                ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
                chatAttachAlertDocumentLayout.parentAlert.makeFocusable(chatAttachAlertDocumentLayout.searchItem.getSearchField(), true);
            }

            public void onSearchCollapse() {
                boolean unused = ChatAttachAlertDocumentLayout.this.searching = false;
                ChatAttachAlertDocumentLayout.this.sortItem.setVisibility(0);
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.listAdapter) {
                    ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.listAdapter);
                }
                ChatAttachAlertDocumentLayout.this.listAdapter.notifyDataSetChanged();
                ChatAttachAlertDocumentLayout.this.searchAdapter.search((String) null, true);
            }

            public void onTextChanged(EditText editText) {
                ChatAttachAlertDocumentLayout.this.searchAdapter.search(editText.getText().toString(), false);
            }

            public void onSearchFilterCleared(FiltersView.MediaFilterData mediaFilterData) {
                ChatAttachAlertDocumentLayout.this.searchAdapter.removeSearchFilter(mediaFilterData);
                ChatAttachAlertDocumentLayout.this.searchAdapter.search(ChatAttachAlertDocumentLayout.this.searchItem.getSearchField().getText().toString(), false);
                ChatAttachAlertDocumentLayout.this.searchAdapter.updateFiltersView(true, (ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, true);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(getThemedColor("dialogTextBlack"));
        searchField.setCursorColor(getThemedColor("dialogTextBlack"));
        searchField.setHintTextColor(getThemedColor("chat_messagePanelHint"));
        ActionBarMenuItem addItem = createMenu.addItem(6, this.sortByName ? NUM : NUM);
        this.sortItem = addItem;
        addItem.setContentDescription(LocaleController.getString("AccDescrContactSorting", NUM));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, resourcesProvider);
        this.loadingView = flickerLoadingView;
        addView(flickerLoadingView);
        AnonymousClass3 r3 = new StickerEmptyView(context, this.loadingView, 1, resourcesProvider) {
            public void setTranslationY(float f) {
                super.setTranslationY(f + ChatAttachAlertDocumentLayout.this.additionalTranslationY);
            }

            public float getTranslationY() {
                return super.getTranslationY() - ChatAttachAlertDocumentLayout.this.additionalTranslationY;
            }
        };
        this.emptyView = r3;
        addView(r3, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setVisibility(8);
        this.emptyView.setOnTouchListener(ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda0.INSTANCE);
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider);
        this.listView = recyclerListView;
        recyclerListView.setSectionsType(2);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass4 r2 = new FillLastLinearLayoutManager(context, 1, false, AndroidUtilities.dp(56.0f), this.listView) {
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int i) {
                        return super.calculateDyToMakeVisible(view, i) - (ChatAttachAlertDocumentLayout.this.listView.getPaddingTop() - AndroidUtilities.dp(56.0f));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int i) {
                        return super.calculateTimeForDeceleration(i) * 2;
                    }
                };
                r2.setTargetPosition(i);
                startSmoothScroll(r2);
            }
        };
        this.layoutManager = r2;
        recyclerListView2.setLayoutManager(r2);
        this.listView.setClipToPadding(false);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context);
        this.listAdapter = listAdapter2;
        recyclerListView3.setAdapter(listAdapter2);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchAdapter = new SearchAdapter(context);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
                chatAttachAlertDocumentLayout.parentAlert.updateLayout(chatAttachAlertDocumentLayout, true, i2);
                ChatAttachAlertDocumentLayout.this.updateEmptyViewPosition();
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() == ChatAttachAlertDocumentLayout.this.searchAdapter) {
                    int findFirstVisibleItemPosition = ChatAttachAlertDocumentLayout.this.layoutManager.findFirstVisibleItemPosition();
                    int findLastVisibleItemPosition = ChatAttachAlertDocumentLayout.this.layoutManager.findLastVisibleItemPosition();
                    int abs = Math.abs(findLastVisibleItemPosition - findFirstVisibleItemPosition) + 1;
                    int itemCount = recyclerView.getAdapter().getItemCount();
                    if (abs > 0 && findLastVisibleItemPosition >= itemCount - 10) {
                        ChatAttachAlertDocumentLayout.this.searchAdapter.loadMore();
                    }
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                RecyclerListView.Holder holder;
                boolean z = false;
                if (i == 0) {
                    int dp = AndroidUtilities.dp(13.0f);
                    int backgroundPaddingTop = ChatAttachAlertDocumentLayout.this.parentAlert.getBackgroundPaddingTop();
                    if (((ChatAttachAlertDocumentLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - dp) + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertDocumentLayout.this.listView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > AndroidUtilities.dp(56.0f)) {
                        ChatAttachAlertDocumentLayout.this.listView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(56.0f));
                    }
                }
                if (i == 1 && ChatAttachAlertDocumentLayout.this.searching && ChatAttachAlertDocumentLayout.this.listView.getAdapter() == ChatAttachAlertDocumentLayout.this.searchAdapter) {
                    AndroidUtilities.hideKeyboard(ChatAttachAlertDocumentLayout.this.parentAlert.getCurrentFocus());
                }
                ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
                if (i != 0) {
                    z = true;
                }
                boolean unused = chatAttachAlertDocumentLayout.scrolling = z;
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda3(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda5(this));
        FiltersView filtersView2 = new FiltersView(context, resourcesProvider);
        this.filtersView = filtersView2;
        filtersView2.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda4(this));
        this.filtersView.setBackgroundColor(getThemedColor("dialogBackground"));
        addView(this.filtersView, LayoutHelper.createFrame(-1, -2, 48));
        this.filtersView.setTranslationY((float) (-AndroidUtilities.dp(44.0f)));
        this.filtersView.setVisibility(4);
        listRoots();
        updateSearchButton();
        updateEmptyView();
    }

    /* JADX WARNING: type inference failed for: r2v6, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$new$1(android.view.View r14, int r15) {
        /*
            r13 = this;
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r1 = r13.listAdapter
            if (r0 != r1) goto L_0x000f
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r15 = r1.getItem(r15)
            goto L_0x0015
        L_0x000f:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter r0 = r13.searchAdapter
            java.lang.Object r15 = r0.getItem(r15)
        L_0x0015:
            boolean r0 = r15 instanceof org.telegram.ui.Components.ChatAttachAlertDocumentLayout.ListItem
            if (r0 == 0) goto L_0x011c
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r15 = (org.telegram.ui.Components.ChatAttachAlertDocumentLayout.ListItem) r15
            java.io.File r0 = r15.file
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 30
            r3 = 0
            if (r1 < r2) goto L_0x0029
            boolean r1 = android.os.Environment.isExternalStorageManager()
            goto L_0x002a
        L_0x0029:
            r1 = 0
        L_0x002a:
            boolean r2 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
            if (r2 != 0) goto L_0x0043
            int r2 = r15.icon
            r4 = 2131165439(0x7var_ff, float:1.7945095E38)
            if (r2 == r4) goto L_0x003a
            r4 = 2131165437(0x7var_fd, float:1.7945091E38)
            if (r2 != r4) goto L_0x0043
        L_0x003a:
            if (r1 != 0) goto L_0x0043
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$DocumentSelectActivityDelegate r14 = r13.delegate
            r14.startDocumentSelectActivity()
            goto L_0x011f
        L_0x0043:
            r1 = 0
            if (r0 != 0) goto L_0x00d0
            int r14 = r15.icon
            r15 = 2131165436(0x7var_fc, float:1.794509E38)
            r0 = 1
            if (r14 != r15) goto L_0x0094
            java.util.HashMap r14 = new java.util.HashMap
            r14.<init>()
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            org.telegram.ui.Components.ChatAttachAlert r2 = r13.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r2 = r2.baseFragment
            boolean r4 = r2 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x0063
            r1 = r2
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
        L_0x0063:
            r11 = r1
            org.telegram.ui.PhotoPickerActivity r1 = new org.telegram.ui.PhotoPickerActivity
            r5 = 0
            org.telegram.messenger.MediaController$AlbumEntry r6 = org.telegram.messenger.MediaController.allMediaAlbumEntry
            r9 = 0
            if (r11 == 0) goto L_0x006e
            r10 = 1
            goto L_0x006f
        L_0x006e:
            r10 = 0
        L_0x006f:
            r12 = 0
            r4 = r1
            r7 = r14
            r8 = r15
            r4.<init>(r5, r6, r7, r8, r9, r10, r11, r12)
            r1.setDocumentsPicker(r0)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$6 r2 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$6
            r2.<init>(r14, r15)
            r1.setDelegate(r2)
            int r14 = r13.maxSelectedFiles
            r1.setMaxSelectedPhotos(r14, r3)
            org.telegram.ui.Components.ChatAttachAlert r14 = r13.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r14 = r14.baseFragment
            r14.presentFragment(r1)
            org.telegram.ui.Components.ChatAttachAlert r14 = r13.parentAlert
            r14.dismiss(r0)
            goto L_0x011f
        L_0x0094:
            r15 = 2131165438(0x7var_fe, float:1.7945093E38)
            if (r14 != r15) goto L_0x00a2
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$DocumentSelectActivityDelegate r14 = r13.delegate
            if (r14 == 0) goto L_0x011f
            r14.startMusicSelectActivity()
            goto L_0x011f
        L_0x00a2:
            int r14 = r13.getTopForScroll()
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry> r15 = r13.history
            int r1 = r15.size()
            int r1 = r1 - r0
            java.lang.Object r15 = r15.remove(r1)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry r15 = (org.telegram.ui.Components.ChatAttachAlertDocumentLayout.HistoryEntry) r15
            org.telegram.ui.Components.ChatAttachAlert r0 = r13.parentAlert
            org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
            java.lang.String r1 = r15.title
            r0.setTitle(r1)
            java.io.File r15 = r15.dir
            if (r15 == 0) goto L_0x00c4
            r13.listFiles(r15)
            goto L_0x00c7
        L_0x00c4:
            r13.listRoots()
        L_0x00c7:
            r13.updateSearchButton()
            androidx.recyclerview.widget.LinearLayoutManager r15 = r13.layoutManager
            r15.scrollToPositionWithOffset(r3, r14)
            goto L_0x011f
        L_0x00d0:
            boolean r2 = r0.isDirectory()
            if (r2 == 0) goto L_0x0118
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry r14 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry
            r14.<init>()
            org.telegram.ui.Components.RecyclerListView r1 = r13.listView
            android.view.View r1 = r1.getChildAt(r3)
            org.telegram.ui.Components.RecyclerListView r2 = r13.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findContainingViewHolder(r1)
            if (r2 == 0) goto L_0x011f
            r2.getAdapterPosition()
            r1.getTop()
            java.io.File r1 = r13.currentDir
            r14.dir = r1
            org.telegram.ui.Components.ChatAttachAlert r1 = r13.parentAlert
            org.telegram.ui.ActionBar.ActionBar r1 = r1.actionBar
            java.lang.String r1 = r1.getTitle()
            r14.title = r1
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry> r1 = r13.history
            r1.add(r14)
            boolean r0 = r13.listFiles(r0)
            if (r0 != 0) goto L_0x010e
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry> r15 = r13.history
            r15.remove(r14)
            return
        L_0x010e:
            org.telegram.ui.Components.ChatAttachAlert r14 = r13.parentAlert
            org.telegram.ui.ActionBar.ActionBar r14 = r14.actionBar
            java.lang.String r15 = r15.title
            r14.setTitle(r15)
            goto L_0x011f
        L_0x0118:
            r13.onItemClick(r14, r15)
            goto L_0x011f
        L_0x011c:
            r13.onItemClick(r14, r15)
        L_0x011f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.lambda$new$1(android.view.View, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$2(View view, int i) {
        Object obj;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ListAdapter listAdapter2 = this.listAdapter;
        if (adapter == listAdapter2) {
            obj = listAdapter2.getItem(i);
        } else {
            obj = this.searchAdapter.getItem(i);
        }
        return onItemClick(view, obj);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view, int i) {
        this.filtersView.cancelClickRunnables(true);
        this.searchAdapter.addSearchFilter(this.filtersView.getFilterAt(i));
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        try {
            if (this.receiverRegistered) {
                ApplicationLoader.applicationContext.unregisterReceiver(this.receiver);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.parentAlert.actionBar.closeSearchField();
        ActionBarMenu createMenu = this.parentAlert.actionBar.createMenu();
        createMenu.removeView(this.sortItem);
        createMenu.removeView(this.searchItem);
    }

    /* access modifiers changed from: package-private */
    public void onMenuItemClick(int i) {
        if (i == 6) {
            SharedConfig.toggleSortFilesByName();
            this.sortByName = SharedConfig.sortFilesByName;
            sortRecentItems();
            sortFileItems();
            this.listAdapter.notifyDataSetChanged();
            this.sortItem.setIcon(this.sortByName ? NUM : NUM);
        }
    }

    /* access modifiers changed from: package-private */
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int y = ((int) childAt.getY()) - AndroidUtilities.dp(8.0f);
        if (y > 0 && holder != null && holder.getAdapterPosition() == 0) {
            i = y;
        }
        if (y < 0 || holder == null || holder.getAdapterPosition() != 0) {
            y = i;
        }
        return y + AndroidUtilities.dp(13.0f);
    }

    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }

    /* access modifiers changed from: package-private */
    public int getListTopPadding() {
        return this.listView.getPaddingTop();
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(5.0f);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x003e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPreMeasure(int r4, int r5) {
        /*
            r3 = this;
            org.telegram.ui.Components.ChatAttachAlert r4 = r3.parentAlert
            org.telegram.ui.ActionBar.ActionBar r4 = r4.actionBar
            boolean r4 = r4.isSearchFieldVisible()
            r0 = 1
            r1 = 0
            if (r4 != 0) goto L_0x0045
            org.telegram.ui.Components.ChatAttachAlert r4 = r3.parentAlert
            org.telegram.ui.Components.SizeNotifierFrameLayout r4 = r4.sizeNotifierFrameLayout
            int r4 = r4.measureKeyboardHeight()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            if (r4 <= r2) goto L_0x001d
            goto L_0x0045
        L_0x001d:
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 != 0) goto L_0x0031
            android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r4.x
            int r4 = r4.y
            if (r2 <= r4) goto L_0x0031
            float r4 = (float) r5
            r5 = 1080033280(0x40600000, float:3.5)
            float r4 = r4 / r5
            int r4 = (int) r4
            goto L_0x0035
        L_0x0031:
            int r5 = r5 / 5
            int r4 = r5 * 2
        L_0x0035:
            r5 = 1065353216(0x3var_, float:1.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            if (r4 >= 0) goto L_0x003f
            r4 = 0
        L_0x003f:
            org.telegram.ui.Components.ChatAttachAlert r5 = r3.parentAlert
            r5.setAllowNestedScroll(r0)
            goto L_0x0050
        L_0x0045:
            r4 = 1113587712(0x42600000, float:56.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            org.telegram.ui.Components.ChatAttachAlert r5 = r3.parentAlert
            r5.setAllowNestedScroll(r1)
        L_0x0050:
            org.telegram.ui.Components.RecyclerListView r5 = r3.listView
            int r5 = r5.getPaddingTop()
            if (r5 == r4) goto L_0x0067
            r3.ignoreLayout = r0
            org.telegram.ui.Components.RecyclerListView r5 = r3.listView
            r0 = 1111490560(0x42400000, float:48.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r5.setPadding(r1, r4, r1, r0)
            r3.ignoreLayout = r1
        L_0x0067:
            org.telegram.ui.Adapters.FiltersView r4 = r3.filtersView
            android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r4 = (android.widget.FrameLayout.LayoutParams) r4
            int r5 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            r4.topMargin = r5
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.onPreMeasure(int, int):void");
    }

    /* access modifiers changed from: package-private */
    public int getButtonsHideOffset() {
        return AndroidUtilities.dp(62.0f);
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void scrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    /* access modifiers changed from: package-private */
    public int getSelectedItemsCount() {
        return this.selectedFiles.size() + this.selectedMessages.size();
    }

    /* access modifiers changed from: package-private */
    public void sendSelectedItems(boolean z, int i) {
        if ((this.selectedFiles.size() != 0 || this.selectedMessages.size() != 0) && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList arrayList = new ArrayList();
            for (FilteredSearchView.MessageHashId messageHashId : this.selectedMessages.keySet()) {
                arrayList.add(this.selectedMessages.get(messageHashId));
            }
            this.delegate.didSelectFiles(new ArrayList(this.selectedFilesOrder), this.parentAlert.commentTextView.getText().toString(), arrayList, z, i);
            this.parentAlert.dismiss(true);
        }
    }

    private boolean onItemClick(View view, Object obj) {
        boolean z;
        int i;
        boolean z2 = false;
        if (obj instanceof ListItem) {
            ListItem listItem = (ListItem) obj;
            File file = listItem.file;
            if (file == null || file.isDirectory()) {
                return false;
            }
            String absolutePath = listItem.file.getAbsolutePath();
            if (this.selectedFiles.containsKey(absolutePath)) {
                this.selectedFiles.remove(absolutePath);
                this.selectedFilesOrder.remove(absolutePath);
                z = false;
            } else if (!listItem.file.canRead()) {
                showErrorBox(LocaleController.getString("AccessError", NUM));
                return false;
            } else if (this.canSelectOnlyImageFiles && listItem.thumb == null) {
                showErrorBox(LocaleController.formatString("PassportUploadNotImage", NUM, new Object[0]));
                return false;
            } else if (listItem.file.length() > NUM) {
                showErrorBox(LocaleController.formatString("FileUploadLimit", NUM, AndroidUtilities.formatFileSize(NUM)));
                return false;
            } else if (this.maxSelectedFiles >= 0 && this.selectedFiles.size() >= (i = this.maxSelectedFiles)) {
                showErrorBox(LocaleController.formatString("PassportUploadMaxReached", NUM, LocaleController.formatPluralString("Files", i)));
                return false;
            } else if ((this.isSoundPicker && !isRingtone(listItem.file)) || listItem.file.length() == 0) {
                return false;
            } else {
                this.selectedFiles.put(absolutePath, listItem);
                this.selectedFilesOrder.add(absolutePath);
                z = true;
            }
            this.scrolling = false;
        } else if (!(obj instanceof MessageObject)) {
            return false;
        } else {
            MessageObject messageObject = (MessageObject) obj;
            FilteredSearchView.MessageHashId messageHashId = new FilteredSearchView.MessageHashId(messageObject.getId(), messageObject.getDialogId());
            if (this.selectedMessages.containsKey(messageHashId)) {
                this.selectedMessages.remove(messageHashId);
            } else if (this.selectedMessages.size() >= 100) {
                return false;
            } else {
                this.selectedMessages.put(messageHashId, messageObject);
                z2 = true;
            }
            z = z2;
        }
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(z, true);
        }
        this.parentAlert.updateCountButton(z ? 1 : 2);
        return true;
    }

    public boolean isRingtone(File file) {
        int i;
        String fileExtension = FileLoader.getFileExtension(file);
        String mimeTypeFromExtension = fileExtension != null ? MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension) : null;
        if (file.length() == 0 || mimeTypeFromExtension == null || !RingtoneDataStore.ringtoneSupportedMimeType.contains(mimeTypeFromExtension)) {
            BulletinFactory.of(this.parentAlert.getContainer(), (Theme.ResourcesProvider) null).createErrorBulletinSubtitle(LocaleController.formatString("InvalidFormatError", NUM, new Object[0]), LocaleController.formatString("ErrorInvalidRingtone", NUM, new Object[0]), (Theme.ResourcesProvider) null).show();
            return false;
        } else if (file.length() > ((long) MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax)) {
            BulletinFactory.of(this.parentAlert.getContainer(), (Theme.ResourcesProvider) null).createErrorBulletinSubtitle(LocaleController.formatString("TooLargeError", NUM, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", NUM, new Object[0]), (Theme.ResourcesProvider) null).show();
            return false;
        } else {
            try {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(ApplicationLoader.applicationContext, Uri.fromFile(file));
                i = Integer.parseInt(mediaMetadataRetriever.extractMetadata(9));
            } catch (Exception unused) {
                i = Integer.MAX_VALUE;
            }
            if (i <= MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax * 1000) {
                return true;
            }
            BulletinFactory.of(this.parentAlert.getContainer(), (Theme.ResourcesProvider) null).createErrorBulletinSubtitle(LocaleController.formatString("TooLongError", NUM, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", NUM, new Object[0]), (Theme.ResourcesProvider) null).show();
            return false;
        }
    }

    public void setMaxSelectedFiles(int i) {
        this.maxSelectedFiles = i;
    }

    public void setCanSelectOnlyImageFiles(boolean z) {
        this.canSelectOnlyImageFiles = z;
    }

    /* access modifiers changed from: private */
    public void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, boolean z, int i) {
        if (!hashMap.isEmpty() && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                Object obj = hashMap.get(arrayList.get(i2));
                SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                arrayList2.add(sendingMediaInfo);
                if (obj instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                    String str = photoEntry.imagePath;
                    if (str != null) {
                        sendingMediaInfo.path = str;
                    } else {
                        sendingMediaInfo.path = photoEntry.path;
                    }
                    sendingMediaInfo.thumbPath = photoEntry.thumbPath;
                    sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                    sendingMediaInfo.isVideo = photoEntry.isVideo;
                    CharSequence charSequence = photoEntry.caption;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = photoEntry.entities;
                    sendingMediaInfo.masks = photoEntry.stickers;
                    sendingMediaInfo.ttl = photoEntry.ttl;
                }
            }
            this.delegate.didSelectPhotos(arrayList2, z, i);
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:40:0x00e8 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadRecentFiles() {
        /*
            r14 = this;
            boolean r0 = r14.isSoundPicker     // Catch:{ Exception -> 0x00fb }
            if (r0 == 0) goto L_0x00ee
            r0 = 5
            java.lang.String[] r3 = new java.lang.String[r0]     // Catch:{ Exception -> 0x00fb }
            r0 = 0
            java.lang.String r1 = "_id"
            r3[r0] = r1     // Catch:{ Exception -> 0x00fb }
            java.lang.String r0 = "_data"
            r7 = 1
            r3[r7] = r0     // Catch:{ Exception -> 0x00fb }
            java.lang.String r0 = "duration"
            r8 = 2
            r3[r8] = r0     // Catch:{ Exception -> 0x00fb }
            java.lang.String r0 = "_size"
            r9 = 3
            r3[r9] = r0     // Catch:{ Exception -> 0x00fb }
            java.lang.String r0 = "mime_type"
            r10 = 4
            r3[r10] = r0     // Catch:{ Exception -> 0x00fb }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00e9 }
            android.content.ContentResolver r1 = r0.getContentResolver()     // Catch:{ Exception -> 0x00e9 }
            android.net.Uri r2 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x00e9 }
            java.lang.String r4 = "is_music != 0"
            r5 = 0
            java.lang.String r6 = "date_added DESC"
            android.database.Cursor r0 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x00e9 }
        L_0x0031:
            boolean r1 = r0.moveToNext()     // Catch:{ all -> 0x00e2 }
            if (r1 == 0) goto L_0x00de
            java.io.File r1 = new java.io.File     // Catch:{ all -> 0x00e2 }
            java.lang.String r2 = r0.getString(r7)     // Catch:{ all -> 0x00e2 }
            r1.<init>(r2)     // Catch:{ all -> 0x00e2 }
            long r2 = r0.getLong(r8)     // Catch:{ all -> 0x00e2 }
            long r4 = r0.getLong(r9)     // Catch:{ all -> 0x00e2 }
            java.lang.String r6 = r0.getString(r10)     // Catch:{ all -> 0x00e2 }
            int r11 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x00e2 }
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)     // Catch:{ all -> 0x00e2 }
            int r11 = r11.ringtoneDurationMax     // Catch:{ all -> 0x00e2 }
            int r11 = r11 * 1000
            long r11 = (long) r11     // Catch:{ all -> 0x00e2 }
            int r13 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r13 > 0) goto L_0x0031
            int r2 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x00e2 }
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)     // Catch:{ all -> 0x00e2 }
            int r2 = r2.ringtoneSizeMax     // Catch:{ all -> 0x00e2 }
            long r2 = (long) r2     // Catch:{ all -> 0x00e2 }
            int r11 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r11 > 0) goto L_0x0031
            boolean r2 = android.text.TextUtils.isEmpty(r6)     // Catch:{ all -> 0x00e2 }
            if (r2 != 0) goto L_0x007f
            java.lang.String r2 = "audio/mpeg"
            boolean r2 = r2.equals(r6)     // Catch:{ all -> 0x00e2 }
            if (r2 != 0) goto L_0x007f
            java.lang.String r2 = "audio/mpeg4"
            boolean r2 = r2.equals(r6)     // Catch:{ all -> 0x00e2 }
            if (r2 == 0) goto L_0x007f
            goto L_0x0031
        L_0x007f:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r2 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem     // Catch:{ all -> 0x00e2 }
            r3 = 0
            r2.<init>()     // Catch:{ all -> 0x00e2 }
            java.lang.String r3 = r1.getName()     // Catch:{ all -> 0x00e2 }
            r2.title = r3     // Catch:{ all -> 0x00e2 }
            r2.file = r1     // Catch:{ all -> 0x00e2 }
            java.lang.String r3 = r1.getName()     // Catch:{ all -> 0x00e2 }
            java.lang.String r4 = "\\."
            java.lang.String[] r4 = r3.split(r4)     // Catch:{ all -> 0x00e2 }
            int r5 = r4.length     // Catch:{ all -> 0x00e2 }
            if (r5 <= r7) goto L_0x009f
            int r5 = r4.length     // Catch:{ all -> 0x00e2 }
            int r5 = r5 - r7
            r4 = r4[r5]     // Catch:{ all -> 0x00e2 }
            goto L_0x00a1
        L_0x009f:
            java.lang.String r4 = "?"
        L_0x00a1:
            r2.ext = r4     // Catch:{ all -> 0x00e2 }
            long r4 = r1.length()     // Catch:{ all -> 0x00e2 }
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.formatFileSize(r4)     // Catch:{ all -> 0x00e2 }
            r2.subtitle = r4     // Catch:{ all -> 0x00e2 }
            java.lang.String r3 = r3.toLowerCase()     // Catch:{ all -> 0x00e2 }
            java.lang.String r4 = ".jpg"
            boolean r4 = r3.endsWith(r4)     // Catch:{ all -> 0x00e2 }
            if (r4 != 0) goto L_0x00d1
            java.lang.String r4 = ".png"
            boolean r4 = r3.endsWith(r4)     // Catch:{ all -> 0x00e2 }
            if (r4 != 0) goto L_0x00d1
            java.lang.String r4 = ".gif"
            boolean r4 = r3.endsWith(r4)     // Catch:{ all -> 0x00e2 }
            if (r4 != 0) goto L_0x00d1
            java.lang.String r4 = ".jpeg"
            boolean r3 = r3.endsWith(r4)     // Catch:{ all -> 0x00e2 }
            if (r3 == 0) goto L_0x00d7
        L_0x00d1:
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ all -> 0x00e2 }
            r2.thumb = r1     // Catch:{ all -> 0x00e2 }
        L_0x00d7:
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r1 = r14.recentItems     // Catch:{ all -> 0x00e2 }
            r1.add(r2)     // Catch:{ all -> 0x00e2 }
            goto L_0x0031
        L_0x00de:
            r0.close()     // Catch:{ Exception -> 0x00e9 }
            goto L_0x00ff
        L_0x00e2:
            r1 = move-exception
            if (r0 == 0) goto L_0x00e8
            r0.close()     // Catch:{ all -> 0x00e8 }
        L_0x00e8:
            throw r1     // Catch:{ Exception -> 0x00e9 }
        L_0x00e9:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x00fb }
            goto L_0x00ff
        L_0x00ee:
            java.lang.String r0 = android.os.Environment.DIRECTORY_DOWNLOADS     // Catch:{ Exception -> 0x00fb }
            java.io.File r0 = android.os.Environment.getExternalStoragePublicDirectory(r0)     // Catch:{ Exception -> 0x00fb }
            r14.checkDirectory(r0)     // Catch:{ Exception -> 0x00fb }
            r14.sortRecentItems()     // Catch:{ Exception -> 0x00fb }
            goto L_0x00ff
        L_0x00fb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00ff:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.loadRecentFiles():void");
    }

    private void checkDirectory(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (!file2.isDirectory() || !file2.getName().equals("Telegram")) {
                    ListItem listItem = new ListItem();
                    listItem.title = file2.getName();
                    listItem.file = file2;
                    String name = file2.getName();
                    String[] split = name.split("\\.");
                    listItem.ext = split.length > 1 ? split[split.length - 1] : "?";
                    listItem.subtitle = AndroidUtilities.formatFileSize(file2.length());
                    String lowerCase = name.toLowerCase();
                    if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif") || lowerCase.endsWith(".jpeg")) {
                        listItem.thumb = file2.getAbsolutePath();
                    }
                    this.recentItems.add(listItem);
                } else {
                    checkDirectory(file2);
                }
            }
        }
    }

    private void sortRecentItems() {
        Collections.sort(this.recentItems, new ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ int lambda$sortRecentItems$4(ListItem listItem, ListItem listItem2) {
        if (this.sortByName) {
            return listItem.file.getName().compareToIgnoreCase(listItem2.file.getName());
        }
        long lastModified = listItem.file.lastModified();
        long lastModified2 = listItem2.file.lastModified();
        if (lastModified == lastModified2) {
            return 0;
        }
        return lastModified > lastModified2 ? -1 : 1;
    }

    private void sortFileItems() {
        if (this.currentDir != null) {
            Collections.sort(this.items, new ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda2(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ int lambda$sortFileItems$5(ListItem listItem, ListItem listItem2) {
        File file = listItem.file;
        if (file == null) {
            return -1;
        }
        if (listItem2.file == null) {
            return 1;
        }
        boolean isDirectory = file.isDirectory();
        if (isDirectory != listItem2.file.isDirectory()) {
            if (isDirectory) {
                return -1;
            }
            return 1;
        } else if (isDirectory || this.sortByName) {
            return listItem.file.getName().compareToIgnoreCase(listItem2.file.getName());
        } else {
            long lastModified = listItem.file.lastModified();
            long lastModified2 = listItem2.file.lastModified();
            if (lastModified == lastModified2) {
                return 0;
            }
            if (lastModified > lastModified2) {
                return -1;
            }
            return 1;
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        SearchAdapter searchAdapter2 = this.searchAdapter;
        if (searchAdapter2 != null) {
            searchAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void onShow(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        this.selectedFiles.clear();
        this.selectedMessages.clear();
        this.searchAdapter.currentSearchFilters.clear();
        this.selectedFilesOrder.clear();
        this.history.clear();
        listRoots();
        updateSearchButton();
        updateEmptyView();
        this.parentAlert.actionBar.setTitle(LocaleController.getString("SelectFile", NUM));
        this.sortItem.setVisibility(0);
        this.layoutManager.scrollToPositionWithOffset(0, 0);
    }

    /* access modifiers changed from: package-private */
    public void onHide() {
        this.sortItem.setVisibility(8);
        this.searchItem.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void updateEmptyViewPosition() {
        View childAt;
        if (this.emptyView.getVisibility() == 0 && (childAt = this.listView.getChildAt(0)) != null) {
            float translationY = this.emptyView.getTranslationY();
            this.additionalTranslationY = (float) (((this.emptyView.getMeasuredHeight() - getMeasuredHeight()) + childAt.getTop()) / 2);
            this.emptyView.setTranslationY(translationY);
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter2 = this.searchAdapter;
        int i = 0;
        boolean z = true;
        if (adapter != searchAdapter2 ? this.listAdapter.getItemCount() != 1 : !searchAdapter2.searchResult.isEmpty() || !this.searchAdapter.sections.isEmpty()) {
            z = false;
        }
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (!z) {
            i = 8;
        }
        stickerEmptyView.setVisibility(i);
        updateEmptyViewPosition();
    }

    /* access modifiers changed from: private */
    public void updateSearchButton() {
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null && !actionBarMenuItem.isSearchFieldVisible()) {
            this.searchItem.setVisibility((this.hasFiles || this.history.isEmpty()) ? 0 : 8);
        }
    }

    private int getTopForScroll() {
        View childAt = this.listView.getChildAt(0);
        RecyclerView.ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(childAt);
        int i = -this.listView.getPaddingTop();
        return (findContainingViewHolder == null || findContainingViewHolder.getAdapterPosition() != 0) ? i : i + childAt.getTop();
    }

    private boolean canClosePicker() {
        if (this.history.size() <= 0) {
            return true;
        }
        ArrayList<HistoryEntry> arrayList = this.history;
        HistoryEntry remove = arrayList.remove(arrayList.size() - 1);
        this.parentAlert.actionBar.setTitle(remove.title);
        int topForScroll = getTopForScroll();
        File file = remove.dir;
        if (file != null) {
            listFiles(file);
        } else {
            listRoots();
        }
        updateSearchButton();
        this.layoutManager.scrollToPositionWithOffset(0, topForScroll);
        return false;
    }

    public boolean onBackPressed() {
        if (!canClosePicker()) {
            return true;
        }
        return super.onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateEmptyViewPosition();
    }

    public void setDelegate(DocumentSelectActivityDelegate documentSelectActivityDelegate) {
        this.delegate = documentSelectActivityDelegate;
    }

    /* access modifiers changed from: private */
    public boolean listFiles(File file) {
        this.hasFiles = false;
        if (file.canRead()) {
            try {
                File[] listFiles = file.listFiles();
                if (listFiles == null) {
                    showErrorBox(LocaleController.getString("UnknownError", NUM));
                    return false;
                }
                this.currentDir = file;
                this.items.clear();
                for (File file2 : listFiles) {
                    if (file2.getName().indexOf(46) != 0) {
                        ListItem listItem = new ListItem();
                        listItem.title = file2.getName();
                        listItem.file = file2;
                        if (file2.isDirectory()) {
                            listItem.icon = NUM;
                            listItem.subtitle = LocaleController.getString("Folder", NUM);
                        } else {
                            this.hasFiles = true;
                            String name = file2.getName();
                            String[] split = name.split("\\.");
                            listItem.ext = split.length > 1 ? split[split.length - 1] : "?";
                            listItem.subtitle = AndroidUtilities.formatFileSize(file2.length());
                            String lowerCase = name.toLowerCase();
                            if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif") || lowerCase.endsWith(".jpeg")) {
                                listItem.thumb = file2.getAbsolutePath();
                            }
                        }
                        this.items.add(listItem);
                    }
                }
                ListItem listItem2 = new ListItem();
                listItem2.title = "..";
                if (this.history.size() > 0) {
                    ArrayList<HistoryEntry> arrayList = this.history;
                    File file3 = arrayList.get(arrayList.size() - 1).dir;
                    if (file3 == null) {
                        listItem2.subtitle = LocaleController.getString("Folder", NUM);
                    } else {
                        listItem2.subtitle = file3.toString();
                    }
                } else {
                    listItem2.subtitle = LocaleController.getString("Folder", NUM);
                }
                listItem2.icon = NUM;
                listItem2.file = null;
                this.items.add(0, listItem2);
                sortFileItems();
                updateSearchButton();
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                int topForScroll = getTopForScroll();
                this.listAdapter.notifyDataSetChanged();
                this.layoutManager.scrollToPositionWithOffset(0, topForScroll);
                return true;
            } catch (Exception e) {
                showErrorBox(e.getLocalizedMessage());
                return false;
            }
        } else if ((file.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString()) || file.getAbsolutePath().startsWith("/sdcard") || file.getAbsolutePath().startsWith("/mnt/sdcard")) && !Environment.getExternalStorageState().equals("mounted") && !Environment.getExternalStorageState().equals("mounted_ro")) {
            this.currentDir = file;
            this.items.clear();
            Environment.getExternalStorageState();
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
            return true;
        } else {
            showErrorBox(LocaleController.getString("AccessError", NUM));
            return false;
        }
    }

    private void showErrorBox(String str) {
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString("AppName", NUM)).setMessage(str).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0197 A[Catch:{ Exception -> 0x01b6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01be  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01e9  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0219  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x022c A[SYNTHETIC, Splitter:B:89:0x022c] */
    @android.annotation.SuppressLint({"NewApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void listRoots() {
        /*
            r13 = this;
            java.lang.String r0 = "Telegram"
            r1 = 0
            r13.currentDir = r1
            r2 = 0
            r13.hasFiles = r2
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r2 = r13.items
            r2.clear()
            java.util.HashSet r2 = new java.util.HashSet
            r2.<init>()
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 30
            if (r3 < r4) goto L_0x001b
            android.os.Environment.isExternalStorageManager()
        L_0x001b:
            java.io.File r3 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r3 = r3.getPath()
            java.lang.String r4 = android.os.Environment.getExternalStorageState()
            java.lang.String r5 = "mounted"
            boolean r5 = r4.equals(r5)
            r6 = 2131625687(0x7f0e06d7, float:1.887859E38)
            java.lang.String r7 = "ExternalFolderInfo"
            r8 = 2131165437(0x7var_fd, float:1.7945091E38)
            r9 = 2131627870(0x7f0e0f5e, float:1.8883017E38)
            java.lang.String r10 = "SdCard"
            if (r5 != 0) goto L_0x0044
            java.lang.String r5 = "mounted_ro"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0087
        L_0x0044:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r4 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r4.<init>()
            boolean r5 = android.os.Environment.isExternalStorageRemovable()
            if (r5 == 0) goto L_0x005e
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r4.title = r5
            r4.icon = r8
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.subtitle = r5
            goto L_0x0079
        L_0x005e:
            r5 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r11 = "InternalStorage"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r4.title = r5
            r5 = 2131165439(0x7var_ff, float:1.7945095E38)
            r4.icon = r5
            r5 = 2131626122(0x7f0e088a, float:1.8879471E38)
            java.lang.String r11 = "InternalFolderInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r4.subtitle = r5
        L_0x0079:
            java.io.File r5 = android.os.Environment.getExternalStorageDirectory()
            r4.file = r5
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r5 = r13.items
            r5.add(r4)
            r2.add(r3)
        L_0x0087:
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0177, all -> 0x0174 }
            java.io.FileReader r4 = new java.io.FileReader     // Catch:{ Exception -> 0x0177, all -> 0x0174 }
            java.lang.String r5 = "/proc/mounts"
            r4.<init>(r5)     // Catch:{ Exception -> 0x0177, all -> 0x0174 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x0177, all -> 0x0174 }
        L_0x0093:
            java.lang.String r4 = r3.readLine()     // Catch:{ Exception -> 0x0172 }
            if (r4 == 0) goto L_0x016e
            java.lang.String r5 = "vfat"
            boolean r5 = r4.contains(r5)     // Catch:{ Exception -> 0x0172 }
            if (r5 != 0) goto L_0x00a9
            java.lang.String r5 = "/mnt"
            boolean r5 = r4.contains(r5)     // Catch:{ Exception -> 0x0172 }
            if (r5 == 0) goto L_0x0093
        L_0x00a9:
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0172 }
            if (r5 == 0) goto L_0x00b0
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0172 }
        L_0x00b0:
            java.util.StringTokenizer r5 = new java.util.StringTokenizer     // Catch:{ Exception -> 0x0172 }
            java.lang.String r11 = " "
            r5.<init>(r4, r11)     // Catch:{ Exception -> 0x0172 }
            r5.nextToken()     // Catch:{ Exception -> 0x0172 }
            java.lang.String r5 = r5.nextToken()     // Catch:{ Exception -> 0x0172 }
            boolean r11 = r2.contains(r5)     // Catch:{ Exception -> 0x0172 }
            if (r11 == 0) goto L_0x00c5
            goto L_0x0093
        L_0x00c5:
            java.lang.String r11 = "/dev/block/vold"
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x0172 }
            if (r11 == 0) goto L_0x0093
            java.lang.String r11 = "/mnt/secure"
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x0172 }
            if (r11 != 0) goto L_0x0093
            java.lang.String r11 = "/mnt/asec"
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x0172 }
            if (r11 != 0) goto L_0x0093
            java.lang.String r11 = "/mnt/obb"
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x0172 }
            if (r11 != 0) goto L_0x0093
            java.lang.String r11 = "/dev/mapper"
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x0172 }
            if (r11 != 0) goto L_0x0093
            java.lang.String r11 = "tmpfs"
            boolean r4 = r4.contains(r11)     // Catch:{ Exception -> 0x0172 }
            if (r4 != 0) goto L_0x0093
            java.io.File r4 = new java.io.File     // Catch:{ Exception -> 0x0172 }
            r4.<init>(r5)     // Catch:{ Exception -> 0x0172 }
            boolean r4 = r4.isDirectory()     // Catch:{ Exception -> 0x0172 }
            if (r4 != 0) goto L_0x012c
            r4 = 47
            int r4 = r5.lastIndexOf(r4)     // Catch:{ Exception -> 0x0172 }
            r11 = -1
            if (r4 == r11) goto L_0x012c
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0172 }
            r11.<init>()     // Catch:{ Exception -> 0x0172 }
            java.lang.String r12 = "/storage/"
            r11.append(r12)     // Catch:{ Exception -> 0x0172 }
            int r4 = r4 + 1
            java.lang.String r4 = r5.substring(r4)     // Catch:{ Exception -> 0x0172 }
            r11.append(r4)     // Catch:{ Exception -> 0x0172 }
            java.lang.String r4 = r11.toString()     // Catch:{ Exception -> 0x0172 }
            java.io.File r11 = new java.io.File     // Catch:{ Exception -> 0x0172 }
            r11.<init>(r4)     // Catch:{ Exception -> 0x0172 }
            boolean r11 = r11.isDirectory()     // Catch:{ Exception -> 0x0172 }
            if (r11 == 0) goto L_0x012c
            r5 = r4
        L_0x012c:
            r2.add(r5)     // Catch:{ Exception -> 0x0172 }
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r4 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem     // Catch:{ Exception -> 0x0168 }
            r4.<init>()     // Catch:{ Exception -> 0x0168 }
            java.lang.String r11 = r5.toLowerCase()     // Catch:{ Exception -> 0x0168 }
            java.lang.String r12 = "sd"
            boolean r11 = r11.contains(r12)     // Catch:{ Exception -> 0x0168 }
            if (r11 == 0) goto L_0x0147
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r10, r9)     // Catch:{ Exception -> 0x0168 }
            r4.title = r11     // Catch:{ Exception -> 0x0168 }
            goto L_0x0152
        L_0x0147:
            java.lang.String r11 = "ExternalStorage"
            r12 = 2131625688(0x7f0e06d8, float:1.8878591E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11, r12)     // Catch:{ Exception -> 0x0168 }
            r4.title = r11     // Catch:{ Exception -> 0x0168 }
        L_0x0152:
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r7, r6)     // Catch:{ Exception -> 0x0168 }
            r4.subtitle = r11     // Catch:{ Exception -> 0x0168 }
            r4.icon = r8     // Catch:{ Exception -> 0x0168 }
            java.io.File r11 = new java.io.File     // Catch:{ Exception -> 0x0168 }
            r11.<init>(r5)     // Catch:{ Exception -> 0x0168 }
            r4.file = r11     // Catch:{ Exception -> 0x0168 }
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r5 = r13.items     // Catch:{ Exception -> 0x0168 }
            r5.add(r4)     // Catch:{ Exception -> 0x0168 }
            goto L_0x0093
        L_0x0168:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x0172 }
            goto L_0x0093
        L_0x016e:
            r3.close()     // Catch:{ Exception -> 0x0182 }
            goto L_0x0186
        L_0x0172:
            r2 = move-exception
            goto L_0x0179
        L_0x0174:
            r0 = move-exception
            goto L_0x022a
        L_0x0177:
            r2 = move-exception
            r3 = r1
        L_0x0179:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x0228 }
            if (r3 == 0) goto L_0x0186
            r3.close()     // Catch:{ Exception -> 0x0182 }
            goto L_0x0186
        L_0x0182:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0186:
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x01b6 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x01b6 }
            java.io.File r3 = r3.getExternalFilesDir(r1)     // Catch:{ Exception -> 0x01b6 }
            r2.<init>(r3, r0)     // Catch:{ Exception -> 0x01b6 }
            boolean r3 = r2.exists()     // Catch:{ Exception -> 0x01b6 }
            if (r3 == 0) goto L_0x01ba
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r3 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem     // Catch:{ Exception -> 0x01b6 }
            r3.<init>()     // Catch:{ Exception -> 0x01b6 }
            r3.title = r0     // Catch:{ Exception -> 0x01b6 }
            java.lang.String r0 = "AppFolderInfo"
            r4 = 2131624315(0x7f0e017b, float:1.8875806E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r4)     // Catch:{ Exception -> 0x01b6 }
            r3.subtitle = r0     // Catch:{ Exception -> 0x01b6 }
            r0 = 2131165435(0x7var_fb, float:1.7945087E38)
            r3.icon = r0     // Catch:{ Exception -> 0x01b6 }
            r3.file = r2     // Catch:{ Exception -> 0x01b6 }
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r0 = r13.items     // Catch:{ Exception -> 0x01b6 }
            r0.add(r3)     // Catch:{ Exception -> 0x01b6 }
            goto L_0x01ba
        L_0x01b6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01ba:
            boolean r0 = r13.isSoundPicker
            if (r0 != 0) goto L_0x01e5
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r0.<init>()
            r2 = 2131625938(0x7f0e07d2, float:1.8879098E38)
            java.lang.String r3 = "Gallery"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.title = r2
            r2 = 2131625939(0x7f0e07d3, float:1.88791E38)
            java.lang.String r3 = "GalleryInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.subtitle = r2
            r2 = 2131165436(0x7var_fc, float:1.794509E38)
            r0.icon = r2
            r0.file = r1
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r2 = r13.items
            r2.add(r0)
        L_0x01e5:
            boolean r0 = r13.allowMusic
            if (r0 == 0) goto L_0x0210
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r0.<init>()
            r2 = 2131624441(0x7f0e01f9, float:1.8876062E38)
            java.lang.String r3 = "AttachMusic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.title = r2
            r2 = 2131626582(0x7f0e0a56, float:1.8880404E38)
            java.lang.String r3 = "MusicInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.subtitle = r2
            r2 = 2131165438(0x7var_fe, float:1.7945093E38)
            r0.icon = r2
            r0.file = r1
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r1 = r13.items
            r1.add(r0)
        L_0x0210:
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r0 = r13.recentItems
            boolean r0 = r0.isEmpty()
            r1 = 1
            if (r0 != 0) goto L_0x021b
            r13.hasFiles = r1
        L_0x021b:
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            org.telegram.messenger.AndroidUtilities.clearDrawableAnimation(r0)
            r13.scrolling = r1
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r0 = r13.listAdapter
            r0.notifyDataSetChanged()
            return
        L_0x0228:
            r0 = move-exception
            r1 = r3
        L_0x022a:
            if (r1 == 0) goto L_0x0234
            r1.close()     // Catch:{ Exception -> 0x0230 }
            goto L_0x0234
        L_0x0230:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0234:
            goto L_0x0236
        L_0x0235:
            throw r0
        L_0x0236:
            goto L_0x0235
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.listRoots():void");
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        public int getItemCount() {
            int size = ChatAttachAlertDocumentLayout.this.items.size();
            if (ChatAttachAlertDocumentLayout.this.history.isEmpty() && !ChatAttachAlertDocumentLayout.this.recentItems.isEmpty()) {
                size += ChatAttachAlertDocumentLayout.this.recentItems.size() + 2;
            }
            return size + 1;
        }

        public ListItem getItem(int i) {
            int size;
            int size2 = ChatAttachAlertDocumentLayout.this.items.size();
            if (i < size2) {
                return (ListItem) ChatAttachAlertDocumentLayout.this.items.get(i);
            }
            if (!ChatAttachAlertDocumentLayout.this.history.isEmpty() || ChatAttachAlertDocumentLayout.this.recentItems.isEmpty() || i == size2 || i == size2 + 1 || (size = i - (ChatAttachAlertDocumentLayout.this.items.size() + 2)) >= ChatAttachAlertDocumentLayout.this.recentItems.size()) {
                return null;
            }
            return (ListItem) ChatAttachAlertDocumentLayout.this.recentItems.get(size);
        }

        public int getItemViewType(int i) {
            if (i == getItemCount() - 1) {
                return 3;
            }
            int size = ChatAttachAlertDocumentLayout.this.items.size();
            if (i == size) {
                return 2;
            }
            if (i == size + 1) {
                return 0;
            }
            return 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            if (i != 0) {
                if (i == 1) {
                    view2 = new SharedDocumentCell(this.mContext, 1, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                } else if (i != 2) {
                    view = new View(this.mContext);
                } else {
                    view2 = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(ChatAttachAlertDocumentLayout.this.getThemedColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    view2.setBackgroundDrawable(combinedDrawable);
                }
                view = view2;
            } else {
                view = new HeaderCell(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (ChatAttachAlertDocumentLayout.this.sortByName) {
                    headerCell.setText(LocaleController.getString("RecentFilesAZ", NUM));
                } else {
                    headerCell.setText(LocaleController.getString("RecentFiles", NUM));
                }
            } else if (itemViewType == 1) {
                ListItem item = getItem(i);
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                int i2 = item.icon;
                if (i2 != 0) {
                    sharedDocumentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, (String) null, (String) null, i2, i != ChatAttachAlertDocumentLayout.this.items.size() - 1);
                } else {
                    sharedDocumentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4)), item.thumb, 0, false);
                }
                if (item.file != null) {
                    sharedDocumentCell.setChecked(ChatAttachAlertDocumentLayout.this.selectedFiles.containsKey(item.file.toString()), !ChatAttachAlertDocumentLayout.this.scrolling);
                } else {
                    sharedDocumentCell.setChecked(false, !ChatAttachAlertDocumentLayout.this.scrolling);
                }
            }
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertDocumentLayout.this.updateEmptyView();
        }
    }

    public class SearchAdapter extends RecyclerListView.SectionsAdapter {
        /* access modifiers changed from: private */
        public int animationIndex = -1;
        private Runnable clearCurrentResultsRunnable = new Runnable() {
            public void run() {
                if (SearchAdapter.this.isLoading) {
                    SearchAdapter.this.messages.clear();
                    SearchAdapter.this.sections.clear();
                    SearchAdapter.this.sectionArrays.clear();
                    SearchAdapter.this.notifyDataSetChanged();
                }
            }
        };
        private String currentDataQuery;
        private long currentSearchDialogId;
        private FiltersView.MediaFilterData currentSearchFilter;
        /* access modifiers changed from: private */
        public ArrayList<FiltersView.MediaFilterData> currentSearchFilters = new ArrayList<>();
        private long currentSearchMaxDate;
        private long currentSearchMinDate;
        private boolean endReached;
        /* access modifiers changed from: private */
        public boolean isLoading;
        private String lastMessagesSearchString;
        private String lastSearchFilterQueryString;
        private Runnable localSearchRunnable;
        private ArrayList<Object> localTipChats = new ArrayList<>();
        private ArrayList<FiltersView.DateData> localTipDates = new ArrayList<>();
        private Context mContext;
        /* access modifiers changed from: private */
        public final FilteredSearchView.MessageHashId messageHashIdTmp = new FilteredSearchView.MessageHashId(0, 0);
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public SparseArray<MessageObject> messagesById = new SparseArray<>();
        private int nextSearchRate;
        private int requestIndex;
        /* access modifiers changed from: private */
        public ArrayList<ListItem> searchResult = new ArrayList<>();
        private Runnable searchRunnable;
        public HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap<>();
        public ArrayList<String> sections = new ArrayList<>();

        public String getLetter(int i) {
            return null;
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(String str, boolean z) {
            long j;
            Runnable runnable = this.localSearchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.localSearchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                if (!this.searchResult.isEmpty()) {
                    this.searchResult.clear();
                }
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.listAdapter) {
                    ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.listAdapter);
                }
                notifyDataSetChanged();
            } else {
                ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2 chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2 = new ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2(this, str);
                this.localSearchRunnable = chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2;
                AndroidUtilities.runOnUIThread(chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2, 300);
            }
            if (!ChatAttachAlertDocumentLayout.this.canSelectOnlyImageFiles && ChatAttachAlertDocumentLayout.this.history.isEmpty()) {
                long j2 = 0;
                long j3 = 0;
                long j4 = 0;
                for (int i = 0; i < this.currentSearchFilters.size(); i++) {
                    FiltersView.MediaFilterData mediaFilterData = this.currentSearchFilters.get(i);
                    int i2 = mediaFilterData.filterType;
                    if (i2 == 4) {
                        TLObject tLObject = mediaFilterData.chat;
                        if (tLObject instanceof TLRPC$User) {
                            j = ((TLRPC$User) tLObject).id;
                        } else if (tLObject instanceof TLRPC$Chat) {
                            j = -((TLRPC$Chat) tLObject).id;
                        }
                        j4 = j;
                    } else if (i2 == 6) {
                        FiltersView.DateData dateData = mediaFilterData.dateData;
                        j2 = dateData.minDate;
                        j3 = dateData.maxDate;
                    }
                }
                searchGlobal(j4, j2, j3, FiltersView.filters[2], str, z);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$1(String str) {
            ArrayList arrayList = new ArrayList(ChatAttachAlertDocumentLayout.this.items);
            if (ChatAttachAlertDocumentLayout.this.history.isEmpty()) {
                arrayList.addAll(0, ChatAttachAlertDocumentLayout.this.recentItems);
            }
            Utilities.searchQueue.postRunnable(new ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda3(this, str, !this.currentSearchFilters.isEmpty(), arrayList));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$0(String str, boolean z, ArrayList arrayList) {
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList(), str);
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            if (lowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            int i = (translitString != null ? 1 : 0) + 1;
            String[] strArr = new String[i];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            if (!z) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    ListItem listItem = (ListItem) arrayList.get(i2);
                    File file = listItem.file;
                    if (file != null && !file.isDirectory()) {
                        int i3 = 0;
                        while (true) {
                            if (i3 >= i) {
                                break;
                            }
                            String str2 = strArr[i3];
                            String str3 = listItem.title;
                            if (str3 != null ? str3.toLowerCase().contains(str2) : false) {
                                arrayList2.add(listItem);
                                break;
                            }
                            i3++;
                        }
                    }
                }
            }
            updateSearchResults(arrayList2, str);
        }

        public void loadMore() {
            FiltersView.MediaFilterData mediaFilterData;
            if (!ChatAttachAlertDocumentLayout.this.searchAdapter.isLoading && !ChatAttachAlertDocumentLayout.this.searchAdapter.endReached && (mediaFilterData = this.currentSearchFilter) != null) {
                searchGlobal(this.currentSearchDialogId, this.currentSearchMinDate, this.currentSearchMaxDate, mediaFilterData, this.lastMessagesSearchString, false);
            }
        }

        public void removeSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
            this.currentSearchFilters.remove(mediaFilterData);
        }

        /* access modifiers changed from: private */
        public void addSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
            if (!this.currentSearchFilters.isEmpty()) {
                int i = 0;
                while (i < this.currentSearchFilters.size()) {
                    if (!mediaFilterData.isSameType(this.currentSearchFilters.get(i))) {
                        i++;
                    } else {
                        return;
                    }
                }
            }
            this.currentSearchFilters.add(mediaFilterData);
            ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.setSearchFilter(mediaFilterData);
            ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.setSearchFieldText("");
            updateFiltersView(true, (ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, true);
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x0085  */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x00a5  */
        /* JADX WARNING: Removed duplicated region for block: B:99:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateFiltersView(boolean r9, java.util.ArrayList<java.lang.Object> r10, java.util.ArrayList<org.telegram.ui.Adapters.FiltersView.DateData> r11, boolean r12) {
            /*
                r8 = this;
                r0 = 0
                r1 = 0
                r2 = 0
                r3 = 0
                r4 = 0
            L_0x0005:
                java.util.ArrayList<org.telegram.ui.Adapters.FiltersView$MediaFilterData> r5 = r8.currentSearchFilters
                int r5 = r5.size()
                r6 = 4
                r7 = 1
                if (r1 >= r5) goto L_0x003e
                java.util.ArrayList<org.telegram.ui.Adapters.FiltersView$MediaFilterData> r5 = r8.currentSearchFilters
                java.lang.Object r5 = r5.get(r1)
                org.telegram.ui.Adapters.FiltersView$MediaFilterData r5 = (org.telegram.ui.Adapters.FiltersView.MediaFilterData) r5
                boolean r5 = r5.isMedia()
                if (r5 == 0) goto L_0x001f
                r2 = 1
                goto L_0x003b
            L_0x001f:
                java.util.ArrayList<org.telegram.ui.Adapters.FiltersView$MediaFilterData> r5 = r8.currentSearchFilters
                java.lang.Object r5 = r5.get(r1)
                org.telegram.ui.Adapters.FiltersView$MediaFilterData r5 = (org.telegram.ui.Adapters.FiltersView.MediaFilterData) r5
                int r5 = r5.filterType
                if (r5 != r6) goto L_0x002d
                r3 = 1
                goto L_0x003b
            L_0x002d:
                java.util.ArrayList<org.telegram.ui.Adapters.FiltersView$MediaFilterData> r5 = r8.currentSearchFilters
                java.lang.Object r5 = r5.get(r1)
                org.telegram.ui.Adapters.FiltersView$MediaFilterData r5 = (org.telegram.ui.Adapters.FiltersView.MediaFilterData) r5
                int r5 = r5.filterType
                r6 = 6
                if (r5 != r6) goto L_0x003b
                r4 = 1
            L_0x003b:
                int r1 = r1 + 1
                goto L_0x0005
            L_0x003e:
                if (r10 == 0) goto L_0x0046
                boolean r1 = r10.isEmpty()
                if (r1 == 0) goto L_0x004e
            L_0x0046:
                if (r11 == 0) goto L_0x0050
                boolean r1 = r11.isEmpty()
                if (r1 != 0) goto L_0x0050
            L_0x004e:
                r1 = 1
                goto L_0x0051
            L_0x0050:
                r1 = 0
            L_0x0051:
                r5 = 0
                if (r2 != 0) goto L_0x0059
                if (r1 != 0) goto L_0x0059
                if (r9 == 0) goto L_0x0059
                goto L_0x0082
            L_0x0059:
                if (r1 == 0) goto L_0x0082
                if (r10 == 0) goto L_0x0066
                boolean r9 = r10.isEmpty()
                if (r9 != 0) goto L_0x0066
                if (r3 != 0) goto L_0x0066
                goto L_0x0067
            L_0x0066:
                r10 = r5
            L_0x0067:
                if (r11 == 0) goto L_0x0072
                boolean r9 = r11.isEmpty()
                if (r9 != 0) goto L_0x0072
                if (r4 != 0) goto L_0x0072
                goto L_0x0073
            L_0x0072:
                r11 = r5
            L_0x0073:
                if (r10 != 0) goto L_0x0077
                if (r11 == 0) goto L_0x0082
            L_0x0077:
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r9 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r9 = r9.filtersView
                r9.setUsersAndDates(r10, r11, r0)
                r9 = 1
                goto L_0x0083
            L_0x0082:
                r9 = 0
            L_0x0083:
                if (r9 != 0) goto L_0x008e
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r10 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r10 = r10.filtersView
                r10.setUsersAndDates(r5, r5, r0)
            L_0x008e:
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r10 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r10 = r10.filtersView
                r10.setEnabled(r9)
                if (r9 == 0) goto L_0x00a5
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r10 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r10 = r10.filtersView
                java.lang.Object r10 = r10.getTag()
                if (r10 != 0) goto L_0x00b3
            L_0x00a5:
                if (r9 != 0) goto L_0x00b4
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r10 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r10 = r10.filtersView
                java.lang.Object r10 = r10.getTag()
                if (r10 != 0) goto L_0x00b4
            L_0x00b3:
                return
            L_0x00b4:
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r10 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r10 = r10.filtersView
                if (r9 == 0) goto L_0x00c0
                java.lang.Integer r5 = java.lang.Integer.valueOf(r7)
            L_0x00c0:
                r10.setTag(r5)
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r10 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                android.animation.AnimatorSet r10 = r10.filtersViewAnimator
                if (r10 == 0) goto L_0x00d4
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r10 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                android.animation.AnimatorSet r10 = r10.filtersViewAnimator
                r10.cancel()
            L_0x00d4:
                r10 = 1110441984(0x42300000, float:44.0)
                r11 = 0
                if (r12 == 0) goto L_0x0194
                if (r9 == 0) goto L_0x00e4
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r12 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r12 = r12.filtersView
                r12.setVisibility(r0)
            L_0x00e4:
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r12 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
                r1.<init>()
                android.animation.AnimatorSet unused = r12.filtersViewAnimator = r1
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r12 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                android.animation.AnimatorSet r12 = r12.filtersViewAnimator
                android.animation.Animator[] r1 = new android.animation.Animator[r6]
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r2 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Components.RecyclerListView r2 = r2.listView
                android.util.Property r3 = android.view.View.TRANSLATION_Y
                float[] r4 = new float[r7]
                if (r9 == 0) goto L_0x0108
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r5 = (float) r5
                goto L_0x0109
            L_0x0108:
                r5 = 0
            L_0x0109:
                r4[r0] = r5
                android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
                r1[r0] = r2
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r2 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r2 = r2.filtersView
                android.util.Property r3 = android.view.View.TRANSLATION_Y
                float[] r4 = new float[r7]
                if (r9 == 0) goto L_0x011f
                r5 = 0
                goto L_0x0125
            L_0x011f:
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r5 = -r5
                float r5 = (float) r5
            L_0x0125:
                r4[r0] = r5
                android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofFloat(r2, r3, r4)
                r1[r7] = r2
                r2 = 2
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r3 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Components.FlickerLoadingView r3 = r3.loadingView
                android.util.Property r4 = android.view.View.TRANSLATION_Y
                float[] r5 = new float[r7]
                if (r9 == 0) goto L_0x0140
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r6 = (float) r6
                goto L_0x0141
            L_0x0140:
                r6 = 0
            L_0x0141:
                r5[r0] = r6
                android.animation.ObjectAnimator r3 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
                r1[r2] = r3
                r2 = 3
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r3 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Components.StickerEmptyView r3 = r3.emptyView
                android.util.Property r4 = android.view.View.TRANSLATION_Y
                float[] r5 = new float[r7]
                if (r9 == 0) goto L_0x015b
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r11 = (float) r9
            L_0x015b:
                r5[r0] = r11
                android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofFloat(r3, r4, r5)
                r1[r2] = r9
                r12.playTogether(r1)
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r9 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                android.animation.AnimatorSet r9 = r9.filtersViewAnimator
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$2 r10 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$2
                r10.<init>()
                r9.addListener(r10)
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r9 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                android.animation.AnimatorSet r9 = r9.filtersViewAnimator
                org.telegram.ui.Components.CubicBezierInterpolator r10 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
                r9.setInterpolator(r10)
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r9 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                android.animation.AnimatorSet r9 = r9.filtersViewAnimator
                r10 = 180(0xb4, double:8.9E-322)
                r9.setDuration(r10)
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r9 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                android.animation.AnimatorSet r9 = r9.filtersViewAnimator
                r9.start()
                goto L_0x01f5
            L_0x0194:
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r12 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r12 = r12.filtersView
                androidx.recyclerview.widget.RecyclerView$Adapter r12 = r12.getAdapter()
                r12.notifyDataSetChanged()
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r12 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Components.RecyclerListView r12 = r12.listView
                if (r9 == 0) goto L_0x01af
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r1 = (float) r1
                goto L_0x01b0
            L_0x01af:
                r1 = 0
            L_0x01b0:
                r12.setTranslationY(r1)
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r12 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r12 = r12.filtersView
                if (r9 == 0) goto L_0x01bd
                r1 = 0
                goto L_0x01c3
            L_0x01bd:
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r10)
                int r1 = -r1
                float r1 = (float) r1
            L_0x01c3:
                r12.setTranslationY(r1)
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r12 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Components.FlickerLoadingView r12 = r12.loadingView
                if (r9 == 0) goto L_0x01d4
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r1 = (float) r1
                goto L_0x01d5
            L_0x01d4:
                r1 = 0
            L_0x01d5:
                r12.setTranslationY(r1)
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r12 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Components.StickerEmptyView r12 = r12.emptyView
                if (r9 == 0) goto L_0x01e5
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                float r11 = (float) r10
            L_0x01e5:
                r12.setTranslationY(r11)
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r10 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.Adapters.FiltersView r10 = r10.filtersView
                if (r9 == 0) goto L_0x01f1
                goto L_0x01f2
            L_0x01f1:
                r0 = 4
            L_0x01f2:
                r10.setVisibility(r0)
            L_0x01f5:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.updateFiltersView(boolean, java.util.ArrayList, java.util.ArrayList, boolean):void");
        }

        private void searchGlobal(long j, long j2, long j3, FiltersView.MediaFilterData mediaFilterData, String str, boolean z) {
            long j4 = j;
            long j5 = j2;
            long j6 = j3;
            FiltersView.MediaFilterData mediaFilterData2 = mediaFilterData;
            String format = String.format(Locale.ENGLISH, "%d%d%d%d%s", new Object[]{Long.valueOf(j), Long.valueOf(j2), Long.valueOf(j3), Integer.valueOf(mediaFilterData2.filterType), str});
            String str2 = this.lastSearchFilterQueryString;
            boolean z2 = str2 != null && str2.equals(format);
            boolean z3 = !z2 && z;
            if (j4 == this.currentSearchDialogId && this.currentSearchMinDate == j5) {
                int i = (this.currentSearchMaxDate > j6 ? 1 : (this.currentSearchMaxDate == j6 ? 0 : -1));
            }
            this.currentSearchFilter = mediaFilterData2;
            this.currentSearchDialogId = j4;
            this.currentSearchMinDate = j5;
            this.currentSearchMaxDate = j6;
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            AndroidUtilities.cancelRunOnUIThread(this.clearCurrentResultsRunnable);
            if (!z2 || !z) {
                if (z3) {
                    this.messages.clear();
                    this.sections.clear();
                    this.sectionArrays.clear();
                    this.isLoading = true;
                    ChatAttachAlertDocumentLayout.this.emptyView.setVisibility(0);
                    notifyDataSetChanged();
                    this.requestIndex++;
                    if (ChatAttachAlertDocumentLayout.this.listView.getPinnedHeader() != null) {
                        ChatAttachAlertDocumentLayout.this.listView.getPinnedHeader().setAlpha(0.0f);
                    }
                    this.localTipChats.clear();
                    this.localTipDates.clear();
                }
                this.isLoading = true;
                notifyDataSetChanged();
                if (!z2) {
                    this.clearCurrentResultsRunnable.run();
                    ChatAttachAlertDocumentLayout.this.emptyView.showProgress(true, !z);
                }
                if (TextUtils.isEmpty(str)) {
                    this.localTipDates.clear();
                    this.localTipChats.clear();
                    updateFiltersView(false, (ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, true);
                    return;
                }
                int i2 = 1 + this.requestIndex;
                this.requestIndex = i2;
                ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1 chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1 = r0;
                ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1 chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda12 = new ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1(this, j, str, AccountInstance.getInstance(UserConfig.selectedAccount), j2, j3, z2, format, i2);
                this.searchRunnable = chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1;
                AndroidUtilities.runOnUIThread(chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1, (!z2 || this.messages.isEmpty()) ? 350 : 0);
                ChatAttachAlertDocumentLayout.this.loadingView.setViewType(3);
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_search} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
        /* access modifiers changed from: private */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$searchGlobal$4(long r21, java.lang.String r23, org.telegram.messenger.AccountInstance r24, long r25, long r27, boolean r29, java.lang.String r30, int r31) {
            /*
                r20 = this;
                r12 = r20
                r6 = r21
                r3 = r23
                r0 = 20
                r1 = 0
                r4 = 1000(0x3e8, double:4.94E-321)
                r8 = 0
                r2 = 0
                int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r10 == 0) goto L_0x006a
                org.telegram.tgnet.TLRPC$TL_messages_search r10 = new org.telegram.tgnet.TLRPC$TL_messages_search
                r10.<init>()
                r10.q = r3
                r10.limit = r0
                org.telegram.ui.Adapters.FiltersView$MediaFilterData r0 = r12.currentSearchFilter
                org.telegram.tgnet.TLRPC$MessagesFilter r0 = r0.filter
                r10.filter = r0
                org.telegram.messenger.MessagesController r0 = r24.getMessagesController()
                org.telegram.tgnet.TLRPC$InputPeer r0 = r0.getInputPeer((long) r6)
                r10.peer = r0
                int r0 = (r25 > r8 ? 1 : (r25 == r8 ? 0 : -1))
                if (r0 <= 0) goto L_0x0034
                long r13 = r25 / r4
                int r0 = (int) r13
                r10.min_date = r0
            L_0x0034:
                int r0 = (r27 > r8 ? 1 : (r27 == r8 ? 0 : -1))
                if (r0 <= 0) goto L_0x003d
                long r4 = r27 / r4
                int r0 = (int) r4
                r10.max_date = r0
            L_0x003d:
                if (r29 == 0) goto L_0x0064
                java.lang.String r0 = r12.lastMessagesSearchString
                boolean r0 = r3.equals(r0)
                if (r0 == 0) goto L_0x0064
                java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r12.messages
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x0064
                java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r12.messages
                int r1 = r0.size()
                int r1 = r1 + -1
                java.lang.Object r0 = r0.get(r1)
                org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
                int r0 = r0.getId()
                r10.offset_id = r0
                goto L_0x0066
            L_0x0064:
                r10.offset_id = r1
            L_0x0066:
                r13 = r10
                r10 = r2
                goto L_0x0106
            L_0x006a:
                boolean r10 = android.text.TextUtils.isEmpty(r23)
                if (r10 != 0) goto L_0x008d
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r17 = new java.util.ArrayList
                r17.<init>()
                java.util.ArrayList r18 = new java.util.ArrayList
                r18.<init>()
                org.telegram.messenger.MessagesStorage r13 = r24.getMessagesStorage()
                r14 = 0
                r19 = -1
                r15 = r23
                r16 = r2
                r13.localSearch(r14, r15, r16, r17, r18, r19)
            L_0x008d:
                org.telegram.tgnet.TLRPC$TL_messages_searchGlobal r10 = new org.telegram.tgnet.TLRPC$TL_messages_searchGlobal
                r10.<init>()
                r10.limit = r0
                r10.q = r3
                org.telegram.ui.Adapters.FiltersView$MediaFilterData r0 = r12.currentSearchFilter
                org.telegram.tgnet.TLRPC$MessagesFilter r0 = r0.filter
                r10.filter = r0
                int r0 = (r25 > r8 ? 1 : (r25 == r8 ? 0 : -1))
                if (r0 <= 0) goto L_0x00a5
                long r13 = r25 / r4
                int r0 = (int) r13
                r10.min_date = r0
            L_0x00a5:
                int r0 = (r27 > r8 ? 1 : (r27 == r8 ? 0 : -1))
                if (r0 <= 0) goto L_0x00ae
                long r4 = r27 / r4
                int r0 = (int) r4
                r10.max_date = r0
            L_0x00ae:
                if (r29 == 0) goto L_0x00f9
                java.lang.String r0 = r12.lastMessagesSearchString
                boolean r0 = r3.equals(r0)
                if (r0 == 0) goto L_0x00f9
                java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r12.messages
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00f9
                java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r12.messages
                int r1 = r0.size()
                int r1 = r1 + -1
                java.lang.Object r0 = r0.get(r1)
                org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
                int r1 = r0.getId()
                r10.offset_id = r1
                int r1 = r12.nextSearchRate
                r10.offset_rate = r1
                org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
                org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
                long r4 = r0.channel_id
                int r1 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
                if (r1 == 0) goto L_0x00e4
            L_0x00e2:
                long r0 = -r4
                goto L_0x00ed
            L_0x00e4:
                long r4 = r0.chat_id
                int r1 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
                if (r1 == 0) goto L_0x00eb
                goto L_0x00e2
            L_0x00eb:
                long r0 = r0.user_id
            L_0x00ed:
                org.telegram.messenger.MessagesController r4 = r24.getMessagesController()
                org.telegram.tgnet.TLRPC$InputPeer r0 = r4.getInputPeer((long) r0)
                r10.offset_peer = r0
                goto L_0x0066
            L_0x00f9:
                r10.offset_rate = r1
                r10.offset_id = r1
                org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
                r0.<init>()
                r10.offset_peer = r0
                goto L_0x0066
            L_0x0106:
                r12.lastMessagesSearchString = r3
                r0 = r30
                r12.lastSearchFilterQueryString = r0
                java.util.ArrayList r11 = new java.util.ArrayList
                r11.<init>()
                java.lang.String r0 = r12.lastMessagesSearchString
                org.telegram.ui.Adapters.FiltersView.fillTipDates(r0, r11)
                org.telegram.tgnet.ConnectionsManager r14 = r24.getConnectionsManager()
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda5 r15 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda5
                r0 = r15
                r1 = r20
                r2 = r24
                r3 = r23
                r4 = r31
                r5 = r29
                r6 = r21
                r8 = r25
                r0.<init>(r1, r2, r3, r4, r5, r6, r8, r10, r11)
                r14.sendRequest(r13, r15)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.lambda$searchGlobal$4(long, java.lang.String, org.telegram.messenger.AccountInstance, long, long, boolean, java.lang.String, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchGlobal$3(AccountInstance accountInstance, String str, int i, boolean z, long j, long j2, ArrayList arrayList, ArrayList arrayList2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            ArrayList arrayList3 = new ArrayList();
            if (tLRPC$TL_error == null) {
                TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                int size = tLRPC$messages_Messages.messages.size();
                for (int i2 = 0; i2 < size; i2++) {
                    MessageObject messageObject = new MessageObject(accountInstance.getCurrentAccount(), tLRPC$messages_Messages.messages.get(i2), false, true);
                    messageObject.setQuery(str);
                    arrayList3.add(messageObject);
                }
            }
            String str2 = str;
            AndroidUtilities.runOnUIThread(new ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda0(this, i, tLRPC$TL_error, tLObject, accountInstance, z, str, arrayList3, j, j2, arrayList, arrayList2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$searchGlobal$2(int i, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, AccountInstance accountInstance, boolean z, String str, ArrayList arrayList, long j, long j2, ArrayList arrayList2, ArrayList arrayList3) {
            boolean z2;
            String str2 = str;
            ArrayList arrayList4 = arrayList2;
            if (i == this.requestIndex) {
                this.isLoading = false;
                if (tLRPC$TL_error != null) {
                    ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", NUM));
                    ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                    ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
                    ChatAttachAlertDocumentLayout.this.emptyView.showProgress(false, true);
                    return;
                }
                ChatAttachAlertDocumentLayout.this.emptyView.showProgress(false);
                TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
                this.nextSearchRate = tLRPC$messages_Messages.next_rate;
                accountInstance.getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
                accountInstance.getMessagesController().putUsers(tLRPC$messages_Messages.users, false);
                accountInstance.getMessagesController().putChats(tLRPC$messages_Messages.chats, false);
                if (!z) {
                    this.messages.clear();
                    this.messagesById.clear();
                    this.sections.clear();
                    this.sectionArrays.clear();
                }
                int i2 = tLRPC$messages_Messages.count;
                this.currentDataQuery = str2;
                int size = arrayList.size();
                for (int i3 = 0; i3 < size; i3++) {
                    MessageObject messageObject = (MessageObject) arrayList.get(i3);
                    ArrayList arrayList5 = this.sectionArrays.get(messageObject.monthKey);
                    if (arrayList5 == null) {
                        arrayList5 = new ArrayList();
                        this.sectionArrays.put(messageObject.monthKey, arrayList5);
                        this.sections.add(messageObject.monthKey);
                    }
                    arrayList5.add(messageObject);
                    this.messages.add(messageObject);
                    this.messagesById.put(messageObject.getId(), messageObject);
                }
                if (this.messages.size() > i2) {
                    i2 = this.messages.size();
                }
                this.endReached = this.messages.size() >= i2;
                if (this.messages.isEmpty()) {
                    if (TextUtils.isEmpty(this.currentDataQuery) && j == 0 && j2 == 0) {
                        ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle", NUM));
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitleFiles", NUM));
                    } else {
                        ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", NUM));
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
                    }
                }
                if (!z) {
                    this.localTipChats.clear();
                    if (arrayList4 != null) {
                        this.localTipChats.addAll(arrayList4);
                    }
                    if (str.length() >= 3 && (LocaleController.getString("SavedMessages", NUM).toLowerCase().startsWith(str2) || "saved messages".startsWith(str2))) {
                        int i4 = 0;
                        while (true) {
                            if (i4 < this.localTipChats.size()) {
                                if ((this.localTipChats.get(i4) instanceof TLRPC$User) && UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == ((TLRPC$User) this.localTipChats.get(i4)).id) {
                                    z2 = true;
                                    break;
                                }
                                i4++;
                            } else {
                                z2 = false;
                                break;
                            }
                        }
                        if (!z2) {
                            this.localTipChats.add(0, UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser());
                        }
                    }
                    this.localTipDates.clear();
                    this.localTipDates.addAll(arrayList3);
                    updateFiltersView(TextUtils.isEmpty(this.currentDataQuery), this.localTipChats, this.localTipDates, true);
                }
                final View view = null;
                final int i5 = -1;
                for (int i6 = 0; i6 < size; i6++) {
                    View childAt = ChatAttachAlertDocumentLayout.this.listView.getChildAt(i6);
                    if (childAt instanceof FlickerLoadingView) {
                        i5 = ChatAttachAlertDocumentLayout.this.listView.getChildAdapterPosition(childAt);
                        view = childAt;
                    }
                }
                if (view != null) {
                    ChatAttachAlertDocumentLayout.this.listView.removeView(view);
                }
                if ((ChatAttachAlertDocumentLayout.this.loadingView.getVisibility() == 0 && ChatAttachAlertDocumentLayout.this.listView.getChildCount() <= 1) || view != null) {
                    final AccountInstance accountInstance2 = accountInstance;
                    ChatAttachAlertDocumentLayout.this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            ChatAttachAlertDocumentLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                            int childCount = ChatAttachAlertDocumentLayout.this.listView.getChildCount();
                            AnimatorSet animatorSet = new AnimatorSet();
                            for (int i = 0; i < childCount; i++) {
                                View childAt = ChatAttachAlertDocumentLayout.this.listView.getChildAt(i);
                                if (view == null || ChatAttachAlertDocumentLayout.this.listView.getChildAdapterPosition(childAt) >= i5) {
                                    childAt.setAlpha(0.0f);
                                    ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                                    ofFloat.setStartDelay((long) ((int) ((((float) Math.min(ChatAttachAlertDocumentLayout.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) ChatAttachAlertDocumentLayout.this.listView.getMeasuredHeight())) * 100.0f)));
                                    ofFloat.setDuration(200);
                                    animatorSet.playTogether(new Animator[]{ofFloat});
                                }
                            }
                            animatorSet.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    accountInstance2.getNotificationCenter().onAnimationFinish(SearchAdapter.this.animationIndex);
                                }
                            });
                            int unused = SearchAdapter.this.animationIndex = accountInstance2.getNotificationCenter().setAnimationInProgress(SearchAdapter.this.animationIndex, (int[]) null);
                            animatorSet.start();
                            View view = view;
                            if (view != null && view.getParent() == null) {
                                ChatAttachAlertDocumentLayout.this.listView.addView(view);
                                final RecyclerView.LayoutManager layoutManager = ChatAttachAlertDocumentLayout.this.listView.getLayoutManager();
                                if (layoutManager != null) {
                                    layoutManager.ignoreView(view);
                                    View view2 = view;
                                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view2, View.ALPHA, new float[]{view2.getAlpha(), 0.0f});
                                    ofFloat2.addListener(new AnimatorListenerAdapter() {
                                        public void onAnimationEnd(Animator animator) {
                                            view.setAlpha(1.0f);
                                            layoutManager.stopIgnoringView(view);
                                            ChatAttachAlertDocumentLayout.this.listView.removeView(view);
                                        }
                                    });
                                    ofFloat2.start();
                                }
                            }
                            return true;
                        }
                    });
                }
                notifyDataSetChanged();
            }
        }

        private void updateSearchResults(ArrayList<ListItem> arrayList, String str) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda4(this, arrayList));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$5(ArrayList arrayList) {
            if (ChatAttachAlertDocumentLayout.this.searching && ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.searchAdapter) {
                ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.searchAdapter);
            }
            this.searchResult = arrayList;
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 1 || itemViewType == 4;
        }

        public int getSectionCount() {
            if (!this.sections.isEmpty()) {
                return 2 + this.sections.size() + (this.endReached ^ true ? 1 : 0);
            }
            return 2;
        }

        public Object getItem(int i, int i2) {
            ArrayList arrayList;
            if (i != 0) {
                int i3 = i - 1;
                if (i3 >= this.sections.size() || (arrayList = this.sectionArrays.get(this.sections.get(i3))) == null) {
                    return null;
                }
                return arrayList.get(i2 - ((i3 != 0 || !this.searchResult.isEmpty()) ? 1 : 0));
            } else if (i2 < this.searchResult.size()) {
                return this.searchResult.get(i2);
            } else {
                return null;
            }
        }

        public int getCountForSection(int i) {
            if (i == 0) {
                return this.searchResult.size();
            }
            int i2 = i - 1;
            int i3 = 1;
            if (i2 >= this.sections.size()) {
                return 1;
            }
            ArrayList arrayList = this.sectionArrays.get(this.sections.get(i2));
            if (arrayList == null) {
                return 0;
            }
            int size = arrayList.size();
            if (i2 == 0 && this.searchResult.isEmpty()) {
                i3 = 0;
            }
            return size + i3;
        }

        public View getSectionHeaderView(int i, View view) {
            String str;
            GraySectionCell graySectionCell = (GraySectionCell) view;
            if (graySectionCell == null) {
                graySectionCell = new GraySectionCell(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                graySectionCell.setBackgroundColor(ChatAttachAlertDocumentLayout.this.getThemedColor("graySection") & -NUM);
            }
            if (i == 0 || (i == 1 && this.searchResult.isEmpty())) {
                graySectionCell.setAlpha(0.0f);
                return graySectionCell;
            }
            int i2 = i - 1;
            if (i2 < this.sections.size()) {
                graySectionCell.setAlpha(1.0f);
                ArrayList arrayList = this.sectionArrays.get(this.sections.get(i2));
                if (arrayList != null) {
                    MessageObject messageObject = (MessageObject) arrayList.get(0);
                    if (i2 != 0 || this.searchResult.isEmpty()) {
                        str = LocaleController.formatSectionDate((long) messageObject.messageOwner.date);
                    } else {
                        str = LocaleController.getString("GlobalSearch", NUM);
                    }
                    graySectionCell.setText(str);
                }
            }
            return view;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v11, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r4, int r5) {
            /*
                r3 = this;
                if (r5 == 0) goto L_0x003a
                r4 = 2
                r0 = 1
                if (r5 == r0) goto L_0x0026
                if (r5 == r4) goto L_0x0013
                r1 = 4
                if (r5 == r1) goto L_0x0026
                android.view.View r4 = new android.view.View
                android.content.Context r5 = r3.mContext
                r4.<init>(r5)
                goto L_0x0045
            L_0x0013:
                org.telegram.ui.Components.FlickerLoadingView r4 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r5 = r3.mContext
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r1 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r1.resourcesProvider
                r4.<init>(r5, r1)
                r5 = 3
                r4.setViewType(r5)
                r4.setIsSingleCell(r0)
                goto L_0x0045
            L_0x0026:
                org.telegram.ui.Cells.SharedDocumentCell r1 = new org.telegram.ui.Cells.SharedDocumentCell
                android.content.Context r2 = r3.mContext
                if (r5 != r0) goto L_0x002d
                r4 = 1
            L_0x002d:
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r5 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r5 = r5.resourcesProvider
                r1.<init>(r2, r4, r5)
                r4 = 0
                r1.setDrawDownloadIcon(r4)
                r4 = r1
                goto L_0x0045
            L_0x003a:
                org.telegram.ui.Cells.GraySectionCell r4 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r5 = r3.mContext
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r0 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r0.resourcesProvider
                r4.<init>(r5, r0)
            L_0x0045:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r5 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r5.<init>((int) r0, (int) r1)
                r4.setLayoutParams(r5)
                org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
                r5.<init>(r4)
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            String str;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 2 && itemViewType != 3) {
                boolean z = false;
                if (itemViewType == 0) {
                    int i3 = i - 1;
                    ArrayList arrayList = this.sectionArrays.get(this.sections.get(i3));
                    if (arrayList != null) {
                        MessageObject messageObject = (MessageObject) arrayList.get(0);
                        if (i3 != 0 || this.searchResult.isEmpty()) {
                            str = LocaleController.formatSectionDate((long) messageObject.messageOwner.date);
                        } else {
                            str = LocaleController.getString("GlobalSearch", NUM);
                        }
                        ((GraySectionCell) viewHolder.itemView).setText(str);
                    }
                } else if (itemViewType == 1 || itemViewType == 4) {
                    final SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                    if (i == 0) {
                        ListItem listItem = (ListItem) getItem(i2);
                        SharedDocumentCell sharedDocumentCell2 = (SharedDocumentCell) viewHolder.itemView;
                        int i4 = listItem.icon;
                        if (i4 != 0) {
                            sharedDocumentCell2.setTextAndValueAndTypeAndThumb(listItem.title, listItem.subtitle, (String) null, (String) null, i4, false);
                        } else {
                            sharedDocumentCell2.setTextAndValueAndTypeAndThumb(listItem.title, listItem.subtitle, listItem.ext.toUpperCase().substring(0, Math.min(listItem.ext.length(), 4)), listItem.thumb, 0, false);
                        }
                        if (listItem.file != null) {
                            sharedDocumentCell2.setChecked(ChatAttachAlertDocumentLayout.this.selectedFiles.containsKey(listItem.file.toString()), !ChatAttachAlertDocumentLayout.this.scrolling);
                        } else {
                            sharedDocumentCell2.setChecked(false, !ChatAttachAlertDocumentLayout.this.scrolling);
                        }
                    } else {
                        int i5 = i - 1;
                        if (i5 != 0 || !this.searchResult.isEmpty()) {
                            i2--;
                        }
                        ArrayList arrayList2 = this.sectionArrays.get(this.sections.get(i5));
                        if (arrayList2 != null) {
                            final MessageObject messageObject2 = (MessageObject) arrayList2.get(i2);
                            final boolean z2 = sharedDocumentCell.getMessage() != null && sharedDocumentCell.getMessage().getId() == messageObject2.getId();
                            if (i2 != arrayList2.size() - 1 || (i5 == this.sections.size() - 1 && this.isLoading)) {
                                z = true;
                            }
                            sharedDocumentCell.setDocument(messageObject2, z);
                            sharedDocumentCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                public boolean onPreDraw() {
                                    sharedDocumentCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                    if (ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.isActionModeShowed()) {
                                        SearchAdapter.this.messageHashIdTmp.set(messageObject2.getId(), messageObject2.getDialogId());
                                        sharedDocumentCell.setChecked(ChatAttachAlertDocumentLayout.this.selectedMessages.containsKey(SearchAdapter.this.messageHashIdTmp), z2);
                                        return true;
                                    }
                                    sharedDocumentCell.setChecked(false, z2);
                                    return true;
                                }
                            });
                        }
                    }
                }
            }
        }

        public int getItemViewType(int i, int i2) {
            if (i == 0) {
                return 1;
            }
            if (i == getSectionCount() - 1) {
                return 3;
            }
            int i3 = i - 1;
            if (i3 < this.sections.size()) {
                return ((i3 != 0 || !this.searchResult.isEmpty()) && i2 == 0) ? 0 : 4;
            }
            return 2;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertDocumentLayout.this.updateEmptyView();
        }

        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = 0;
            iArr[1] = 0;
        }
    }

    /* access modifiers changed from: package-private */
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.searchItem.getSearchField(), ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_folderIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_folderIconBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_iconText"));
        return arrayList;
    }
}
