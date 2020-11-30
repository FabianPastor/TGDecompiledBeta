package org.telegram.ui.Components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.RecyclerListView;

public class ChatAttachAlertDocumentLayout extends ChatAttachAlert.AttachAlertLayout {
    private boolean allowMusic;
    private boolean canSelectOnlyImageFiles;
    /* access modifiers changed from: private */
    public File currentDir;
    /* access modifiers changed from: private */
    public DocumentSelectActivityDelegate delegate;
    private ImageView emptyImageView;
    /* access modifiers changed from: private */
    public TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    private boolean hasFiles;
    /* access modifiers changed from: private */
    public ArrayList<HistoryEntry> history = new ArrayList<>();
    private boolean ignoreLayout;
    /* access modifiers changed from: private */
    public ArrayList<ListItem> items = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private int maxSelectedFiles = -1;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            $$Lambda$ChatAttachAlertDocumentLayout$1$f3hr_nbX6ALeTmaPbT6gulIKuc r3 = new Runnable() {
                public final void run() {
                    ChatAttachAlertDocumentLayout.AnonymousClass1.this.lambda$onReceive$0$ChatAttachAlertDocumentLayout$1();
                }
            };
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                ChatAttachAlertDocumentLayout.this.listView.postDelayed(r3, 1000);
            } else {
                r3.run();
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onReceive$0 */
        public /* synthetic */ void lambda$onReceive$0$ChatAttachAlertDocumentLayout$1() {
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

        void didSelectFiles(ArrayList<String> arrayList, String str, boolean z, int i);

        void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i);

        void startDocumentSelectActivity();

        void startMusicSelectActivity();
    }

    static /* synthetic */ boolean lambda$new$0(View view, MotionEvent motionEvent) {
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChatAttachAlertDocumentLayout(ChatAttachAlert chatAttachAlert, Context context, boolean z) {
        super(chatAttachAlert, context);
        Context context2 = context;
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
        ActionBarMenuItem addItem = createMenu.addItem(0, NUM);
        addItem.setIsSearchField(true);
        addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
                ChatAttachAlertDocumentLayout.this.searchAdapter.search((String) null);
            }

            public void onTextChanged(EditText editText) {
                ChatAttachAlertDocumentLayout.this.searchAdapter.search(editText.getText().toString());
            }
        });
        this.searchItem = addItem;
        addItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(Theme.getColor("dialogTextBlack"));
        searchField.setCursorColor(Theme.getColor("dialogTextBlack"));
        searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        ActionBarMenuItem addItem2 = createMenu.addItem(6, this.sortByName ? NUM : NUM);
        this.sortItem = addItem2;
        addItem2.setContentDescription(LocaleController.getString("AccDescrContactSorting", NUM));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener($$Lambda$ChatAttachAlertDocumentLayout$UzXO3YpFzwU2u1b1zfUtDODa94.INSTANCE);
        ImageView imageView = new ImageView(context2);
        this.emptyImageView = imageView;
        imageView.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogEmptyImage"), PorterDuff.Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        TextView textView = new TextView(context2);
        this.emptyTitleTextView = textView;
        textView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        TextView textView2 = new TextView(context2);
        this.emptySubtitleTextView = textView2;
        textView2.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass3 r0 = new FillLastLinearLayoutManager(context, 1, false, AndroidUtilities.dp(56.0f), this.listView) {
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
        this.layoutManager = r0;
        recyclerListView2.setLayoutManager(r0);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setClipToPadding(false);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView3.setAdapter(listAdapter2);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchAdapter = new SearchAdapter(context2);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlertDocumentLayout.this;
                chatAttachAlertDocumentLayout.parentAlert.updateLayout(chatAttachAlertDocumentLayout, true, i2);
                ChatAttachAlertDocumentLayout.this.updateEmptyViewPosition();
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ChatAttachAlertDocumentLayout.this.lambda$new$1$ChatAttachAlertDocumentLayout(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ChatAttachAlertDocumentLayout.this.lambda$new$2$ChatAttachAlertDocumentLayout(view, i);
            }
        });
        listRoots();
        updateSearchButton();
        updateEmptyView();
    }

    /* JADX WARNING: type inference failed for: r3v2, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* renamed from: lambda$new$1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$new$1$ChatAttachAlertDocumentLayout(android.view.View r12, int r13) {
        /*
            r11 = this;
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r0 = r0.getAdapter()
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r1 = r11.listAdapter
            if (r0 != r1) goto L_0x000f
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r13 = r1.getItem(r13)
            goto L_0x0015
        L_0x000f:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter r0 = r11.searchAdapter
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r13 = r0.getItem(r13)
        L_0x0015:
            if (r13 != 0) goto L_0x0018
            return
        L_0x0018:
            java.io.File r0 = r13.file
            r1 = 0
            r2 = 0
            if (r0 != 0) goto L_0x00a7
            int r12 = r13.icon
            r13 = 2131165401(0x7var_d9, float:1.7945018E38)
            r0 = 1
            if (r12 != r13) goto L_0x006b
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>()
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            org.telegram.ui.Components.ChatAttachAlert r3 = r11.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r3 = r3.baseFragment
            boolean r4 = r3 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x003b
            r1 = r3
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
        L_0x003b:
            r10 = r1
            org.telegram.ui.PhotoPickerActivity r1 = new org.telegram.ui.PhotoPickerActivity
            r4 = 0
            org.telegram.messenger.MediaController$AlbumEntry r5 = org.telegram.messenger.MediaController.allMediaAlbumEntry
            r8 = 0
            if (r10 == 0) goto L_0x0046
            r9 = 1
            goto L_0x0047
        L_0x0046:
            r9 = 0
        L_0x0047:
            r3 = r1
            r6 = r12
            r7 = r13
            r3.<init>(r4, r5, r6, r7, r8, r9, r10)
            r1.setDocumentsPicker(r0)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$5 r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$5
            r0.<init>(r12, r13)
            r1.setDelegate(r0)
            int r12 = r11.maxSelectedFiles
            r1.setMaxSelectedPhotos(r12, r2)
            org.telegram.ui.Components.ChatAttachAlert r12 = r11.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r12 = r12.baseFragment
            r12.presentFragment(r1)
            org.telegram.ui.Components.ChatAttachAlert r12 = r11.parentAlert
            r12.dismiss()
            goto L_0x00f0
        L_0x006b:
            r13 = 2131165403(0x7var_db, float:1.7945022E38)
            if (r12 != r13) goto L_0x0079
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$DocumentSelectActivityDelegate r12 = r11.delegate
            if (r12 == 0) goto L_0x00f0
            r12.startMusicSelectActivity()
            goto L_0x00f0
        L_0x0079:
            int r12 = r11.getTopForScroll()
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry> r13 = r11.history
            int r1 = r13.size()
            int r1 = r1 - r0
            java.lang.Object r13 = r13.remove(r1)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry r13 = (org.telegram.ui.Components.ChatAttachAlertDocumentLayout.HistoryEntry) r13
            org.telegram.ui.Components.ChatAttachAlert r0 = r11.parentAlert
            org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
            java.lang.String r1 = r13.title
            r0.setTitle(r1)
            java.io.File r13 = r13.dir
            if (r13 == 0) goto L_0x009b
            r11.listFiles(r13)
            goto L_0x009e
        L_0x009b:
            r11.listRoots()
        L_0x009e:
            r11.updateSearchButton()
            androidx.recyclerview.widget.LinearLayoutManager r13 = r11.layoutManager
            r13.scrollToPositionWithOffset(r2, r12)
            goto L_0x00f0
        L_0x00a7:
            boolean r3 = r0.isDirectory()
            if (r3 == 0) goto L_0x00ed
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry r12 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry
            r12.<init>()
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            android.view.View r1 = r1.getChildAt(r2)
            org.telegram.ui.Components.RecyclerListView r2 = r11.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findContainingViewHolder(r1)
            r2.getAdapterPosition()
            r1.getTop()
            java.io.File r1 = r11.currentDir
            r12.dir = r1
            org.telegram.ui.Components.ChatAttachAlert r1 = r11.parentAlert
            org.telegram.ui.ActionBar.ActionBar r1 = r1.actionBar
            java.lang.String r1 = r1.getTitle()
            r12.title = r1
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry> r1 = r11.history
            r1.add(r12)
            boolean r0 = r11.listFiles(r0)
            if (r0 != 0) goto L_0x00e3
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$HistoryEntry> r13 = r11.history
            r13.remove(r12)
            return
        L_0x00e3:
            org.telegram.ui.Components.ChatAttachAlert r12 = r11.parentAlert
            org.telegram.ui.ActionBar.ActionBar r12 = r12.actionBar
            java.lang.String r13 = r13.title
            r12.setTitle(r13)
            goto L_0x00f0
        L_0x00ed:
            r11.onItemClick(r12, r13)
        L_0x00f0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.lambda$new$1$ChatAttachAlertDocumentLayout(android.view.View, int):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$2 */
    public /* synthetic */ boolean lambda$new$2$ChatAttachAlertDocumentLayout(View view, int i) {
        ListItem listItem;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ListAdapter listAdapter2 = this.listAdapter;
        if (adapter == listAdapter2) {
            listItem = listAdapter2.getItem(i);
        } else {
            listItem = this.searchAdapter.getItem(i);
        }
        return onItemClick(view, listItem);
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
        return this.selectedFiles.size();
    }

    /* access modifiers changed from: package-private */
    public void sendSelectedItems(boolean z, int i) {
        if (this.selectedFiles.size() != 0 && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            this.delegate.didSelectFiles(new ArrayList(this.selectedFilesOrder), this.parentAlert.commentTextView.getText().toString(), z, i);
            this.parentAlert.dismiss();
        }
    }

    private boolean onItemClick(View view, ListItem listItem) {
        File file;
        boolean z;
        int i;
        if (listItem == null || (file = listItem.file) == null || file.isDirectory()) {
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
            sortRecentItems();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void sortRecentItems() {
        Collections.sort(this.recentItems, new Object() {
            public final int compare(Object obj, Object obj2) {
                return ChatAttachAlertDocumentLayout.this.lambda$sortRecentItems$3$ChatAttachAlertDocumentLayout((ChatAttachAlertDocumentLayout.ListItem) obj, (ChatAttachAlertDocumentLayout.ListItem) obj2);
            }

            public /* synthetic */ Comparator reversed() {
                return Comparator.CC.$default$reversed(this);
            }

            public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
            }

            public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                return Comparator.CC.$default$thenComparing(this, function, comparator);
            }

            public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
            }

            public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
            }

            public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
            }

            public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sortRecentItems$3 */
    public /* synthetic */ int lambda$sortRecentItems$3$ChatAttachAlertDocumentLayout(ListItem listItem, ListItem listItem2) {
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
            Collections.sort(this.items, new Object() {
                public final int compare(Object obj, Object obj2) {
                    return ChatAttachAlertDocumentLayout.this.lambda$sortFileItems$4$ChatAttachAlertDocumentLayout((ChatAttachAlertDocumentLayout.ListItem) obj, (ChatAttachAlertDocumentLayout.ListItem) obj2);
                }

                public /* synthetic */ java.util.Comparator reversed() {
                    return Comparator.CC.$default$reversed(this);
                }

                public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                    return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                }

                public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                    return Comparator.CC.$default$thenComparing(this, function, comparator);
                }

                public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                    return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                }

                public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                    return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                }

                public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                    return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                }

                public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                    return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sortFileItems$4 */
    public /* synthetic */ int lambda$sortFileItems$4$ChatAttachAlertDocumentLayout(ListItem listItem, ListItem listItem2) {
        File file = listItem.file;
        if (file == null) {
            return -1;
        }
        File file2 = listItem2.file;
        if (file2 == null) {
            return 1;
        }
        if (file == null && file2 == null) {
            return 0;
        }
        boolean isDirectory = file.isDirectory();
        boolean isDirectory2 = listItem2.file.isDirectory();
        if (isDirectory != isDirectory2) {
            if (isDirectory) {
                return -1;
            }
            return 1;
        } else if ((isDirectory && isDirectory2) || this.sortByName) {
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
            LinearLayout linearLayout = this.emptyView;
            linearLayout.setTranslationY((float) (((linearLayout.getMeasuredHeight() - getMeasuredHeight()) + childAt.getTop()) / 2));
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        boolean z;
        if (this.searching) {
            this.emptyTitleTextView.setText(LocaleController.getString("NoFilesFound", NUM));
        } else {
            this.emptyTitleTextView.setText(LocaleController.getString("NoFilesFound", NUM));
            this.emptySubtitleTextView.setText(LocaleController.getString("NoFilesInfo", NUM));
        }
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter2 = this.searchAdapter;
        int i = 0;
        if (adapter == searchAdapter2) {
            z = searchAdapter2.searchResult.isEmpty();
        } else {
            z = this.listAdapter.getItemCount() == 1;
        }
        LinearLayout linearLayout = this.emptyView;
        if (!z) {
            i = 8;
        }
        linearLayout.setVisibility(i);
        updateEmptyViewPosition();
    }

    /* access modifiers changed from: private */
    public void updateSearchButton() {
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            if (!actionBarMenuItem.isSearchFieldVisible()) {
                this.searchItem.setVisibility(this.hasFiles ? 0 : 8);
            }
            if (this.history.isEmpty()) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchRecentFiles", NUM));
            } else {
                this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
            }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(str);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.show();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x018f A[Catch:{ Exception -> 0x01ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x020d  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0220 A[SYNTHETIC, Splitter:B:83:0x0220] */
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
            java.io.File r3 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r3 = r3.getPath()
            android.os.Environment.isExternalStorageRemovable()
            java.lang.String r4 = android.os.Environment.getExternalStorageState()
            java.lang.String r5 = "mounted"
            boolean r5 = r4.equals(r5)
            r6 = 2131625341(0x7f0e057d, float:1.8877887E38)
            java.lang.String r7 = "ExternalFolderInfo"
            r8 = 2131165402(0x7var_da, float:1.794502E38)
            r9 = 2131626985(0x7f0e0be9, float:1.8881222E38)
            java.lang.String r10 = "SdCard"
            if (r5 != 0) goto L_0x003e
            java.lang.String r5 = "mounted_ro"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0081
        L_0x003e:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r4 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r4.<init>()
            boolean r5 = android.os.Environment.isExternalStorageRemovable()
            if (r5 == 0) goto L_0x0058
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r4.title = r5
            r4.icon = r8
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r4.subtitle = r5
            goto L_0x0073
        L_0x0058:
            r5 = 2131625656(0x7f0e06b8, float:1.8878526E38)
            java.lang.String r11 = "InternalStorage"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r4.title = r5
            r5 = 2131165404(0x7var_dc, float:1.7945024E38)
            r4.icon = r5
            r5 = 2131625655(0x7f0e06b7, float:1.8878524E38)
            java.lang.String r11 = "InternalFolderInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r4.subtitle = r5
        L_0x0073:
            java.io.File r5 = android.os.Environment.getExternalStorageDirectory()
            r4.file = r5
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r5 = r13.items
            r5.add(r4)
            r2.add(r3)
        L_0x0081:
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0171, all -> 0x016e }
            java.io.FileReader r4 = new java.io.FileReader     // Catch:{ Exception -> 0x0171, all -> 0x016e }
            java.lang.String r5 = "/proc/mounts"
            r4.<init>(r5)     // Catch:{ Exception -> 0x0171, all -> 0x016e }
            r3.<init>(r4)     // Catch:{ Exception -> 0x0171, all -> 0x016e }
        L_0x008d:
            java.lang.String r4 = r3.readLine()     // Catch:{ Exception -> 0x016c }
            if (r4 == 0) goto L_0x0168
            java.lang.String r5 = "vfat"
            boolean r5 = r4.contains(r5)     // Catch:{ Exception -> 0x016c }
            if (r5 != 0) goto L_0x00a3
            java.lang.String r5 = "/mnt"
            boolean r5 = r4.contains(r5)     // Catch:{ Exception -> 0x016c }
            if (r5 == 0) goto L_0x008d
        L_0x00a3:
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x016c }
            if (r5 == 0) goto L_0x00aa
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x016c }
        L_0x00aa:
            java.util.StringTokenizer r5 = new java.util.StringTokenizer     // Catch:{ Exception -> 0x016c }
            java.lang.String r11 = " "
            r5.<init>(r4, r11)     // Catch:{ Exception -> 0x016c }
            r5.nextToken()     // Catch:{ Exception -> 0x016c }
            java.lang.String r5 = r5.nextToken()     // Catch:{ Exception -> 0x016c }
            boolean r11 = r2.contains(r5)     // Catch:{ Exception -> 0x016c }
            if (r11 == 0) goto L_0x00bf
            goto L_0x008d
        L_0x00bf:
            java.lang.String r11 = "/dev/block/vold"
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x016c }
            if (r11 == 0) goto L_0x008d
            java.lang.String r11 = "/mnt/secure"
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x016c }
            if (r11 != 0) goto L_0x008d
            java.lang.String r11 = "/mnt/asec"
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x016c }
            if (r11 != 0) goto L_0x008d
            java.lang.String r11 = "/mnt/obb"
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x016c }
            if (r11 != 0) goto L_0x008d
            java.lang.String r11 = "/dev/mapper"
            boolean r11 = r4.contains(r11)     // Catch:{ Exception -> 0x016c }
            if (r11 != 0) goto L_0x008d
            java.lang.String r11 = "tmpfs"
            boolean r4 = r4.contains(r11)     // Catch:{ Exception -> 0x016c }
            if (r4 != 0) goto L_0x008d
            java.io.File r4 = new java.io.File     // Catch:{ Exception -> 0x016c }
            r4.<init>(r5)     // Catch:{ Exception -> 0x016c }
            boolean r4 = r4.isDirectory()     // Catch:{ Exception -> 0x016c }
            if (r4 != 0) goto L_0x0126
            r4 = 47
            int r4 = r5.lastIndexOf(r4)     // Catch:{ Exception -> 0x016c }
            r11 = -1
            if (r4 == r11) goto L_0x0126
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x016c }
            r11.<init>()     // Catch:{ Exception -> 0x016c }
            java.lang.String r12 = "/storage/"
            r11.append(r12)     // Catch:{ Exception -> 0x016c }
            int r4 = r4 + 1
            java.lang.String r4 = r5.substring(r4)     // Catch:{ Exception -> 0x016c }
            r11.append(r4)     // Catch:{ Exception -> 0x016c }
            java.lang.String r4 = r11.toString()     // Catch:{ Exception -> 0x016c }
            java.io.File r11 = new java.io.File     // Catch:{ Exception -> 0x016c }
            r11.<init>(r4)     // Catch:{ Exception -> 0x016c }
            boolean r11 = r11.isDirectory()     // Catch:{ Exception -> 0x016c }
            if (r11 == 0) goto L_0x0126
            r5 = r4
        L_0x0126:
            r2.add(r5)     // Catch:{ Exception -> 0x016c }
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r4 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem     // Catch:{ Exception -> 0x0162 }
            r4.<init>()     // Catch:{ Exception -> 0x0162 }
            java.lang.String r11 = r5.toLowerCase()     // Catch:{ Exception -> 0x0162 }
            java.lang.String r12 = "sd"
            boolean r11 = r11.contains(r12)     // Catch:{ Exception -> 0x0162 }
            if (r11 == 0) goto L_0x0141
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r10, r9)     // Catch:{ Exception -> 0x0162 }
            r4.title = r11     // Catch:{ Exception -> 0x0162 }
            goto L_0x014c
        L_0x0141:
            java.lang.String r11 = "ExternalStorage"
            r12 = 2131625342(0x7f0e057e, float:1.887789E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11, r12)     // Catch:{ Exception -> 0x0162 }
            r4.title = r11     // Catch:{ Exception -> 0x0162 }
        L_0x014c:
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r7, r6)     // Catch:{ Exception -> 0x0162 }
            r4.subtitle = r11     // Catch:{ Exception -> 0x0162 }
            r4.icon = r8     // Catch:{ Exception -> 0x0162 }
            java.io.File r11 = new java.io.File     // Catch:{ Exception -> 0x0162 }
            r11.<init>(r5)     // Catch:{ Exception -> 0x0162 }
            r4.file = r11     // Catch:{ Exception -> 0x0162 }
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r5 = r13.items     // Catch:{ Exception -> 0x0162 }
            r5.add(r4)     // Catch:{ Exception -> 0x0162 }
            goto L_0x008d
        L_0x0162:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)     // Catch:{ Exception -> 0x016c }
            goto L_0x008d
        L_0x0168:
            r3.close()     // Catch:{ Exception -> 0x017c }
            goto L_0x0180
        L_0x016c:
            r2 = move-exception
            goto L_0x0173
        L_0x016e:
            r0 = move-exception
            goto L_0x021e
        L_0x0171:
            r2 = move-exception
            r3 = r1
        L_0x0173:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x021c }
            if (r3 == 0) goto L_0x0180
            r3.close()     // Catch:{ Exception -> 0x017c }
            goto L_0x0180
        L_0x017c:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0180:
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x01ae }
            java.io.File r3 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x01ae }
            r2.<init>(r3, r0)     // Catch:{ Exception -> 0x01ae }
            boolean r3 = r2.exists()     // Catch:{ Exception -> 0x01ae }
            if (r3 == 0) goto L_0x01b2
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r3 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem     // Catch:{ Exception -> 0x01ae }
            r3.<init>()     // Catch:{ Exception -> 0x01ae }
            r3.title = r0     // Catch:{ Exception -> 0x01ae }
            java.lang.String r0 = "AppFolderInfo"
            r4 = 2131624256(0x7f0e0140, float:1.8875687E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r4)     // Catch:{ Exception -> 0x01ae }
            r3.subtitle = r0     // Catch:{ Exception -> 0x01ae }
            r0 = 2131165400(0x7var_d8, float:1.7945016E38)
            r3.icon = r0     // Catch:{ Exception -> 0x01ae }
            r3.file = r2     // Catch:{ Exception -> 0x01ae }
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r0 = r13.items     // Catch:{ Exception -> 0x01ae }
            r0.add(r3)     // Catch:{ Exception -> 0x01ae }
            goto L_0x01b2
        L_0x01ae:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01b2:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r0.<init>()
            r2 = 2131625551(0x7f0e064f, float:1.8878313E38)
            java.lang.String r3 = "Gallery"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.title = r2
            r2 = 2131625552(0x7f0e0650, float:1.8878315E38)
            java.lang.String r3 = "GalleryInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.subtitle = r2
            r2 = 2131165401(0x7var_d9, float:1.7945018E38)
            r0.icon = r2
            r0.file = r1
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r2 = r13.items
            r2.add(r0)
            boolean r0 = r13.allowMusic
            if (r0 == 0) goto L_0x0204
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r0.<init>()
            r2 = 2131624365(0x7f0e01ad, float:1.8875908E38)
            java.lang.String r3 = "AttachMusic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.title = r2
            r2 = 2131625992(0x7f0e0808, float:1.8879208E38)
            java.lang.String r3 = "MusicInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.subtitle = r2
            r2 = 2131165403(0x7var_db, float:1.7945022E38)
            r0.icon = r2
            r0.file = r1
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r1 = r13.items
            r1.add(r0)
        L_0x0204:
            java.util.ArrayList<org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem> r0 = r13.recentItems
            boolean r0 = r0.isEmpty()
            r1 = 1
            if (r0 != 0) goto L_0x020f
            r13.hasFiles = r1
        L_0x020f:
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            org.telegram.messenger.AndroidUtilities.clearDrawableAnimation(r0)
            r13.scrolling = r1
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r0 = r13.listAdapter
            r0.notifyDataSetChanged()
            return
        L_0x021c:
            r0 = move-exception
            r1 = r3
        L_0x021e:
            if (r1 == 0) goto L_0x0228
            r1.close()     // Catch:{ Exception -> 0x0224 }
            goto L_0x0228
        L_0x0224:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0228:
            goto L_0x022a
        L_0x0229:
            throw r0
        L_0x022a:
            goto L_0x0229
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
                    view2 = new SharedDocumentCell(this.mContext, 1);
                } else if (i != 2) {
                    view = new View(this.mContext);
                } else {
                    view2 = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    view2.setBackgroundDrawable(combinedDrawable);
                }
                view = view2;
            } else {
                view = new HeaderCell(this.mContext);
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

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<ListItem> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(String str) {
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                if (!this.searchResult.isEmpty()) {
                    this.searchResult.clear();
                }
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.listAdapter) {
                    ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.listAdapter);
                }
                notifyDataSetChanged();
                return;
            }
            $$Lambda$ChatAttachAlertDocumentLayout$SearchAdapter$SVXF_0LRGzSJ2PW8PyIx1rhcirA r0 = new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.lambda$search$1$ChatAttachAlertDocumentLayout$SearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$search$1 */
        public /* synthetic */ void lambda$search$1$ChatAttachAlertDocumentLayout$SearchAdapter(String str) {
            ArrayList arrayList = new ArrayList(ChatAttachAlertDocumentLayout.this.items);
            if (ChatAttachAlertDocumentLayout.this.history.isEmpty()) {
                arrayList.addAll(0, ChatAttachAlertDocumentLayout.this.recentItems);
            }
            Utilities.searchQueue.postRunnable(new Runnable(str, arrayList) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.lambda$null$0$ChatAttachAlertDocumentLayout$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
        public /* synthetic */ void lambda$null$0$ChatAttachAlertDocumentLayout$SearchAdapter(String str, ArrayList arrayList) {
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
            updateSearchResults(arrayList2, str);
        }

        private void updateSearchResults(ArrayList<ListItem> arrayList, String str) {
            AndroidUtilities.runOnUIThread(new Runnable(str, arrayList) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatAttachAlertDocumentLayout.SearchAdapter.this.lambda$updateSearchResults$2$ChatAttachAlertDocumentLayout$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$updateSearchResults$2 */
        public /* synthetic */ void lambda$updateSearchResults$2$ChatAttachAlertDocumentLayout$SearchAdapter(String str, ArrayList arrayList) {
            if (ChatAttachAlertDocumentLayout.this.searching) {
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.searchAdapter) {
                    ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.searchAdapter);
                }
                ChatAttachAlertDocumentLayout.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoFilesFoundInfo", NUM, str)));
            }
            this.searchResult = arrayList;
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return this.searchResult.size() + 1;
        }

        public ListItem getItem(int i) {
            if (i < this.searchResult.size()) {
                return this.searchResult.get(i);
            }
            return null;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(this.mContext);
            } else {
                view = new SharedDocumentCell(this.mContext, 1);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ListItem item = getItem(i);
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                int i2 = item.icon;
                if (i2 != 0) {
                    sharedDocumentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, (String) null, (String) null, i2, false);
                } else {
                    SharedDocumentCell sharedDocumentCell2 = sharedDocumentCell;
                    sharedDocumentCell2.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4)), item.thumb, 0, false);
                }
                if (item.file != null) {
                    sharedDocumentCell.setChecked(ChatAttachAlertDocumentLayout.this.selectedFiles.containsKey(item.file.toString()), !ChatAttachAlertDocumentLayout.this.scrolling);
                } else {
                    sharedDocumentCell.setChecked(false, !ChatAttachAlertDocumentLayout.this.scrolling);
                }
            }
        }

        public int getItemViewType(int i) {
            return i < this.searchResult.size() ? 0 : 1;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertDocumentLayout.this.updateEmptyView();
        }
    }

    /* access modifiers changed from: package-private */
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.searchItem.getSearchField(), ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        arrayList.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
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
