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
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
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

    public ChatAttachAlertDocumentLayout(ChatAttachAlert chatAttachAlert, Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        this.allowMusic = z;
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
        AnonymousClass3 r1 = new StickerEmptyView(context, this.loadingView, 1, resourcesProvider) {
            public void setTranslationY(float f) {
                super.setTranslationY(f + ChatAttachAlertDocumentLayout.this.additionalTranslationY);
            }

            public float getTranslationY() {
                return super.getTranslationY() - ChatAttachAlertDocumentLayout.this.additionalTranslationY;
            }
        };
        this.emptyView = r1;
        addView(r1, LayoutHelper.createFrame(-1, -1.0f));
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

    /* JADX WARNING: type inference failed for: r3v2, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$new$1(android.view.View r13, int r14) {
        /*
            r12 = this;
            org.telegram.ui.Components.RecyclerListView r0 = r12.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r1 = r12.listAdapter
            if (r0 != r1) goto L_0x000f
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r14 = r1.getItem(r14)
            goto L_0x0015
        L_0x000f:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter r0 = r12.searchAdapter
            java.lang.Object r14 = r0.getItem(r14)
        L_0x0015:
            boolean r0 = r14 instanceof org.telegram.ui.Components.ChatAttachAlertDocumentLayout.ListItem
            if (r0 == 0) goto L_0x0107
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r14 = (org.telegram.ui.Components.ChatAttachAlertDocumentLayout.ListItem) r14
            java.io.File r0 = r14.file
            r1 = 0
            r2 = 0
            if (r0 != 0) goto L_0x00bb
            int r13 = r14.icon
            r14 = 2131165405(0x7var_dd, float:1.7945026E38)
            r0 = 1
            if (r13 != r14) goto L_0x006f
            java.util.HashMap r13 = new java.util.HashMap
            r13.<init>()
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            org.telegram.ui.Components.ChatAttachAlert r3 = r12.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r3 = r3.baseFragment
            boolean r4 = r3 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x003e
            r1 = r3
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
        L_0x003e:
            r10 = r1
            org.telegram.ui.PhotoPickerActivity r1 = new org.telegram.ui.PhotoPickerActivity
            r4 = 0
            org.telegram.messenger.MediaController$AlbumEntry r5 = org.telegram.messenger.MediaController.allMediaAlbumEntry
            r8 = 0
            if (r10 == 0) goto L_0x0049
            r9 = 1
            goto L_0x004a
        L_0x0049:
            r9 = 0
        L_0x004a:
            r11 = 0
            r3 = r1
            r6 = r13
            r7 = r14
            r3.<init>(r4, r5, r6, r7, r8, r9, r10, r11)
            r1.setDocumentsPicker(r0)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$6 r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$6
            r0.<init>(r13, r14)
            r1.setDelegate(r0)
            int r13 = r12.maxSelectedFiles
            r1.setMaxSelectedPhotos(r13, r2)
            org.telegram.ui.Components.ChatAttachAlert r13 = r12.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r13 = r13.baseFragment
            r13.presentFragment(r1)
            org.telegram.ui.Components.ChatAttachAlert r13 = r12.parentAlert
            r13.dismiss()
            goto L_0x010a
        L_0x006f:
            r14 = 2131165407(0x7var_df, float:1.794503E38)
            if (r13 != r14) goto L_0x007d
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$DocumentSelectActivityDelegate r13 = r12.delegate
            if (r13 == 0) goto L_0x010a
            r13.startMusicSelectActivity()
            goto L_0x010a
        L_0x007d:
            boolean r14 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
            if (r14 != 0) goto L_0x008d
            r14 = 2131165408(0x7var_e0, float:1.7945032E38)
            if (r13 != r14) goto L_0x008d
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$DocumentSelectActivityDelegate r13 = r12.delegate
            r13.startDocumentSelectActivity()
            goto L_0x010a
        L_0x008d:
            int r13 = r12.getTopForScroll()
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry> r14 = r12.history
            int r1 = r14.size()
            int r1 = r1 - r0
            java.lang.Object r14 = r14.remove(r1)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry r14 = (org.telegram.ui.Components.ChatAttachAlertDocumentLayout.HistoryEntry) r14
            org.telegram.ui.Components.ChatAttachAlert r0 = r12.parentAlert
            org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
            java.lang.String r1 = r14.title
            r0.setTitle(r1)
            java.io.File r14 = r14.dir
            if (r14 == 0) goto L_0x00af
            r12.listFiles(r14)
            goto L_0x00b2
        L_0x00af:
            r12.listRoots()
        L_0x00b2:
            r12.updateSearchButton()
            androidx.recyclerview.widget.LinearLayoutManager r14 = r12.layoutManager
            r14.scrollToPositionWithOffset(r2, r13)
            goto L_0x010a
        L_0x00bb:
            boolean r3 = r0.isDirectory()
            if (r3 == 0) goto L_0x0103
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry r13 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry
            r13.<init>()
            org.telegram.ui.Components.RecyclerListView r1 = r12.listView
            android.view.View r1 = r1.getChildAt(r2)
            org.telegram.ui.Components.RecyclerListView r2 = r12.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findContainingViewHolder(r1)
            if (r2 == 0) goto L_0x010a
            r2.getAdapterPosition()
            r1.getTop()
            java.io.File r1 = r12.currentDir
            r13.dir = r1
            org.telegram.ui.Components.ChatAttachAlert r1 = r12.parentAlert
            org.telegram.ui.ActionBar.ActionBar r1 = r1.actionBar
            java.lang.String r1 = r1.getTitle()
            r13.title = r1
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry> r1 = r12.history
            r1.add(r13)
            boolean r0 = r12.listFiles(r0)
            if (r0 != 0) goto L_0x00f9
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry> r14 = r12.history
            r14.remove(r13)
            return
        L_0x00f9:
            org.telegram.ui.Components.ChatAttachAlert r13 = r12.parentAlert
            org.telegram.ui.ActionBar.ActionBar r13 = r13.actionBar
            java.lang.String r14 = r14.title
            r13.setTitle(r14)
            goto L_0x010a
        L_0x0103:
            r12.onItemClick(r13, r14)
            goto L_0x010a
        L_0x0107:
            r12.onItemClick(r13, r14)
        L_0x010a:
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
            this.parentAlert.dismiss();
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
            } else if (listItem.file.length() == 0) {
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

    public void setMaxSelectedFiles(int i) {
        this.maxSelectedFiles = i;
    }

    public void setCanSelectOnlyImageFiles(boolean z) {
        this.canSelectOnlyImageFiles = true;
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

    public void loadRecentFiles() {
        try {
            File[] listFiles = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (!file.isDirectory()) {
                        ListItem listItem = new ListItem();
                        listItem.title = file.getName();
                        listItem.file = file;
                        String name = file.getName();
                        String[] split = name.split("\\.");
                        listItem.ext = split.length > 1 ? split[split.length - 1] : "?";
                        listItem.subtitle = AndroidUtilities.formatFileSize(file.length());
                        String lowerCase = name.toLowerCase();
                        if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif") || lowerCase.endsWith(".jpeg")) {
                            listItem.thumb = file.getAbsolutePath();
                        }
                        this.recentItems.add(listItem);
                    }
                }
            }
            sortRecentItems();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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
    public void onShow() {
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
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01b1 A[Catch:{ Exception -> 0x01d0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01ff  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0243 A[SYNTHETIC, Splitter:B:87:0x0243] */
    @android.annotation.SuppressLint({"NewApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void listRoots() {
        /*
            r16 = this;
            r1 = r16
            java.lang.String r2 = "Telegram"
            r3 = 0
            r1.currentDir = r3
            r0 = 0
            r1.hasFiles = r0
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r0 = r1.items
            r0.clear()
            java.util.HashSet r4 = new java.util.HashSet
            r4.<init>()
            boolean r0 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
            r5 = 2131625950(0x7f0e07de, float:1.8879122E38)
            java.lang.String r6 = "InternalFolderInfo"
            r7 = 2131165408(0x7var_e0, float:1.7945032E38)
            r8 = 2131625951(0x7f0e07df, float:1.8879124E38)
            java.lang.String r9 = "InternalStorage"
            if (r0 != 0) goto L_0x003f
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r0.<init>()
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.title = r4
            r0.icon = r7
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.subtitle = r4
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r4 = r1.items
            r4.add(r0)
            goto L_0x01a2
        L_0x003f:
            java.io.File r0 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r0 = r0.getPath()
            java.lang.String r10 = android.os.Environment.getExternalStorageState()
            java.lang.String r11 = "mounted"
            boolean r11 = r10.equals(r11)
            r12 = 2131625538(0x7f0e0642, float:1.8878287E38)
            java.lang.String r13 = "ExternalFolderInfo"
            r14 = 2131165406(0x7var_de, float:1.7945028E38)
            r15 = 2131627547(0x7f0e0e1b, float:1.8882362E38)
            java.lang.String r5 = "SdCard"
            if (r11 != 0) goto L_0x0068
            java.lang.String r11 = "mounted_ro"
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x00a1
        L_0x0068:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r10 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r10.<init>()
            boolean r11 = android.os.Environment.isExternalStorageRemovable()
            if (r11 == 0) goto L_0x0082
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r5, r15)
            r10.title = r6
            r10.icon = r14
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r10.subtitle = r6
            goto L_0x0093
        L_0x0082:
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r10.title = r8
            r10.icon = r7
            r7 = 2131625950(0x7f0e07de, float:1.8879122E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)
            r10.subtitle = r6
        L_0x0093:
            java.io.File r6 = android.os.Environment.getExternalStorageDirectory()
            r10.file = r6
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r6 = r1.items
            r6.add(r10)
            r4.add(r0)
        L_0x00a1:
            java.io.BufferedReader r6 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0192, all -> 0x018e }
            java.io.FileReader r0 = new java.io.FileReader     // Catch:{ Exception -> 0x0192, all -> 0x018e }
            java.lang.String r7 = "/proc/mounts"
            r0.<init>(r7)     // Catch:{ Exception -> 0x0192, all -> 0x018e }
            r6.<init>(r0)     // Catch:{ Exception -> 0x0192, all -> 0x018e }
        L_0x00ad:
            java.lang.String r0 = r6.readLine()     // Catch:{ Exception -> 0x018c }
            if (r0 == 0) goto L_0x0188
            java.lang.String r7 = "vfat"
            boolean r7 = r0.contains(r7)     // Catch:{ Exception -> 0x018c }
            if (r7 != 0) goto L_0x00c3
            java.lang.String r7 = "/mnt"
            boolean r7 = r0.contains(r7)     // Catch:{ Exception -> 0x018c }
            if (r7 == 0) goto L_0x00ad
        L_0x00c3:
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x018c }
            if (r7 == 0) goto L_0x00ca
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x018c }
        L_0x00ca:
            java.util.StringTokenizer r7 = new java.util.StringTokenizer     // Catch:{ Exception -> 0x018c }
            java.lang.String r8 = " "
            r7.<init>(r0, r8)     // Catch:{ Exception -> 0x018c }
            r7.nextToken()     // Catch:{ Exception -> 0x018c }
            java.lang.String r7 = r7.nextToken()     // Catch:{ Exception -> 0x018c }
            boolean r8 = r4.contains(r7)     // Catch:{ Exception -> 0x018c }
            if (r8 == 0) goto L_0x00df
            goto L_0x00ad
        L_0x00df:
            java.lang.String r8 = "/dev/block/vold"
            boolean r8 = r0.contains(r8)     // Catch:{ Exception -> 0x018c }
            if (r8 == 0) goto L_0x00ad
            java.lang.String r8 = "/mnt/secure"
            boolean r8 = r0.contains(r8)     // Catch:{ Exception -> 0x018c }
            if (r8 != 0) goto L_0x00ad
            java.lang.String r8 = "/mnt/asec"
            boolean r8 = r0.contains(r8)     // Catch:{ Exception -> 0x018c }
            if (r8 != 0) goto L_0x00ad
            java.lang.String r8 = "/mnt/obb"
            boolean r8 = r0.contains(r8)     // Catch:{ Exception -> 0x018c }
            if (r8 != 0) goto L_0x00ad
            java.lang.String r8 = "/dev/mapper"
            boolean r8 = r0.contains(r8)     // Catch:{ Exception -> 0x018c }
            if (r8 != 0) goto L_0x00ad
            java.lang.String r8 = "tmpfs"
            boolean r0 = r0.contains(r8)     // Catch:{ Exception -> 0x018c }
            if (r0 != 0) goto L_0x00ad
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x018c }
            r0.<init>(r7)     // Catch:{ Exception -> 0x018c }
            boolean r0 = r0.isDirectory()     // Catch:{ Exception -> 0x018c }
            if (r0 != 0) goto L_0x0146
            r0 = 47
            int r0 = r7.lastIndexOf(r0)     // Catch:{ Exception -> 0x018c }
            r8 = -1
            if (r0 == r8) goto L_0x0146
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x018c }
            r8.<init>()     // Catch:{ Exception -> 0x018c }
            java.lang.String r9 = "/storage/"
            r8.append(r9)     // Catch:{ Exception -> 0x018c }
            int r0 = r0 + 1
            java.lang.String r0 = r7.substring(r0)     // Catch:{ Exception -> 0x018c }
            r8.append(r0)     // Catch:{ Exception -> 0x018c }
            java.lang.String r0 = r8.toString()     // Catch:{ Exception -> 0x018c }
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x018c }
            r8.<init>(r0)     // Catch:{ Exception -> 0x018c }
            boolean r8 = r8.isDirectory()     // Catch:{ Exception -> 0x018c }
            if (r8 == 0) goto L_0x0146
            r7 = r0
        L_0x0146:
            r4.add(r7)     // Catch:{ Exception -> 0x018c }
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem     // Catch:{ Exception -> 0x0182 }
            r0.<init>()     // Catch:{ Exception -> 0x0182 }
            java.lang.String r8 = r7.toLowerCase()     // Catch:{ Exception -> 0x0182 }
            java.lang.String r9 = "sd"
            boolean r8 = r8.contains(r9)     // Catch:{ Exception -> 0x0182 }
            if (r8 == 0) goto L_0x0161
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r5, r15)     // Catch:{ Exception -> 0x0182 }
            r0.title = r8     // Catch:{ Exception -> 0x0182 }
            goto L_0x016c
        L_0x0161:
            java.lang.String r8 = "ExternalStorage"
            r9 = 2131625539(0x7f0e0643, float:1.8878289E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ Exception -> 0x0182 }
            r0.title = r8     // Catch:{ Exception -> 0x0182 }
        L_0x016c:
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r13, r12)     // Catch:{ Exception -> 0x0182 }
            r0.subtitle = r8     // Catch:{ Exception -> 0x0182 }
            r0.icon = r14     // Catch:{ Exception -> 0x0182 }
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0182 }
            r8.<init>(r7)     // Catch:{ Exception -> 0x0182 }
            r0.file = r8     // Catch:{ Exception -> 0x0182 }
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r7 = r1.items     // Catch:{ Exception -> 0x0182 }
            r7.add(r0)     // Catch:{ Exception -> 0x0182 }
            goto L_0x00ad
        L_0x0182:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x018c }
            goto L_0x00ad
        L_0x0188:
            r6.close()     // Catch:{ Exception -> 0x019d }
            goto L_0x01a2
        L_0x018c:
            r0 = move-exception
            goto L_0x0194
        L_0x018e:
            r0 = move-exception
            r2 = r0
            goto L_0x0241
        L_0x0192:
            r0 = move-exception
            r6 = r3
        L_0x0194:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x023e }
            if (r6 == 0) goto L_0x01a2
            r6.close()     // Catch:{ Exception -> 0x019d }
            goto L_0x01a2
        L_0x019d:
            r0 = move-exception
            r4 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x01a2:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x01d0 }
            java.io.File r4 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x01d0 }
            r0.<init>(r4, r2)     // Catch:{ Exception -> 0x01d0 }
            boolean r4 = r0.exists()     // Catch:{ Exception -> 0x01d0 }
            if (r4 == 0) goto L_0x01d4
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r4 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem     // Catch:{ Exception -> 0x01d0 }
            r4.<init>()     // Catch:{ Exception -> 0x01d0 }
            r4.title = r2     // Catch:{ Exception -> 0x01d0 }
            java.lang.String r2 = "AppFolderInfo"
            r5 = 2131624290(0x7f0e0162, float:1.8875756E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ Exception -> 0x01d0 }
            r4.subtitle = r2     // Catch:{ Exception -> 0x01d0 }
            r2 = 2131165404(0x7var_dc, float:1.7945024E38)
            r4.icon = r2     // Catch:{ Exception -> 0x01d0 }
            r4.file = r0     // Catch:{ Exception -> 0x01d0 }
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r0 = r1.items     // Catch:{ Exception -> 0x01d0 }
            r0.add(r4)     // Catch:{ Exception -> 0x01d0 }
            goto L_0x01d4
        L_0x01d0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01d4:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r0.<init>()
            r2 = 2131625769(0x7f0e0729, float:1.8878755E38)
            java.lang.String r4 = "Gallery"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.title = r2
            r2 = 2131625770(0x7f0e072a, float:1.8878757E38)
            java.lang.String r4 = "GalleryInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.subtitle = r2
            r2 = 2131165405(0x7var_dd, float:1.7945026E38)
            r0.icon = r2
            r0.file = r3
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r2 = r1.items
            r2.add(r0)
            boolean r0 = r1.allowMusic
            if (r0 == 0) goto L_0x0226
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r0.<init>()
            r2 = 2131624410(0x7f0e01da, float:1.8875999E38)
            java.lang.String r4 = "AttachMusic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.title = r2
            r2 = 2131626379(0x7f0e098b, float:1.8879993E38)
            java.lang.String r4 = "MusicInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.subtitle = r2
            r2 = 2131165407(0x7var_df, float:1.794503E38)
            r0.icon = r2
            r0.file = r3
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r2 = r1.items
            r2.add(r0)
        L_0x0226:
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r0 = r1.recentItems
            boolean r0 = r0.isEmpty()
            r2 = 1
            if (r0 != 0) goto L_0x0231
            r1.hasFiles = r2
        L_0x0231:
            org.telegram.ui.Components.RecyclerListView r0 = r1.listView
            org.telegram.messenger.AndroidUtilities.clearDrawableAnimation(r0)
            r1.scrolling = r2
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r0 = r1.listAdapter
            r0.notifyDataSetChanged()
            return
        L_0x023e:
            r0 = move-exception
            r2 = r0
            r3 = r6
        L_0x0241:
            if (r3 == 0) goto L_0x024c
            r3.close()     // Catch:{ Exception -> 0x0247 }
            goto L_0x024c
        L_0x0247:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x024c:
            goto L_0x024e
        L_0x024d:
            throw r2
        L_0x024e:
            goto L_0x024d
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
