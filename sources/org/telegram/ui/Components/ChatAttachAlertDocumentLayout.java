package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
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
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.PhotoPickerActivity;

public class ChatAttachAlertDocumentLayout extends ChatAttachAlert.AttachAlertLayout {
    private static final int ANIMATION_BACKWARD = 2;
    private static final int ANIMATION_FORWARD = 1;
    private static final int ANIMATION_NONE = 0;
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_MUSIC = 1;
    public static final int TYPE_RINGTONE = 2;
    private static final int search_button = 0;
    private static final int sort_button = 6;
    /* access modifiers changed from: private */
    public float additionalTranslationY;
    private boolean allowMusic;
    private LinearLayoutManager backgroundLayoutManager;
    private ListAdapter backgroundListAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView backgroundListView;
    /* access modifiers changed from: private */
    public boolean canSelectOnlyImageFiles;
    /* access modifiers changed from: private */
    public int currentAnimationType;
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
    private boolean ignoreLayout;
    public boolean isSoundPicker;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    ValueAnimator listAnimation;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public FlickerLoadingView loadingView;
    private int maxSelectedFiles = -1;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            Runnable r = new ChatAttachAlertDocumentLayout$1$$ExternalSyntheticLambda0(this);
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                ChatAttachAlertDocumentLayout.this.listView.postDelayed(r, 1000);
            } else {
                r.run();
            }
        }

        /* renamed from: lambda$onReceive$0$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$1  reason: not valid java name */
        public /* synthetic */ void m792xf4e8da1() {
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
    private int type;

    public interface DocumentSelectActivityDelegate {
        void didSelectFiles(ArrayList<String> arrayList, String str, ArrayList<MessageObject> arrayList2, boolean z, int i);

        void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i);

        void startDocumentSelectActivity();

        void startMusicSelectActivity();

        /* renamed from: org.telegram.ui.Components.ChatAttachAlertDocumentLayout$DocumentSelectActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didSelectPhotos(DocumentSelectActivityDelegate _this, ArrayList arrayList, boolean notify, int scheduleDate) {
            }

            public static void $default$startDocumentSelectActivity(DocumentSelectActivityDelegate _this) {
            }

            public static void $default$startMusicSelectActivity(DocumentSelectActivityDelegate _this) {
            }
        }
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
        int scrollItem;
        int scrollOffset;
        String title;

        private HistoryEntry() {
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatAttachAlertDocumentLayout(org.telegram.ui.Components.ChatAttachAlert r21, android.content.Context r22, int r23, org.telegram.ui.ActionBar.Theme.ResourcesProvider r24) {
        /*
            r20 = this;
            r7 = r20
            r8 = r22
            r9 = r23
            r10 = r24
            r11 = r21
            r7.<init>(r11, r8, r10)
            r12 = 0
            r7.receiverRegistered = r12
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r7.selectedFiles = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.selectedFilesOrder = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r7.selectedMessages = r0
            r13 = -1
            r7.maxSelectedFiles = r13
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$1 r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$1
            r0.<init>()
            r7.receiver = r0
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter
            r0.<init>(r8)
            r7.listAdapter = r0
            r0 = 1
            if (r9 != r0) goto L_0x003b
            r1 = 1
            goto L_0x003c
        L_0x003b:
            r1 = 0
        L_0x003c:
            r7.allowMusic = r1
            r6 = 2
            if (r9 != r6) goto L_0x0043
            r1 = 1
            goto L_0x0044
        L_0x0043:
            r1 = 0
        L_0x0044:
            r7.isSoundPicker = r1
            boolean r1 = org.telegram.messenger.SharedConfig.sortFilesByName
            r7.sortByName = r1
            r20.loadRecentFiles()
            r7.searching = r12
            boolean r1 = r7.receiverRegistered
            if (r1 != 0) goto L_0x0093
            r7.receiverRegistered = r0
            android.content.IntentFilter r1 = new android.content.IntentFilter
            r1.<init>()
            java.lang.String r2 = "android.intent.action.MEDIA_BAD_REMOVAL"
            r1.addAction(r2)
            java.lang.String r2 = "android.intent.action.MEDIA_CHECKING"
            r1.addAction(r2)
            java.lang.String r2 = "android.intent.action.MEDIA_EJECT"
            r1.addAction(r2)
            java.lang.String r2 = "android.intent.action.MEDIA_MOUNTED"
            r1.addAction(r2)
            java.lang.String r2 = "android.intent.action.MEDIA_NOFS"
            r1.addAction(r2)
            java.lang.String r2 = "android.intent.action.MEDIA_REMOVED"
            r1.addAction(r2)
            java.lang.String r2 = "android.intent.action.MEDIA_SHARED"
            r1.addAction(r2)
            java.lang.String r2 = "android.intent.action.MEDIA_UNMOUNTABLE"
            r1.addAction(r2)
            java.lang.String r2 = "android.intent.action.MEDIA_UNMOUNTED"
            r1.addAction(r2)
            java.lang.String r2 = "file"
            r1.addDataScheme(r2)
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.BroadcastReceiver r3 = r7.receiver
            r2.registerReceiver(r3, r1)
        L_0x0093:
            org.telegram.ui.Components.ChatAttachAlert r1 = r7.parentAlert
            org.telegram.ui.ActionBar.ActionBar r1 = r1.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r14 = r1.createMenu()
            r1 = 2131165456(0x7var_, float:1.794513E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r14.addItem((int) r12, (int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r1.setIsSearchField(r0)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$2 r1 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$2
            r1.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.setActionBarMenuItemSearchListener(r1)
            r7.searchItem = r0
            java.lang.String r1 = "Search"
            r2 = 2131628092(0x7f0e103c, float:1.8883467E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setSearchFieldHint(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r15 = r0.getSearchField()
            java.lang.String r0 = "dialogTextBlack"
            int r1 = r7.getThemedColor(r0)
            r15.setTextColor(r1)
            int r0 = r7.getThemedColor(r0)
            r15.setCursorColor(r0)
            java.lang.String r0 = "chat_messagePanelHint"
            int r0 = r7.getThemedColor(r0)
            r15.setHintTextColor(r0)
            r0 = 6
            boolean r1 = r7.sortByName
            if (r1 == 0) goto L_0x00ee
            r1 = 2131165696(0x7var_, float:1.7945616E38)
            goto L_0x00f1
        L_0x00ee:
            r1 = 2131165694(0x7var_fe, float:1.7945612E38)
        L_0x00f1:
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r14.addItem((int) r0, (int) r1)
            r7.sortItem = r0
            r1 = 2131623979(0x7f0e002b, float:1.8875125E38)
            java.lang.String r2 = "AccDescrContactSorting"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            org.telegram.ui.Components.FlickerLoadingView r0 = new org.telegram.ui.Components.FlickerLoadingView
            r0.<init>(r8, r10)
            r7.loadingView = r0
            r7.addView(r0)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$3 r5 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$3
            org.telegram.ui.Components.FlickerLoadingView r3 = r7.loadingView
            r4 = 1
            r0 = r5
            r1 = r20
            r2 = r22
            r12 = r5
            r5 = r24
            r0.<init>(r2, r3, r4, r5)
            r7.emptyView = r12
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r5)
            r7.addView(r12, r0)
            org.telegram.ui.Components.StickerEmptyView r0 = r7.emptyView
            r12 = 8
            r0.setVisibility(r12)
            org.telegram.ui.Components.StickerEmptyView r0 = r7.emptyView
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda1 r1 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda1.INSTANCE
            r0.setOnTouchListener(r1)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$4 r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$4
            r0.<init>(r8, r10)
            r7.backgroundListView = r0
            r0.setSectionsType(r6)
            org.telegram.ui.Components.RecyclerListView r0 = r7.backgroundListView
            r1 = 0
            r0.setVerticalScrollBarEnabled(r1)
            org.telegram.ui.Components.RecyclerListView r4 = r7.backgroundListView
            org.telegram.ui.Components.FillLastLinearLayoutManager r3 = new org.telegram.ui.Components.FillLastLinearLayoutManager
            r2 = 1
            r16 = 0
            r17 = 1113587712(0x42600000, float:56.0)
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r17)
            org.telegram.ui.Components.RecyclerListView r1 = r7.backgroundListView
            r0 = r3
            r19 = r1
            r1 = r22
            r6 = r3
            r3 = r16
            r12 = r4
            r4 = r18
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r5 = r19
            r0.<init>(r1, r2, r3, r4, r5)
            r7.backgroundLayoutManager = r6
            r12.setLayoutManager(r6)
            org.telegram.ui.Components.RecyclerListView r0 = r7.backgroundListView
            r1 = 0
            r0.setClipToPadding(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.backgroundListView
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r2 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter
            r2.<init>(r8)
            r7.backgroundListAdapter = r2
            r0.setAdapter(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.backgroundListView
            r12 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r0.setPadding(r1, r1, r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.backgroundListView
            r1 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r13)
            r7.addView(r0, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.backgroundListView
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$5 r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$5
            r0.<init>(r8, r10)
            r7.listView = r0
            r1 = 2
            r0.setSectionsType(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            r1 = 0
            r0.setVerticalScrollBarEnabled(r1)
            org.telegram.ui.Components.RecyclerListView r6 = r7.listView
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$6 r5 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$6
            r3 = 1
            r4 = 0
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r17)
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            r0 = r5
            r1 = r20
            r17 = r2
            r2 = r22
            r13 = r5
            r5 = r16
            r12 = r6
            r6 = r17
            r0.<init>(r2, r3, r4, r5, r6)
            r7.layoutManager = r13
            r12.setLayoutManager(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            r1 = 0
            r0.setClipToPadding(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r2 = r7.listAdapter
            r0.setAdapter(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r1, r1, r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1)
            r7.addView(r0, r1)
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter
            r0.<init>(r8)
            r7.searchAdapter = r0
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$7 r1 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$7
            r1.<init>()
            r0.setOnScrollListener(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda4 r1 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda4
            r1.<init>(r7)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda6 r1 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda6
            r1.<init>(r7)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            org.telegram.ui.Adapters.FiltersView r0 = new org.telegram.ui.Adapters.FiltersView
            r0.<init>(r8, r10)
            r7.filtersView = r0
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda5 r1 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda5
            r1.<init>(r7)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Adapters.FiltersView r0 = r7.filtersView
            java.lang.String r1 = "dialogBackground"
            int r1 = r7.getThemedColor(r1)
            r0.setBackgroundColor(r1)
            org.telegram.ui.Adapters.FiltersView r0 = r7.filtersView
            r1 = -2
            r2 = 48
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r1, (int) r2)
            r7.addView(r0, r1)
            org.telegram.ui.Adapters.FiltersView r0 = r7.filtersView
            r1 = 1110441984(0x42300000, float:44.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = -r1
            float r1 = (float) r1
            r0.setTranslationY(r1)
            org.telegram.ui.Adapters.FiltersView r0 = r7.filtersView
            r1 = 4
            r0.setVisibility(r1)
            r20.listRoots()
            r20.updateSearchButton()
            r20.updateEmptyView()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.<init>(org.telegram.ui.Components.ChatAttachAlert, android.content.Context, int, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertDocumentLayout  reason: not valid java name */
    public /* synthetic */ void m786x1var_fd7(View view, int position) {
        Object object;
        ChatActivity chatActivity;
        View view2 = view;
        int i = position;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ListAdapter listAdapter2 = this.listAdapter;
        if (adapter == listAdapter2) {
            object = listAdapter2.getItem(i);
        } else {
            object = this.searchAdapter.getItem(i);
        }
        if (object instanceof ListItem) {
            ListItem item = (ListItem) object;
            File file = item.file;
            boolean isExternalStorageManager = false;
            if (Build.VERSION.SDK_INT >= 30) {
                isExternalStorageManager = Environment.isExternalStorageManager();
            }
            if (!BuildVars.NO_SCOPED_STORAGE && ((item.icon == NUM || item.icon == NUM) && !isExternalStorageManager)) {
                this.delegate.startDocumentSelectActivity();
            } else if (file == null) {
                if (item.icon == NUM) {
                    final HashMap<Object, Object> selectedPhotos = new HashMap<>();
                    ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
                    if (this.parentAlert.baseFragment instanceof ChatActivity) {
                        chatActivity = (ChatActivity) this.parentAlert.baseFragment;
                    } else {
                        chatActivity = null;
                    }
                    final ArrayList<Object> selectedPhotosOrder2 = selectedPhotosOrder;
                    PhotoPickerActivity fragment = new PhotoPickerActivity(0, MediaController.allMediaAlbumEntry, selectedPhotos, selectedPhotosOrder, 0, chatActivity != null, chatActivity, false);
                    fragment.setDocumentsPicker(true);
                    fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                        public void selectedPhotosChanged() {
                        }

                        public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate) {
                            if (!canceled) {
                                ChatAttachAlertDocumentLayout.this.sendSelectedPhotos(selectedPhotos, selectedPhotosOrder2, notify, scheduleDate);
                            }
                        }

                        public void onCaptionChanged(CharSequence text) {
                        }

                        public void onOpenInPressed() {
                            ChatAttachAlertDocumentLayout.this.delegate.startDocumentSelectActivity();
                        }
                    });
                    fragment.setMaxSelectedPhotos(this.maxSelectedFiles, false);
                    this.parentAlert.baseFragment.presentFragment(fragment);
                    this.parentAlert.dismiss(true);
                } else if (item.icon == NUM) {
                    DocumentSelectActivityDelegate documentSelectActivityDelegate = this.delegate;
                    if (documentSelectActivityDelegate != null) {
                        documentSelectActivityDelegate.startMusicSelectActivity();
                    }
                } else {
                    int top = getTopForScroll();
                    prepareAnimation();
                    HistoryEntry he = (HistoryEntry) this.listAdapter.history.remove(this.listAdapter.history.size() - 1);
                    this.parentAlert.actionBar.setTitle(he.title);
                    if (he.dir != null) {
                        listFiles(he.dir);
                    } else {
                        listRoots();
                    }
                    updateSearchButton();
                    this.layoutManager.scrollToPositionWithOffset(0, top);
                    runAnimation(2);
                }
            } else if (file.isDirectory()) {
                HistoryEntry he2 = new HistoryEntry();
                View child = this.listView.getChildAt(0);
                RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(child);
                if (holder != null) {
                    he2.scrollItem = holder.getAdapterPosition();
                    he2.scrollOffset = child.getTop();
                    he2.dir = this.currentDir;
                    he2.title = this.parentAlert.actionBar.getTitle();
                    prepareAnimation();
                    this.listAdapter.history.add(he2);
                    if (!listFiles(file)) {
                        this.listAdapter.history.remove(he2);
                        return;
                    }
                    runAnimation(1);
                    this.parentAlert.actionBar.setTitle(item.title);
                }
            } else {
                onItemClick(view2, item);
            }
        } else {
            onItemClick(view2, object);
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlertDocumentLayout  reason: not valid java name */
    public /* synthetic */ boolean m787xaCLASSNAMEf6(View view, int position) {
        Object object;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ListAdapter listAdapter2 = this.listAdapter;
        if (adapter == listAdapter2) {
            object = listAdapter2.getItem(position);
        } else {
            object = this.searchAdapter.getItem(position);
        }
        return onItemClick(view, object);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlertDocumentLayout  reason: not valid java name */
    public /* synthetic */ void m788x3961ae15(View view, int position) {
        this.filtersView.cancelClickRunnables(true);
        this.searchAdapter.addSearchFilter(this.filtersView.getFilterAt(position));
    }

    private void runAnimation(int animationType) {
        float xTranslate;
        ValueAnimator valueAnimator = this.listAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.currentAnimationType = animationType;
        int listViewChildIndex = 0;
        int i = 0;
        while (true) {
            if (i >= getChildCount()) {
                break;
            } else if (getChildAt(i) == this.listView) {
                listViewChildIndex = i;
                break;
            } else {
                i++;
            }
        }
        if (animationType == 1) {
            xTranslate = (float) AndroidUtilities.dp(150.0f);
            this.backgroundListView.setAlpha(1.0f);
            this.backgroundListView.setScaleX(1.0f);
            this.backgroundListView.setScaleY(1.0f);
            this.backgroundListView.setTranslationX(0.0f);
            removeView(this.backgroundListView);
            addView(this.backgroundListView, listViewChildIndex);
            this.backgroundListView.setVisibility(0);
            this.listView.setTranslationX(xTranslate);
            this.listView.setAlpha(0.0f);
            this.listAnimation = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        } else {
            xTranslate = (float) AndroidUtilities.dp(150.0f);
            this.listView.setAlpha(0.0f);
            this.listView.setScaleX(0.95f);
            this.listView.setScaleY(0.95f);
            this.backgroundListView.setScaleX(1.0f);
            this.backgroundListView.setScaleY(1.0f);
            this.backgroundListView.setTranslationX(0.0f);
            this.backgroundListView.setAlpha(1.0f);
            removeView(this.backgroundListView);
            addView(this.backgroundListView, listViewChildIndex + 1);
            this.backgroundListView.setVisibility(0);
            this.listAnimation = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        }
        this.listAnimation.addUpdateListener(new ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda0(this, animationType, xTranslate));
        this.listAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ChatAttachAlertDocumentLayout.this.backgroundListView.setVisibility(8);
                int unused = ChatAttachAlertDocumentLayout.this.currentAnimationType = 0;
                ChatAttachAlertDocumentLayout.this.listView.setAlpha(1.0f);
                ChatAttachAlertDocumentLayout.this.listView.setScaleX(1.0f);
                ChatAttachAlertDocumentLayout.this.listView.setScaleY(1.0f);
                ChatAttachAlertDocumentLayout.this.listView.setTranslationX(0.0f);
                ChatAttachAlertDocumentLayout.this.listView.invalidate();
            }
        });
        if (animationType == 1) {
            this.listAnimation.setDuration(220);
        } else {
            this.listAnimation.setDuration(200);
        }
        this.listAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.listAnimation.start();
    }

    /* renamed from: lambda$runAnimation$4$org-telegram-ui-Components-ChatAttachAlertDocumentLayout  reason: not valid java name */
    public /* synthetic */ void m789xe59159a1(int animationType, float xTranslate, ValueAnimator animation) {
        float value = ((Float) animation.getAnimatedValue()).floatValue();
        if (animationType == 1) {
            this.listView.setTranslationX(xTranslate * value);
            this.listView.setAlpha(1.0f - value);
            this.listView.invalidate();
            this.backgroundListView.setAlpha(value);
            float s = (0.05f * value) + 0.95f;
            this.backgroundListView.setScaleX(s);
            this.backgroundListView.setScaleY(s);
            return;
        }
        this.backgroundListView.setTranslationX(xTranslate * value);
        this.backgroundListView.setAlpha(Math.max(0.0f, 1.0f - value));
        this.backgroundListView.invalidate();
        this.listView.setAlpha(value);
        float s2 = (0.05f * value) + 0.95f;
        this.listView.setScaleX(s2);
        this.listView.setScaleY(s2);
        this.backgroundListView.invalidate();
    }

    private void prepareAnimation() {
        View childView;
        this.backgroundListAdapter.history.clear();
        this.backgroundListAdapter.history.addAll(this.listAdapter.history);
        this.backgroundListAdapter.items.clear();
        this.backgroundListAdapter.items.addAll(this.listAdapter.items);
        this.backgroundListAdapter.recentItems.clear();
        this.backgroundListAdapter.recentItems.addAll(this.listAdapter.recentItems);
        this.backgroundListAdapter.notifyDataSetChanged();
        this.backgroundListView.setVisibility(0);
        this.backgroundListView.setPadding(this.listView.getPaddingLeft(), this.listView.getPaddingTop(), this.listView.getPaddingRight(), this.listView.getPaddingBottom());
        int p = this.layoutManager.findFirstVisibleItemPosition();
        if (p >= 0 && (childView = this.layoutManager.findViewByPosition(p)) != null) {
            this.backgroundLayoutManager.scrollToPositionWithOffset(p, childView.getTop() - this.backgroundListView.getPaddingTop());
        }
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
        ActionBarMenu menu = this.parentAlert.actionBar.createMenu();
        menu.removeView(this.sortItem);
        menu.removeView(this.searchItem);
    }

    /* access modifiers changed from: package-private */
    public void onMenuItemClick(int id) {
        if (id == 6) {
            SharedConfig.toggleSortFilesByName();
            this.sortByName = SharedConfig.sortFilesByName;
            sortRecentItems();
            sortFileItems();
            this.listAdapter.notifyDataSetChanged();
            this.sortItem.setIcon(this.sortByName ? NUM : NUM);
        }
    }

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    /* access modifiers changed from: package-private */
    public int getCurrentItemTop() {
        if (this.listView.getChildCount() <= 0) {
            return Integer.MAX_VALUE;
        }
        int newOffset = 0;
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = ((int) child.getY()) - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        return AndroidUtilities.dp(13.0f) + newOffset;
    }

    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
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
    public void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        int padding2;
        if (this.parentAlert.actionBar.isSearchFieldVisible() || this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            padding = AndroidUtilities.dp(56.0f);
            this.parentAlert.setAllowNestedScroll(false);
        } else {
            if (AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
                padding2 = (availableHeight / 5) * 2;
            } else {
                padding2 = (int) (((float) availableHeight) / 3.5f);
            }
            padding = padding2 - AndroidUtilities.dp(1.0f);
            if (padding < 0) {
                padding = 0;
            }
            this.parentAlert.setAllowNestedScroll(true);
        }
        if (this.listView.getPaddingTop() != padding) {
            this.ignoreLayout = true;
            this.listView.setPadding(0, padding, 0, AndroidUtilities.dp(48.0f));
            this.ignoreLayout = false;
        }
        ((FrameLayout.LayoutParams) this.filtersView.getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
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
    public void sendSelectedItems(boolean notify, int scheduleDate) {
        if ((this.selectedFiles.size() != 0 || this.selectedMessages.size() != 0) && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList<MessageObject> fmessages = new ArrayList<>();
            for (FilteredSearchView.MessageHashId hashId : this.selectedMessages.keySet()) {
                fmessages.add(this.selectedMessages.get(hashId));
            }
            this.delegate.didSelectFiles(new ArrayList<>(this.selectedFilesOrder), this.parentAlert.commentTextView.getText().toString(), fmessages, notify, scheduleDate);
            this.parentAlert.dismiss(true);
        }
    }

    private boolean onItemClick(View view, Object object) {
        boolean add;
        int i;
        if (object instanceof ListItem) {
            ListItem item = (ListItem) object;
            if (item.file == null || item.file.isDirectory()) {
                return false;
            }
            String path = item.file.getAbsolutePath();
            if (this.selectedFiles.containsKey(path)) {
                this.selectedFiles.remove(path);
                this.selectedFilesOrder.remove(path);
                add = false;
            } else if (!item.file.canRead()) {
                showErrorBox(LocaleController.getString("AccessError", NUM));
                return false;
            } else if (this.canSelectOnlyImageFiles && item.thumb == null) {
                showErrorBox(LocaleController.formatString("PassportUploadNotImage", NUM, new Object[0]));
                return false;
            } else if ((item.file.length() > NUM && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) || item.file.length() > 4194304000L) {
                LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(this.parentAlert.baseFragment, this.parentAlert.getContainer().getContext(), 6, UserConfig.selectedAccount);
                limitReachedBottomSheet.setVeryLargeFile(true);
                limitReachedBottomSheet.show();
                return false;
            } else if (this.maxSelectedFiles >= 0 && this.selectedFiles.size() >= (i = this.maxSelectedFiles)) {
                showErrorBox(LocaleController.formatString("PassportUploadMaxReached", NUM, LocaleController.formatPluralString("Files", i, new Object[0])));
                return false;
            } else if ((this.isSoundPicker && !isRingtone(item.file)) || item.file.length() == 0) {
                return false;
            } else {
                this.selectedFiles.put(path, item);
                this.selectedFilesOrder.add(path);
                add = true;
            }
            this.scrolling = false;
        } else if (!(object instanceof MessageObject)) {
            return false;
        } else {
            MessageObject message = (MessageObject) object;
            FilteredSearchView.MessageHashId hashId = new FilteredSearchView.MessageHashId(message.getId(), message.getDialogId());
            if (this.selectedMessages.containsKey(hashId)) {
                this.selectedMessages.remove(hashId);
                add = false;
            } else if (this.selectedMessages.size() >= 100) {
                return false;
            } else {
                this.selectedMessages.put(hashId, message);
                add = true;
            }
        }
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(add, true);
        }
        this.parentAlert.updateCountButton(add ? 1 : 2);
        return true;
    }

    public boolean isRingtone(File file) {
        int millSecond;
        String mimeType = null;
        String extension = FileLoader.getFileExtension(file);
        if (extension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (file.length() == 0 || mimeType == null || !RingtoneDataStore.ringtoneSupportedMimeType.contains(mimeType)) {
            BulletinFactory.of(this.parentAlert.getContainer(), (Theme.ResourcesProvider) null).createErrorBulletinSubtitle(LocaleController.formatString("InvalidFormatError", NUM, new Object[0]), LocaleController.formatString("ErrorInvalidRingtone", NUM, new Object[0]), (Theme.ResourcesProvider) null).show();
            return false;
        } else if (file.length() > ((long) MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax)) {
            BulletinFactory.of(this.parentAlert.getContainer(), (Theme.ResourcesProvider) null).createErrorBulletinSubtitle(LocaleController.formatString("TooLargeError", NUM, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", NUM, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax / 1024)), (Theme.ResourcesProvider) null).show();
            return false;
        } else {
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(ApplicationLoader.applicationContext, Uri.fromFile(file));
                millSecond = Integer.parseInt(mmr.extractMetadata(9));
            } catch (Exception e) {
                millSecond = Integer.MAX_VALUE;
            }
            if (millSecond <= MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax * 1000) {
                return true;
            }
            BulletinFactory.of(this.parentAlert.getContainer(), (Theme.ResourcesProvider) null).createErrorBulletinSubtitle(LocaleController.formatString("TooLongError", NUM, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", NUM, Integer.valueOf(MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax)), (Theme.ResourcesProvider) null).show();
            return false;
        }
    }

    public void setMaxSelectedFiles(int value) {
        this.maxSelectedFiles = value;
    }

    public void setCanSelectOnlyImageFiles(boolean value) {
        this.canSelectOnlyImageFiles = value;
    }

    /* access modifiers changed from: private */
    public void sendSelectedPhotos(HashMap<Object, Object> photos, ArrayList<Object> order, boolean notify, int scheduleDate) {
        if (!photos.isEmpty() && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList<SendMessagesHelper.SendingMediaInfo> media = new ArrayList<>();
            for (int a = 0; a < order.size(); a++) {
                Object object = photos.get(order.get(a));
                SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                media.add(info);
                if (object instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                    if (photoEntry.imagePath != null) {
                        info.path = photoEntry.imagePath;
                    } else {
                        info.path = photoEntry.path;
                    }
                    info.thumbPath = photoEntry.thumbPath;
                    info.videoEditedInfo = photoEntry.editedInfo;
                    info.isVideo = photoEntry.isVideo;
                    info.caption = photoEntry.caption != null ? photoEntry.caption.toString() : null;
                    info.entities = photoEntry.entities;
                    info.masks = photoEntry.stickers;
                    info.ttl = photoEntry.ttl;
                }
            }
            this.delegate.didSelectPhotos(media, notify, scheduleDate);
        }
    }

    public void loadRecentFiles() {
        Cursor cursor;
        Throwable th;
        try {
            if (this.isSoundPicker) {
                int i = 2;
                try {
                    cursor = ApplicationLoader.applicationContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_data", "duration", "_size", "mime_type"}, "is_music != 0", (String[]) null, "date_added DESC");
                    while (cursor.moveToNext()) {
                        File file = new File(cursor.getString(1));
                        long duration = cursor.getLong(i);
                        long fileSize = cursor.getLong(3);
                        String mimeType = cursor.getString(4);
                        if (duration > ((long) (MessagesController.getInstance(UserConfig.selectedAccount).ringtoneDurationMax * 1000)) || fileSize > ((long) MessagesController.getInstance(UserConfig.selectedAccount).ringtoneSizeMax)) {
                            i = 2;
                        } else if (TextUtils.isEmpty(mimeType) || "audio/mpeg".equals(mimeType) || !"audio/mpeg4".equals(mimeType)) {
                            ListItem item = new ListItem();
                            item.title = file.getName();
                            item.file = file;
                            String fname = file.getName();
                            String[] sp = fname.split("\\.");
                            item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                            item.subtitle = AndroidUtilities.formatFileSize(file.length());
                            String fname2 = fname.toLowerCase();
                            if (fname2.endsWith(".jpg") || fname2.endsWith(".png") || fname2.endsWith(".gif") || fname2.endsWith(".jpeg")) {
                                item.thumb = file.getAbsolutePath();
                            }
                            this.listAdapter.recentItems.add(item);
                            i = 2;
                        }
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                } catch (Throwable th2) {
                }
                return;
            }
            checkDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            sortRecentItems();
            return;
            throw th;
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    private void checkDirectory(File rootDir) {
        File[] files = rootDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory() || !file.getName().equals("Telegram")) {
                    ListItem item = new ListItem();
                    item.title = file.getName();
                    item.file = file;
                    String fname = file.getName();
                    String[] sp = fname.split("\\.");
                    item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                    item.subtitle = AndroidUtilities.formatFileSize(file.length());
                    String fname2 = fname.toLowerCase();
                    if (fname2.endsWith(".jpg") || fname2.endsWith(".png") || fname2.endsWith(".gif") || fname2.endsWith(".jpeg")) {
                        item.thumb = file.getAbsolutePath();
                    }
                    this.listAdapter.recentItems.add(item);
                } else {
                    checkDirectory(file);
                }
            }
        }
    }

    private void sortRecentItems() {
        Collections.sort(this.listAdapter.recentItems, new ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda3(this));
    }

    /* renamed from: lambda$sortRecentItems$5$org-telegram-ui-Components-ChatAttachAlertDocumentLayout  reason: not valid java name */
    public /* synthetic */ int m791x1b4cac6c(ListItem o1, ListItem o2) {
        if (this.sortByName) {
            return o1.file.getName().compareToIgnoreCase(o2.file.getName());
        }
        long lm = o1.file.lastModified();
        long rm = o2.file.lastModified();
        if (lm == rm) {
            return 0;
        }
        if (lm > rm) {
            return -1;
        }
        return 1;
    }

    private void sortFileItems() {
        if (this.currentDir != null) {
            Collections.sort(this.listAdapter.items, new ChatAttachAlertDocumentLayout$$ExternalSyntheticLambda2(this));
        }
    }

    /* renamed from: lambda$sortFileItems$6$org-telegram-ui-Components-ChatAttachAlertDocumentLayout  reason: not valid java name */
    public /* synthetic */ int m790x6a58bd6c(ListItem lhs, ListItem rhs) {
        if (lhs.file == null) {
            return -1;
        }
        if (rhs.file == null) {
            return 1;
        }
        boolean isDir1 = lhs.file.isDirectory();
        if (isDir1 != rhs.file.isDirectory()) {
            if (isDir1) {
                return -1;
            }
            return 1;
        } else if (isDir1 || this.sortByName) {
            return lhs.file.getName().compareToIgnoreCase(rhs.file.getName());
        } else {
            long lm = lhs.file.lastModified();
            long rm = rhs.file.lastModified();
            if (lm == rm) {
                return 0;
            }
            if (lm > rm) {
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
    public void onShow(ChatAttachAlert.AttachAlertLayout previousLayout) {
        this.selectedFiles.clear();
        this.selectedMessages.clear();
        this.searchAdapter.currentSearchFilters.clear();
        this.selectedFilesOrder.clear();
        this.listAdapter.history.clear();
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
        View child;
        if (this.emptyView.getVisibility() == 0 && (child = this.listView.getChildAt(0)) != null) {
            float oldTranslation = this.emptyView.getTranslationY();
            this.additionalTranslationY = (float) (((this.emptyView.getMeasuredHeight() - getMeasuredHeight()) + child.getTop()) / 2);
            this.emptyView.setTranslationY(oldTranslation);
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        boolean visible;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter2 = this.searchAdapter;
        int i = 0;
        boolean z = true;
        if (adapter == searchAdapter2) {
            if (!searchAdapter2.searchResult.isEmpty() || !this.searchAdapter.sections.isEmpty()) {
                z = false;
            }
            visible = z;
        } else {
            if (this.listAdapter.getItemCount() != 1) {
                z = false;
            }
            visible = z;
        }
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (!visible) {
            i = 8;
        }
        stickerEmptyView.setVisibility(i);
        updateEmptyViewPosition();
    }

    /* access modifiers changed from: private */
    public void updateSearchButton() {
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null && !actionBarMenuItem.isSearchFieldVisible()) {
            this.searchItem.setVisibility((this.hasFiles || this.listAdapter.history.isEmpty()) ? 0 : 8);
        }
    }

    private int getTopForScroll() {
        View child = this.listView.getChildAt(0);
        RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(child);
        int top = -this.listView.getPaddingTop();
        if (holder == null || holder.getAdapterPosition() != 0) {
            return top;
        }
        return top + child.getTop();
    }

    private boolean canClosePicker() {
        if (this.listAdapter.history.size() <= 0) {
            return true;
        }
        prepareAnimation();
        HistoryEntry he = (HistoryEntry) this.listAdapter.history.remove(this.listAdapter.history.size() - 1);
        this.parentAlert.actionBar.setTitle(he.title);
        int top = getTopForScroll();
        if (he.dir != null) {
            listFiles(he.dir);
        } else {
            listRoots();
        }
        updateSearchButton();
        this.layoutManager.scrollToPositionWithOffset(0, top);
        runAnimation(2);
        return false;
    }

    public boolean onBackPressed() {
        if (!canClosePicker()) {
            return true;
        }
        return super.onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateEmptyViewPosition();
    }

    public void setDelegate(DocumentSelectActivityDelegate documentSelectActivityDelegate) {
        this.delegate = documentSelectActivityDelegate;
    }

    /* access modifiers changed from: private */
    public boolean listFiles(File dir) {
        this.hasFiles = false;
        if (dir.canRead()) {
            try {
                File[] files = dir.listFiles();
                if (files == null) {
                    showErrorBox(LocaleController.getString("UnknownError", NUM));
                    return false;
                }
                this.currentDir = dir;
                this.listAdapter.items.clear();
                for (File file : files) {
                    if (file.getName().indexOf(46) != 0) {
                        ListItem item = new ListItem();
                        item.title = file.getName();
                        item.file = file;
                        if (file.isDirectory()) {
                            item.icon = NUM;
                            item.subtitle = LocaleController.getString("Folder", NUM);
                        } else {
                            this.hasFiles = true;
                            String fname = file.getName();
                            String[] sp = fname.split("\\.");
                            item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                            item.subtitle = AndroidUtilities.formatFileSize(file.length());
                            String fname2 = fname.toLowerCase();
                            if (fname2.endsWith(".jpg") || fname2.endsWith(".png") || fname2.endsWith(".gif") || fname2.endsWith(".jpeg")) {
                                item.thumb = file.getAbsolutePath();
                            }
                        }
                        this.listAdapter.items.add(item);
                    }
                }
                ListItem item2 = new ListItem();
                item2.title = "..";
                if (this.listAdapter.history.size() > 0) {
                    HistoryEntry entry = (HistoryEntry) this.listAdapter.history.get(this.listAdapter.history.size() - 1);
                    if (entry.dir == null) {
                        item2.subtitle = LocaleController.getString("Folder", NUM);
                    } else {
                        item2.subtitle = entry.dir.toString();
                    }
                } else {
                    item2.subtitle = LocaleController.getString("Folder", NUM);
                }
                item2.icon = NUM;
                item2.file = null;
                this.listAdapter.items.add(0, item2);
                sortFileItems();
                updateSearchButton();
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                int top = getTopForScroll();
                this.listAdapter.notifyDataSetChanged();
                this.layoutManager.scrollToPositionWithOffset(0, top);
                return true;
            } catch (Exception e) {
                showErrorBox(e.getLocalizedMessage());
                return false;
            }
        } else if ((dir.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString()) || dir.getAbsolutePath().startsWith("/sdcard") || dir.getAbsolutePath().startsWith("/mnt/sdcard")) && !Environment.getExternalStorageState().equals("mounted") && !Environment.getExternalStorageState().equals("mounted_ro")) {
            this.currentDir = dir;
            this.listAdapter.items.clear();
            String externalStorageState = Environment.getExternalStorageState();
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
            return true;
        } else {
            showErrorBox(LocaleController.getString("AccessError", NUM));
            return false;
        }
    }

    private void showErrorBox(String error) {
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString("AppName", NUM)).setMessage(error).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0159 A[Catch:{ Exception -> 0x0192 }] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0163 A[Catch:{ Exception -> 0x0192 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void listRoots() {
        /*
            r18 = this;
            r1 = r18
            java.lang.String r2 = "Telegram"
            r3 = 0
            r1.currentDir = r3
            r0 = 0
            r1.hasFiles = r0
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r0 = r1.listAdapter
            java.util.ArrayList r0 = r0.items
            r0.clear()
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            r4 = r0
            r0 = 0
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 30
            if (r5 < r6) goto L_0x0026
            boolean r0 = android.os.Environment.isExternalStorageManager()
            r5 = r0
            goto L_0x0027
        L_0x0026:
            r5 = r0
        L_0x0027:
            java.io.File r0 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r6 = r0.getPath()
            java.lang.String r7 = android.os.Environment.getExternalStorageState()
            java.lang.String r0 = "mounted"
            boolean r0 = r7.equals(r0)
            r8 = 2131625792(0x7f0e0740, float:1.8878802E38)
            java.lang.String r9 = "ExternalFolderInfo"
            r10 = 2131165402(0x7var_da, float:1.794502E38)
            r11 = 2131628091(0x7f0e103b, float:1.8883465E38)
            java.lang.String r12 = "SdCard"
            if (r0 != 0) goto L_0x0050
            java.lang.String r0 = "mounted_ro"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0097
        L_0x0050:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r0.<init>()
            boolean r13 = android.os.Environment.isExternalStorageRemovable()
            if (r13 == 0) goto L_0x006a
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r0.title = r13
            r0.icon = r10
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.subtitle = r13
            goto L_0x0085
        L_0x006a:
            r13 = 2131626241(0x7f0e0901, float:1.8879713E38)
            java.lang.String r14 = "InternalStorage"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.title = r13
            r13 = 2131165404(0x7var_dc, float:1.7945024E38)
            r0.icon = r13
            r13 = 2131626240(0x7f0e0900, float:1.887971E38)
            java.lang.String r14 = "InternalFolderInfo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r0.subtitle = r13
        L_0x0085:
            java.io.File r13 = android.os.Environment.getExternalStorageDirectory()
            r0.file = r13
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r13 = r1.listAdapter
            java.util.ArrayList r13 = r13.items
            r13.add(r0)
            r4.add(r6)
        L_0x0097:
            r13 = 0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x01bc }
            java.io.FileReader r14 = new java.io.FileReader     // Catch:{ Exception -> 0x01bc }
            java.lang.String r15 = "/proc/mounts"
            r14.<init>(r15)     // Catch:{ Exception -> 0x01bc }
            r0.<init>(r14)     // Catch:{ Exception -> 0x01bc }
            r13 = r0
        L_0x00a5:
            java.lang.String r0 = r13.readLine()     // Catch:{ Exception -> 0x01bc }
            r14 = r0
            if (r0 == 0) goto L_0x01ac
            java.lang.String r0 = "vfat"
            boolean r0 = r14.contains(r0)     // Catch:{ Exception -> 0x01bc }
            if (r0 != 0) goto L_0x00bc
            java.lang.String r0 = "/mnt"
            boolean r0 = r14.contains(r0)     // Catch:{ Exception -> 0x01bc }
            if (r0 == 0) goto L_0x00a5
        L_0x00bc:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01bc }
            if (r0 == 0) goto L_0x00c3
            org.telegram.messenger.FileLog.d(r14)     // Catch:{ Exception -> 0x01bc }
        L_0x00c3:
            java.util.StringTokenizer r0 = new java.util.StringTokenizer     // Catch:{ Exception -> 0x01bc }
            java.lang.String r15 = " "
            r0.<init>(r14, r15)     // Catch:{ Exception -> 0x01bc }
            r15 = r0
            java.lang.String r0 = r15.nextToken()     // Catch:{ Exception -> 0x01bc }
            r16 = r0
            java.lang.String r0 = r15.nextToken()     // Catch:{ Exception -> 0x01bc }
            boolean r17 = r4.contains(r0)     // Catch:{ Exception -> 0x01bc }
            if (r17 == 0) goto L_0x00dc
            goto L_0x00a5
        L_0x00dc:
            java.lang.String r10 = "/dev/block/vold"
            boolean r10 = r14.contains(r10)     // Catch:{ Exception -> 0x01bc }
            if (r10 == 0) goto L_0x019e
            java.lang.String r10 = "/mnt/secure"
            boolean r10 = r14.contains(r10)     // Catch:{ Exception -> 0x01bc }
            if (r10 != 0) goto L_0x019a
            java.lang.String r10 = "/mnt/asec"
            boolean r10 = r14.contains(r10)     // Catch:{ Exception -> 0x01bc }
            if (r10 != 0) goto L_0x019a
            java.lang.String r10 = "/mnt/obb"
            boolean r10 = r14.contains(r10)     // Catch:{ Exception -> 0x01bc }
            if (r10 != 0) goto L_0x019a
            java.lang.String r10 = "/dev/mapper"
            boolean r10 = r14.contains(r10)     // Catch:{ Exception -> 0x01bc }
            if (r10 != 0) goto L_0x019a
            java.lang.String r10 = "tmpfs"
            boolean r10 = r14.contains(r10)     // Catch:{ Exception -> 0x01bc }
            if (r10 != 0) goto L_0x019a
            java.io.File r10 = new java.io.File     // Catch:{ Exception -> 0x01bc }
            r10.<init>(r0)     // Catch:{ Exception -> 0x01bc }
            boolean r10 = r10.isDirectory()     // Catch:{ Exception -> 0x01bc }
            if (r10 != 0) goto L_0x0144
            r10 = 47
            int r10 = r0.lastIndexOf(r10)     // Catch:{ Exception -> 0x01bc }
            r8 = -1
            if (r10 == r8) goto L_0x0144
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01bc }
            r8.<init>()     // Catch:{ Exception -> 0x01bc }
            java.lang.String r11 = "/storage/"
            r8.append(r11)     // Catch:{ Exception -> 0x01bc }
            int r11 = r10 + 1
            java.lang.String r11 = r0.substring(r11)     // Catch:{ Exception -> 0x01bc }
            r8.append(r11)     // Catch:{ Exception -> 0x01bc }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x01bc }
            java.io.File r11 = new java.io.File     // Catch:{ Exception -> 0x01bc }
            r11.<init>(r8)     // Catch:{ Exception -> 0x01bc }
            boolean r11 = r11.isDirectory()     // Catch:{ Exception -> 0x01bc }
            if (r11 == 0) goto L_0x0144
            r0 = r8
            goto L_0x0145
        L_0x0144:
            r8 = r0
        L_0x0145:
            r4.add(r8)     // Catch:{ Exception -> 0x01bc }
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem     // Catch:{ Exception -> 0x0192 }
            r0.<init>()     // Catch:{ Exception -> 0x0192 }
            java.lang.String r10 = r8.toLowerCase()     // Catch:{ Exception -> 0x0192 }
            java.lang.String r11 = "sd"
            boolean r10 = r10.contains(r11)     // Catch:{ Exception -> 0x0192 }
            if (r10 == 0) goto L_0x0163
            r10 = 2131628091(0x7f0e103b, float:1.8883465E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r10)     // Catch:{ Exception -> 0x0192 }
            r0.title = r11     // Catch:{ Exception -> 0x0192 }
            goto L_0x0171
        L_0x0163:
            r10 = 2131628091(0x7f0e103b, float:1.8883465E38)
            java.lang.String r11 = "ExternalStorage"
            r10 = 2131625793(0x7f0e0741, float:1.8878804E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)     // Catch:{ Exception -> 0x0192 }
            r0.title = r10     // Catch:{ Exception -> 0x0192 }
        L_0x0171:
            r10 = 2131625792(0x7f0e0740, float:1.8878802E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r9, r10)     // Catch:{ Exception -> 0x0192 }
            r0.subtitle = r11     // Catch:{ Exception -> 0x0192 }
            r11 = 2131165402(0x7var_da, float:1.794502E38)
            r0.icon = r11     // Catch:{ Exception -> 0x0190 }
            java.io.File r10 = new java.io.File     // Catch:{ Exception -> 0x0190 }
            r10.<init>(r8)     // Catch:{ Exception -> 0x0190 }
            r0.file = r10     // Catch:{ Exception -> 0x0190 }
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r10 = r1.listAdapter     // Catch:{ Exception -> 0x0190 }
            java.util.ArrayList r10 = r10.items     // Catch:{ Exception -> 0x0190 }
            r10.add(r0)     // Catch:{ Exception -> 0x0190 }
            goto L_0x01a1
        L_0x0190:
            r0 = move-exception
            goto L_0x0196
        L_0x0192:
            r0 = move-exception
            r11 = 2131165402(0x7var_da, float:1.794502E38)
        L_0x0196:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x01bc }
            goto L_0x01a1
        L_0x019a:
            r11 = 2131165402(0x7var_da, float:1.794502E38)
            goto L_0x01a1
        L_0x019e:
            r11 = 2131165402(0x7var_da, float:1.794502E38)
        L_0x01a1:
            r8 = 2131625792(0x7f0e0740, float:1.8878802E38)
            r10 = 2131165402(0x7var_da, float:1.794502E38)
            r11 = 2131628091(0x7f0e103b, float:1.8883465E38)
            goto L_0x00a5
        L_0x01ac:
            r13.close()     // Catch:{ Exception -> 0x01b1 }
        L_0x01b0:
            goto L_0x01c6
        L_0x01b1:
            r0 = move-exception
            r8 = r0
            r0 = r8
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01b0
        L_0x01b8:
            r0 = move-exception
            r2 = r0
            goto L_0x0278
        L_0x01bc:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x01b8 }
            if (r13 == 0) goto L_0x01c6
            r13.close()     // Catch:{ Exception -> 0x01b1 }
            goto L_0x01b0
        L_0x01c6:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x01fa }
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x01fa }
            java.io.File r8 = r8.getExternalFilesDir(r3)     // Catch:{ Exception -> 0x01fa }
            r0.<init>(r8, r2)     // Catch:{ Exception -> 0x01fa }
            boolean r8 = r0.exists()     // Catch:{ Exception -> 0x01fa }
            if (r8 == 0) goto L_0x01f9
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r8 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem     // Catch:{ Exception -> 0x01fa }
            r8.<init>()     // Catch:{ Exception -> 0x01fa }
            r8.title = r2     // Catch:{ Exception -> 0x01fa }
            java.lang.String r2 = "AppFolderInfo"
            r9 = 2131624366(0x7f0e01ae, float:1.887591E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r9)     // Catch:{ Exception -> 0x01fa }
            r8.subtitle = r2     // Catch:{ Exception -> 0x01fa }
            r2 = 2131165400(0x7var_d8, float:1.7945016E38)
            r8.icon = r2     // Catch:{ Exception -> 0x01fa }
            r8.file = r0     // Catch:{ Exception -> 0x01fa }
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r2 = r1.listAdapter     // Catch:{ Exception -> 0x01fa }
            java.util.ArrayList r2 = r2.items     // Catch:{ Exception -> 0x01fa }
            r2.add(r8)     // Catch:{ Exception -> 0x01fa }
        L_0x01f9:
            goto L_0x01fe
        L_0x01fa:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01fe:
            boolean r0 = r1.isSoundPicker
            if (r0 != 0) goto L_0x022d
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r0.<init>()
            r2 = 2131626051(0x7f0e0843, float:1.8879327E38)
            java.lang.String r8 = "Gallery"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            r0.title = r2
            r2 = 2131626052(0x7f0e0844, float:1.887933E38)
            java.lang.String r8 = "GalleryInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            r0.subtitle = r2
            r2 = 2131165401(0x7var_d9, float:1.7945018E38)
            r0.icon = r2
            r0.file = r3
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r2 = r1.listAdapter
            java.util.ArrayList r2 = r2.items
            r2.add(r0)
        L_0x022d:
            boolean r0 = r1.allowMusic
            if (r0 == 0) goto L_0x025c
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem r0 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListItem
            r0.<init>()
            r2 = 2131624501(0x7f0e0235, float:1.8876183E38)
            java.lang.String r8 = "AttachMusic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            r0.title = r2
            r2 = 2131626744(0x7f0e0af8, float:1.8880733E38)
            java.lang.String r8 = "MusicInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            r0.subtitle = r2
            r2 = 2131165403(0x7var_db, float:1.7945022E38)
            r0.icon = r2
            r0.file = r3
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r2 = r1.listAdapter
            java.util.ArrayList r2 = r2.items
            r2.add(r0)
        L_0x025c:
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r0 = r1.listAdapter
            java.util.ArrayList r0 = r0.recentItems
            boolean r0 = r0.isEmpty()
            r2 = 1
            if (r0 != 0) goto L_0x026b
            r1.hasFiles = r2
        L_0x026b:
            org.telegram.ui.Components.RecyclerListView r0 = r1.listView
            org.telegram.messenger.AndroidUtilities.clearDrawableAnimation(r0)
            r1.scrolling = r2
            org.telegram.ui.Components.ChatAttachAlertDocumentLayout$ListAdapter r0 = r1.listAdapter
            r0.notifyDataSetChanged()
            return
        L_0x0278:
            if (r13 == 0) goto L_0x0284
            r13.close()     // Catch:{ Exception -> 0x027e }
            goto L_0x0284
        L_0x027e:
            r0 = move-exception
            r3 = r0
            r0 = r3
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0284:
            goto L_0x0286
        L_0x0285:
            throw r2
        L_0x0286:
            goto L_0x0285
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.listRoots():void");
    }

    private String getRootSubtitle(String path) {
        try {
            StatFs stat = new StatFs(path);
            long total = ((long) stat.getBlockCount()) * ((long) stat.getBlockSize());
            long free = ((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize());
            if (total == 0) {
                return "";
            }
            return LocaleController.formatString("FreeOfTotal", NUM, AndroidUtilities.formatFileSize(free), AndroidUtilities.formatFileSize(total));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return path;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public ArrayList<HistoryEntry> history = new ArrayList<>();
        /* access modifiers changed from: private */
        public ArrayList<ListItem> items = new ArrayList<>();
        private Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<ListItem> recentItems = new ArrayList<>();

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1;
        }

        public int getItemCount() {
            int count = this.items.size();
            if (this.history.isEmpty() && !this.recentItems.isEmpty()) {
                count += this.recentItems.size() + 2;
            }
            return count + 1;
        }

        public ListItem getItem(int position) {
            int position2;
            int itemsSize = this.items.size();
            if (position < itemsSize) {
                return this.items.get(position);
            }
            if (!this.history.isEmpty() || this.recentItems.isEmpty() || position == itemsSize || position == itemsSize + 1 || (position2 = position - (this.items.size() + 2)) >= this.recentItems.size()) {
                return null;
            }
            return this.recentItems.get(position2);
        }

        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return 3;
            }
            int itemsSize = this.items.size();
            if (position == itemsSize) {
                return 2;
            }
            if (position == itemsSize + 1) {
                return 0;
            }
            return 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                    break;
                case 1:
                    view = new SharedDocumentCell(this.mContext, 1, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                    break;
                case 2:
                    view = new ShadowSectionCell(this.mContext);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(ChatAttachAlertDocumentLayout.this.getThemedColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
                default:
                    view = new View(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (ChatAttachAlertDocumentLayout.this.sortByName) {
                        headerCell.setText(LocaleController.getString("RecentFilesAZ", NUM));
                        return;
                    } else {
                        headerCell.setText(LocaleController.getString("RecentFiles", NUM));
                        return;
                    }
                case 1:
                    ListItem item = getItem(position);
                    SharedDocumentCell documentCell = (SharedDocumentCell) holder.itemView;
                    if (item.icon != 0) {
                        documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, (String) null, (String) null, item.icon, position != this.items.size() - 1);
                    } else {
                        documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4)), item.thumb, 0, false);
                    }
                    if (item.file != null) {
                        documentCell.setChecked(ChatAttachAlertDocumentLayout.this.selectedFiles.containsKey(item.file.toString()), !ChatAttachAlertDocumentLayout.this.scrolling);
                        return;
                    } else {
                        documentCell.setChecked(false, !ChatAttachAlertDocumentLayout.this.scrolling);
                        return;
                    }
                default:
                    return;
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
        private boolean firstLoading = true;
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
        private int searchIndex;
        /* access modifiers changed from: private */
        public ArrayList<ListItem> searchResult = new ArrayList<>();
        private Runnable searchRunnable;
        public HashMap<String, ArrayList<MessageObject>> sectionArrays = new HashMap<>();
        public ArrayList<String> sections = new ArrayList<>();

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(String query, boolean reset) {
            Runnable runnable = this.localSearchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.localSearchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                if (!this.searchResult.isEmpty()) {
                    this.searchResult.clear();
                }
                if (ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.listAdapter) {
                    ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.listAdapter);
                }
                notifyDataSetChanged();
                String str = query;
            } else {
                ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2 chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2 = new ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2(this, query);
                this.localSearchRunnable = chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2;
                AndroidUtilities.runOnUIThread(chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda2, 300);
            }
            if (!ChatAttachAlertDocumentLayout.this.canSelectOnlyImageFiles && ChatAttachAlertDocumentLayout.this.listAdapter.history.isEmpty()) {
                long dialogId = 0;
                long minDate = 0;
                long maxDate = 0;
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
                    }
                }
                searchGlobal(dialogId, minDate, maxDate, FiltersView.filters[2], query, reset);
            }
        }

        /* renamed from: lambda$search$1$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m794xbcebf2b8(String query) {
            ArrayList<ListItem> copy = new ArrayList<>(ChatAttachAlertDocumentLayout.this.listAdapter.items);
            if (ChatAttachAlertDocumentLayout.this.listAdapter.history.isEmpty()) {
                copy.addAll(0, ChatAttachAlertDocumentLayout.this.listAdapter.recentItems);
            }
            Utilities.searchQueue.postRunnable(new ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda3(this, query, !this.currentSearchFilters.isEmpty(), copy));
        }

        /* renamed from: lambda$search$0$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m793x3aa13dd9(String query, boolean hasFilters, ArrayList copy) {
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList(), query);
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
            search[0] = search1;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<ListItem> resultArray = new ArrayList<>();
            if (!hasFilters) {
                for (int a = 0; a < copy.size(); a++) {
                    ListItem entry = (ListItem) copy.get(a);
                    if (entry.file != null && !entry.file.isDirectory()) {
                        int b = 0;
                        while (true) {
                            if (b >= search.length) {
                                break;
                            }
                            String q = search[b];
                            boolean ok = false;
                            if (entry.title != null) {
                                ok = entry.title.toLowerCase().contains(q);
                            }
                            if (ok) {
                                resultArray.add(entry);
                                break;
                            }
                            b++;
                        }
                    }
                }
            }
            updateSearchResults(resultArray, query);
        }

        public void loadMore() {
            FiltersView.MediaFilterData mediaFilterData;
            if (!ChatAttachAlertDocumentLayout.this.searchAdapter.isLoading && !ChatAttachAlertDocumentLayout.this.searchAdapter.endReached && (mediaFilterData = this.currentSearchFilter) != null) {
                searchGlobal(this.currentSearchDialogId, this.currentSearchMinDate, this.currentSearchMaxDate, mediaFilterData, this.lastMessagesSearchString, false);
            }
        }

        public void removeSearchFilter(FiltersView.MediaFilterData filterData) {
            this.currentSearchFilters.remove(filterData);
        }

        public void clear() {
            this.currentSearchFilters.clear();
        }

        /* access modifiers changed from: private */
        public void addSearchFilter(FiltersView.MediaFilterData filter) {
            if (!this.currentSearchFilters.isEmpty()) {
                int i = 0;
                while (i < this.currentSearchFilters.size()) {
                    if (!filter.isSameType(this.currentSearchFilters.get(i))) {
                        i++;
                    } else {
                        return;
                    }
                }
            }
            this.currentSearchFilters.add(filter);
            ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.setSearchFilter(filter);
            ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.setSearchFieldText("");
            updateFiltersView(true, (ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, true);
        }

        /* access modifiers changed from: private */
        public void updateFiltersView(boolean showMediaFilters, ArrayList<Object> users, ArrayList<FiltersView.DateData> dates, boolean animated) {
            int i;
            boolean hasMediaFilter = false;
            boolean hasUserFilter = false;
            boolean hasDataFilter = false;
            int i2 = 0;
            while (true) {
                i = 4;
                if (i2 >= this.currentSearchFilters.size()) {
                    break;
                }
                if (this.currentSearchFilters.get(i2).isMedia()) {
                    hasMediaFilter = true;
                } else if (this.currentSearchFilters.get(i2).filterType == 4) {
                    hasUserFilter = true;
                } else if (this.currentSearchFilters.get(i2).filterType == 6) {
                    hasDataFilter = true;
                }
                i2++;
            }
            boolean visible = false;
            boolean hasUsersOrDates = (users != null && !users.isEmpty()) || (dates != null && !dates.isEmpty());
            int i3 = null;
            if ((hasMediaFilter || hasUsersOrDates || !showMediaFilters) && hasUsersOrDates) {
                ArrayList<Object> finalUsers = (users == null || users.isEmpty() || hasUserFilter) ? null : users;
                ArrayList<FiltersView.DateData> finalDates = (dates == null || dates.isEmpty() || hasDataFilter) ? null : dates;
                if (!(finalUsers == null && finalDates == null)) {
                    visible = true;
                    ChatAttachAlertDocumentLayout.this.filtersView.setUsersAndDates(finalUsers, finalDates, false);
                }
            }
            if (!visible) {
                ChatAttachAlertDocumentLayout.this.filtersView.setUsersAndDates((ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, false);
            }
            ChatAttachAlertDocumentLayout.this.filtersView.setEnabled(visible);
            if (visible && ChatAttachAlertDocumentLayout.this.filtersView.getTag() != null) {
                return;
            }
            if (visible || ChatAttachAlertDocumentLayout.this.filtersView.getTag() != null) {
                FiltersView access$2800 = ChatAttachAlertDocumentLayout.this.filtersView;
                if (visible) {
                    i3 = 1;
                }
                access$2800.setTag(i3);
                if (ChatAttachAlertDocumentLayout.this.filtersViewAnimator != null) {
                    ChatAttachAlertDocumentLayout.this.filtersViewAnimator.cancel();
                }
                if (animated) {
                    if (visible) {
                        ChatAttachAlertDocumentLayout.this.filtersView.setVisibility(0);
                    }
                    AnimatorSet unused = ChatAttachAlertDocumentLayout.this.filtersViewAnimator = new AnimatorSet();
                    AnimatorSet access$2900 = ChatAttachAlertDocumentLayout.this.filtersViewAnimator;
                    Animator[] animatorArr = new Animator[4];
                    RecyclerListView access$000 = ChatAttachAlertDocumentLayout.this.listView;
                    Property property = View.TRANSLATION_Y;
                    float[] fArr = new float[1];
                    fArr[0] = visible ? (float) AndroidUtilities.dp(44.0f) : 0.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(access$000, property, fArr);
                    FiltersView access$28002 = ChatAttachAlertDocumentLayout.this.filtersView;
                    Property property2 = View.TRANSLATION_Y;
                    float[] fArr2 = new float[1];
                    fArr2[0] = visible ? 0.0f : (float) (-AndroidUtilities.dp(44.0f));
                    animatorArr[1] = ObjectAnimator.ofFloat(access$28002, property2, fArr2);
                    FlickerLoadingView access$3000 = ChatAttachAlertDocumentLayout.this.loadingView;
                    Property property3 = View.TRANSLATION_Y;
                    float[] fArr3 = new float[1];
                    fArr3[0] = visible ? (float) AndroidUtilities.dp(44.0f) : 0.0f;
                    animatorArr[2] = ObjectAnimator.ofFloat(access$3000, property3, fArr3);
                    StickerEmptyView access$3100 = ChatAttachAlertDocumentLayout.this.emptyView;
                    Property property4 = View.TRANSLATION_Y;
                    float[] fArr4 = new float[1];
                    fArr4[0] = visible ? (float) AndroidUtilities.dp(44.0f) : 0.0f;
                    animatorArr[3] = ObjectAnimator.ofFloat(access$3100, property4, fArr4);
                    access$2900.playTogether(animatorArr);
                    ChatAttachAlertDocumentLayout.this.filtersViewAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ChatAttachAlertDocumentLayout.this.filtersView.getTag() == null) {
                                ChatAttachAlertDocumentLayout.this.filtersView.setVisibility(4);
                            }
                            AnimatorSet unused = ChatAttachAlertDocumentLayout.this.filtersViewAnimator = null;
                        }
                    });
                    ChatAttachAlertDocumentLayout.this.filtersViewAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    ChatAttachAlertDocumentLayout.this.filtersViewAnimator.setDuration(180);
                    ChatAttachAlertDocumentLayout.this.filtersViewAnimator.start();
                    return;
                }
                ChatAttachAlertDocumentLayout.this.filtersView.getAdapter().notifyDataSetChanged();
                ChatAttachAlertDocumentLayout.this.listView.setTranslationY(visible ? (float) AndroidUtilities.dp(44.0f) : 0.0f);
                ChatAttachAlertDocumentLayout.this.filtersView.setTranslationY(visible ? 0.0f : (float) (-AndroidUtilities.dp(44.0f)));
                ChatAttachAlertDocumentLayout.this.loadingView.setTranslationY(visible ? (float) AndroidUtilities.dp(44.0f) : 0.0f);
                ChatAttachAlertDocumentLayout.this.emptyView.setTranslationY(visible ? (float) AndroidUtilities.dp(44.0f) : 0.0f);
                FiltersView access$28003 = ChatAttachAlertDocumentLayout.this.filtersView;
                if (visible) {
                    i = 0;
                }
                access$28003.setVisibility(i);
            }
        }

        private void searchGlobal(long dialogId, long minDate, long maxDate, FiltersView.MediaFilterData searchFilter, String query, boolean clearOldResults) {
            long j = dialogId;
            long j2 = minDate;
            long j3 = maxDate;
            FiltersView.MediaFilterData mediaFilterData = searchFilter;
            String currentSearchFilterQueryString = String.format(Locale.ENGLISH, "%d%d%d%d%s", new Object[]{Long.valueOf(dialogId), Long.valueOf(minDate), Long.valueOf(maxDate), Integer.valueOf(mediaFilterData.filterType), query});
            String str = this.lastSearchFilterQueryString;
            boolean filterAndQueryIsSame = str != null && str.equals(currentSearchFilterQueryString);
            boolean forceClear = !filterAndQueryIsSame && clearOldResults;
            boolean z = j == this.currentSearchDialogId && this.currentSearchMinDate == j2 && this.currentSearchMaxDate == j3;
            this.currentSearchFilter = mediaFilterData;
            this.currentSearchDialogId = j;
            this.currentSearchMinDate = j2;
            this.currentSearchMaxDate = j3;
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            AndroidUtilities.cancelRunOnUIThread(this.clearCurrentResultsRunnable);
            if (!filterAndQueryIsSame || !clearOldResults) {
                if (forceClear) {
                    this.messages.clear();
                    this.sections.clear();
                    this.sectionArrays.clear();
                    this.isLoading = true;
                    ChatAttachAlertDocumentLayout.this.emptyView.setVisibility(0);
                    notifyDataSetChanged();
                    this.requestIndex++;
                    this.firstLoading = true;
                    if (ChatAttachAlertDocumentLayout.this.listView.getPinnedHeader() != null) {
                        ChatAttachAlertDocumentLayout.this.listView.getPinnedHeader().setAlpha(0.0f);
                    }
                    this.localTipChats.clear();
                    this.localTipDates.clear();
                }
                this.isLoading = true;
                notifyDataSetChanged();
                if (!filterAndQueryIsSame) {
                    this.clearCurrentResultsRunnable.run();
                    ChatAttachAlertDocumentLayout.this.emptyView.showProgress(true, !clearOldResults);
                }
                if (TextUtils.isEmpty(query)) {
                    this.localTipDates.clear();
                    this.localTipChats.clear();
                    updateFiltersView(false, (ArrayList<Object>) null, (ArrayList<FiltersView.DateData>) null, true);
                    return;
                }
                this.requestIndex++;
                ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1 chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1 = r0;
                ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1 chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda12 = new ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1(this, dialogId, query, AccountInstance.getInstance(UserConfig.selectedAccount), minDate, maxDate, filterAndQueryIsSame, currentSearchFilterQueryString, this.requestIndex);
                this.searchRunnable = chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1;
                AndroidUtilities.runOnUIThread(chatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda1, (!filterAndQueryIsSame || this.messages.isEmpty()) ? 350 : 0);
                ChatAttachAlertDocumentLayout.this.loadingView.setViewType(3);
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_search} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: org.telegram.tgnet.TLRPC$TL_messages_searchGlobal} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* renamed from: lambda$searchGlobal$4$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m797x66d53cb2(long r19, java.lang.String r21, org.telegram.messenger.AccountInstance r22, long r23, long r25, boolean r27, java.lang.String r28, int r29) {
            /*
                r18 = this;
                r12 = r18
                r13 = r19
                r15 = r21
                r0 = 0
                r7 = 20
                r8 = 0
                r9 = 1000(0x3e8, double:4.94E-321)
                r16 = 0
                int r1 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
                if (r1 == 0) goto L_0x006c
                org.telegram.tgnet.TLRPC$TL_messages_search r1 = new org.telegram.tgnet.TLRPC$TL_messages_search
                r1.<init>()
                r1.q = r15
                r1.limit = r7
                org.telegram.ui.Adapters.FiltersView$MediaFilterData r2 = r12.currentSearchFilter
                org.telegram.tgnet.TLRPC$MessagesFilter r2 = r2.filter
                r1.filter = r2
                org.telegram.messenger.MessagesController r2 = r22.getMessagesController()
                org.telegram.tgnet.TLRPC$InputPeer r2 = r2.getInputPeer((long) r13)
                r1.peer = r2
                int r2 = (r23 > r16 ? 1 : (r23 == r16 ? 0 : -1))
                if (r2 <= 0) goto L_0x0034
                long r2 = r23 / r9
                int r3 = (int) r2
                r1.min_date = r3
            L_0x0034:
                int r2 = (r25 > r16 ? 1 : (r25 == r16 ? 0 : -1))
                if (r2 <= 0) goto L_0x003d
                long r2 = r25 / r9
                int r3 = (int) r2
                r1.max_date = r3
            L_0x003d:
                if (r27 == 0) goto L_0x0064
                java.lang.String r2 = r12.lastMessagesSearchString
                boolean r2 = r15.equals(r2)
                if (r2 == 0) goto L_0x0064
                java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r12.messages
                boolean r2 = r2.isEmpty()
                if (r2 != 0) goto L_0x0064
                java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r12.messages
                int r3 = r2.size()
                int r3 = r3 + -1
                java.lang.Object r2 = r2.get(r3)
                org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
                int r3 = r2.getId()
                r1.offset_id = r3
                goto L_0x0066
            L_0x0064:
                r1.offset_id = r8
            L_0x0066:
                r16 = r0
                r11 = r1
                goto L_0x011c
            L_0x006c:
                boolean r1 = android.text.TextUtils.isEmpty(r21)
                if (r1 != 0) goto L_0x008d
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                java.util.ArrayList r5 = new java.util.ArrayList
                r5.<init>()
                org.telegram.messenger.MessagesStorage r0 = r22.getMessagesStorage()
                r1 = 0
                r6 = -1
                r2 = r21
                r0.localSearch(r1, r2, r3, r4, r5, r6)
                r0 = r3
            L_0x008d:
                org.telegram.tgnet.TLRPC$TL_messages_searchGlobal r1 = new org.telegram.tgnet.TLRPC$TL_messages_searchGlobal
                r1.<init>()
                r1.limit = r7
                r1.q = r15
                org.telegram.ui.Adapters.FiltersView$MediaFilterData r2 = r12.currentSearchFilter
                org.telegram.tgnet.TLRPC$MessagesFilter r2 = r2.filter
                r1.filter = r2
                int r2 = (r23 > r16 ? 1 : (r23 == r16 ? 0 : -1))
                if (r2 <= 0) goto L_0x00a5
                long r2 = r23 / r9
                int r3 = (int) r2
                r1.min_date = r3
            L_0x00a5:
                int r2 = (r25 > r16 ? 1 : (r25 == r16 ? 0 : -1))
                if (r2 <= 0) goto L_0x00ae
                long r2 = r25 / r9
                int r3 = (int) r2
                r1.max_date = r3
            L_0x00ae:
                if (r27 == 0) goto L_0x010d
                java.lang.String r2 = r12.lastMessagesSearchString
                boolean r2 = r15.equals(r2)
                if (r2 == 0) goto L_0x010d
                java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r12.messages
                boolean r2 = r2.isEmpty()
                if (r2 != 0) goto L_0x010d
                java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r12.messages
                int r3 = r2.size()
                int r3 = r3 + -1
                java.lang.Object r2 = r2.get(r3)
                org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
                int r3 = r2.getId()
                r1.offset_id = r3
                int r3 = r12.nextSearchRate
                r1.offset_rate = r3
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
                long r3 = r3.channel_id
                int r5 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
                if (r5 == 0) goto L_0x00ea
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
                long r3 = r3.channel_id
                long r3 = -r3
                goto L_0x0102
            L_0x00ea:
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
                long r3 = r3.chat_id
                int r5 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
                if (r5 == 0) goto L_0x00fc
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
                long r3 = r3.chat_id
                long r3 = -r3
                goto L_0x0102
            L_0x00fc:
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
                long r3 = r3.user_id
            L_0x0102:
                org.telegram.messenger.MessagesController r5 = r22.getMessagesController()
                org.telegram.tgnet.TLRPC$InputPeer r5 = r5.getInputPeer((long) r3)
                r1.offset_peer = r5
                goto L_0x0118
            L_0x010d:
                r1.offset_rate = r8
                r1.offset_id = r8
                org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
                r2.<init>()
                r1.offset_peer = r2
            L_0x0118:
                r2 = r1
                r16 = r0
                r11 = r2
            L_0x011c:
                r12.lastMessagesSearchString = r15
                r8 = r28
                r12.lastSearchFilterQueryString = r8
                r10 = r16
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r9 = r0
                java.lang.String r0 = r12.lastMessagesSearchString
                org.telegram.ui.Adapters.FiltersView.fillTipDates(r0, r9)
                org.telegram.tgnet.ConnectionsManager r6 = r22.getConnectionsManager()
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda5 r7 = new org.telegram.ui.Components.ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda5
                r0 = r7
                r1 = r18
                r2 = r22
                r3 = r21
                r4 = r29
                r5 = r27
                r12 = r6
                r13 = r7
                r6 = r19
                r14 = r9
                r8 = r23
                r15 = r11
                r11 = r14
                r0.<init>(r1, r2, r3, r4, r5, r6, r8, r10, r11)
                r12.sendRequest(r15, r13)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.m797x66d53cb2(long, java.lang.String, org.telegram.messenger.AccountInstance, long, long, boolean, java.lang.String, int):void");
        }

        /* renamed from: lambda$searchGlobal$3$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m796xe48a87d3(AccountInstance accountInstance, String query, int requestId, boolean filterAndQueryIsSame, long dialogId, long minDate, ArrayList finalResultArray, ArrayList dateData, TLObject response, TLRPC.TL_error error) {
            ArrayList<MessageObject> messageObjects = new ArrayList<>();
            if (error == null) {
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                int n = res.messages.size();
                for (int i = 0; i < n; i++) {
                    MessageObject messageObject = new MessageObject(accountInstance.getCurrentAccount(), res.messages.get(i), false, true);
                    messageObject.setQuery(query);
                    messageObjects.add(messageObject);
                }
                String str = query;
            } else {
                String str2 = query;
            }
            AndroidUtilities.runOnUIThread(new ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda0(this, requestId, error, response, accountInstance, filterAndQueryIsSame, query, messageObjects, dialogId, minDate, finalResultArray, dateData));
        }

        /* renamed from: lambda$searchGlobal$2$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m795x623fd2f4(int requestId, TLRPC.TL_error error, TLObject response, AccountInstance accountInstance, boolean filterAndQueryIsSame, String query, ArrayList messageObjects, long dialogId, long minDate, ArrayList finalResultArray, ArrayList dateData) {
            String str = query;
            ArrayList arrayList = finalResultArray;
            if (requestId == this.requestIndex) {
                this.isLoading = false;
                if (error != null) {
                    ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", NUM));
                    ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                    ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
                    ChatAttachAlertDocumentLayout.this.emptyView.showProgress(false, true);
                    return;
                }
                ChatAttachAlertDocumentLayout.this.emptyView.showProgress(false);
                TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
                this.nextSearchRate = res.next_rate;
                accountInstance.getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
                accountInstance.getMessagesController().putUsers(res.users, false);
                accountInstance.getMessagesController().putChats(res.chats, false);
                if (!filterAndQueryIsSame) {
                    this.messages.clear();
                    this.messagesById.clear();
                    this.sections.clear();
                    this.sectionArrays.clear();
                }
                int totalCount = res.count;
                this.currentDataQuery = str;
                int n = messageObjects.size();
                for (int i = 0; i < n; i++) {
                    MessageObject messageObject = (MessageObject) messageObjects.get(i);
                    ArrayList<MessageObject> messageObjectsByDate = this.sectionArrays.get(messageObject.monthKey);
                    if (messageObjectsByDate == null) {
                        messageObjectsByDate = new ArrayList<>();
                        this.sectionArrays.put(messageObject.monthKey, messageObjectsByDate);
                        this.sections.add(messageObject.monthKey);
                    }
                    messageObjectsByDate.add(messageObject);
                    this.messages.add(messageObject);
                    this.messagesById.put(messageObject.getId(), messageObject);
                }
                ArrayList arrayList2 = messageObjects;
                if (this.messages.size() > totalCount) {
                    totalCount = this.messages.size();
                }
                this.endReached = this.messages.size() >= totalCount;
                if (this.messages.isEmpty()) {
                    if (TextUtils.isEmpty(this.currentDataQuery) && dialogId == 0 && minDate == 0) {
                        ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle", NUM));
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitleFiles", NUM));
                    } else {
                        ChatAttachAlertDocumentLayout.this.emptyView.title.setText(LocaleController.getString("SearchEmptyViewTitle2", NUM));
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setVisibility(0);
                        ChatAttachAlertDocumentLayout.this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
                    }
                }
                if (!filterAndQueryIsSame) {
                    this.localTipChats.clear();
                    if (arrayList != null) {
                        this.localTipChats.addAll(arrayList);
                    }
                    if (query.length() >= 3 && (LocaleController.getString("SavedMessages", NUM).toLowerCase().startsWith(str) || "saved messages".startsWith(str))) {
                        boolean found = false;
                        int i2 = 0;
                        while (true) {
                            if (i2 < this.localTipChats.size()) {
                                if ((this.localTipChats.get(i2) instanceof TLRPC.User) && UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == ((TLRPC.User) this.localTipChats.get(i2)).id) {
                                    found = true;
                                    break;
                                } else {
                                    i2++;
                                    int i3 = requestId;
                                }
                            } else {
                                break;
                            }
                        }
                        if (!found) {
                            this.localTipChats.add(0, UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser());
                        }
                    }
                    this.localTipDates.clear();
                    this.localTipDates.addAll(dateData);
                    updateFiltersView(TextUtils.isEmpty(this.currentDataQuery), this.localTipChats, this.localTipDates, true);
                } else {
                    ArrayList arrayList3 = dateData;
                }
                this.firstLoading = false;
                View progressView = null;
                int progressViewPosition = -1;
                for (int i4 = 0; i4 < n; i4++) {
                    View child = ChatAttachAlertDocumentLayout.this.listView.getChildAt(i4);
                    if (child instanceof FlickerLoadingView) {
                        progressView = child;
                        progressViewPosition = ChatAttachAlertDocumentLayout.this.listView.getChildAdapterPosition(child);
                    }
                }
                final View finalProgressView = progressView;
                if (progressView != null) {
                    ChatAttachAlertDocumentLayout.this.listView.removeView(progressView);
                }
                if ((ChatAttachAlertDocumentLayout.this.loadingView.getVisibility() != 0 || ChatAttachAlertDocumentLayout.this.listView.getChildCount() > 1) && progressView == null) {
                    AccountInstance accountInstance2 = accountInstance;
                } else {
                    final int finalProgressViewPosition = progressViewPosition;
                    final AccountInstance accountInstance3 = accountInstance;
                    ChatAttachAlertDocumentLayout.this.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            ChatAttachAlertDocumentLayout.this.getViewTreeObserver().removeOnPreDrawListener(this);
                            int n = ChatAttachAlertDocumentLayout.this.listView.getChildCount();
                            AnimatorSet animatorSet = new AnimatorSet();
                            for (int i = 0; i < n; i++) {
                                View child = ChatAttachAlertDocumentLayout.this.listView.getChildAt(i);
                                if (finalProgressView == null || ChatAttachAlertDocumentLayout.this.listView.getChildAdapterPosition(child) >= finalProgressViewPosition) {
                                    child.setAlpha(0.0f);
                                    ObjectAnimator a = ObjectAnimator.ofFloat(child, View.ALPHA, new float[]{0.0f, 1.0f});
                                    a.setStartDelay((long) ((int) ((((float) Math.min(ChatAttachAlertDocumentLayout.this.listView.getMeasuredHeight(), Math.max(0, child.getTop()))) / ((float) ChatAttachAlertDocumentLayout.this.listView.getMeasuredHeight())) * 100.0f)));
                                    a.setDuration(200);
                                    animatorSet.playTogether(new Animator[]{a});
                                }
                            }
                            animatorSet.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    accountInstance3.getNotificationCenter().onAnimationFinish(SearchAdapter.this.animationIndex);
                                }
                            });
                            int unused = SearchAdapter.this.animationIndex = accountInstance3.getNotificationCenter().setAnimationInProgress(SearchAdapter.this.animationIndex, (int[]) null);
                            animatorSet.start();
                            View view = finalProgressView;
                            if (view != null && view.getParent() == null) {
                                ChatAttachAlertDocumentLayout.this.listView.addView(finalProgressView);
                                final RecyclerView.LayoutManager layoutManager = ChatAttachAlertDocumentLayout.this.listView.getLayoutManager();
                                if (layoutManager != null) {
                                    layoutManager.ignoreView(finalProgressView);
                                    Animator animator = ObjectAnimator.ofFloat(finalProgressView, View.ALPHA, new float[]{finalProgressView.getAlpha(), 0.0f});
                                    animator.addListener(new AnimatorListenerAdapter() {
                                        public void onAnimationEnd(Animator animation) {
                                            finalProgressView.setAlpha(1.0f);
                                            layoutManager.stopIgnoringView(finalProgressView);
                                            ChatAttachAlertDocumentLayout.this.listView.removeView(finalProgressView);
                                        }
                                    });
                                    animator.start();
                                }
                            }
                            return true;
                        }
                    });
                }
                notifyDataSetChanged();
            }
        }

        private void updateSearchResults(ArrayList<ListItem> result, String query) {
            AndroidUtilities.runOnUIThread(new ChatAttachAlertDocumentLayout$SearchAdapter$$ExternalSyntheticLambda4(this, result));
        }

        /* renamed from: lambda$updateSearchResults$5$org-telegram-ui-Components-ChatAttachAlertDocumentLayout$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m798xe95e5031(ArrayList result) {
            if (ChatAttachAlertDocumentLayout.this.searching && ChatAttachAlertDocumentLayout.this.listView.getAdapter() != ChatAttachAlertDocumentLayout.this.searchAdapter) {
                ChatAttachAlertDocumentLayout.this.listView.setAdapter(ChatAttachAlertDocumentLayout.this.searchAdapter);
            }
            this.searchResult = result;
            notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            int type = holder.getItemViewType();
            return type == 1 || type == 4;
        }

        public int getSectionCount() {
            if (!this.sections.isEmpty()) {
                return 2 + this.sections.size() + (this.endReached ^ true ? 1 : 0);
            }
            return 2;
        }

        public Object getItem(int section, int position) {
            ArrayList<MessageObject> arrayList;
            if (section != 0) {
                int section2 = section - 1;
                if (section2 >= this.sections.size() || (arrayList = this.sectionArrays.get(this.sections.get(section2))) == null) {
                    return null;
                }
                return arrayList.get(position - ((section2 != 0 || !this.searchResult.isEmpty()) ? 1 : 0));
            } else if (position < this.searchResult.size()) {
                return this.searchResult.get(position);
            } else {
                return null;
            }
        }

        public int getCountForSection(int section) {
            if (section == 0) {
                return this.searchResult.size();
            }
            int section2 = section - 1;
            int i = 1;
            if (section2 >= this.sections.size()) {
                return 1;
            }
            ArrayList<MessageObject> arrayList = this.sectionArrays.get(this.sections.get(section2));
            if (arrayList == null) {
                return 0;
            }
            int size = arrayList.size();
            if (section2 == 0 && this.searchResult.isEmpty()) {
                i = 0;
            }
            return size + i;
        }

        public View getSectionHeaderView(int section, View view) {
            String str;
            GraySectionCell sectionCell = (GraySectionCell) view;
            if (sectionCell == null) {
                sectionCell = new GraySectionCell(this.mContext, ChatAttachAlertDocumentLayout.this.resourcesProvider);
                sectionCell.setBackgroundColor(ChatAttachAlertDocumentLayout.this.getThemedColor("graySection") & -NUM);
            }
            if (section == 0 || (section == 1 && this.searchResult.isEmpty())) {
                sectionCell.setAlpha(0.0f);
                return sectionCell;
            }
            int section2 = section - 1;
            if (section2 < this.sections.size()) {
                sectionCell.setAlpha(1.0f);
                ArrayList<MessageObject> messageObjects = this.sectionArrays.get(this.sections.get(section2));
                if (messageObjects != null) {
                    MessageObject messageObject = messageObjects.get(0);
                    if (section2 != 0 || this.searchResult.isEmpty()) {
                        str = LocaleController.formatSectionDate((long) messageObject.messageOwner.date);
                    } else {
                        str = LocaleController.getString("GlobalSearch", NUM);
                    }
                    sectionCell.setText(str);
                }
            }
            return view;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.ui.Cells.GraySectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.telegram.ui.Cells.SharedDocumentCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
            /*
                r4 = this;
                r0 = 1
                switch(r6) {
                    case 0: goto L_0x0036;
                    case 1: goto L_0x0020;
                    case 2: goto L_0x000c;
                    case 3: goto L_0x0004;
                    case 4: goto L_0x0020;
                    default: goto L_0x0004;
                }
            L_0x0004:
                android.view.View r0 = new android.view.View
                android.content.Context r1 = r4.mContext
                r0.<init>(r1)
                goto L_0x0042
            L_0x000c:
                org.telegram.ui.Components.FlickerLoadingView r1 = new org.telegram.ui.Components.FlickerLoadingView
                android.content.Context r2 = r4.mContext
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r3 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                r1.<init>(r2, r3)
                r2 = 3
                r1.setViewType(r2)
                r1.setIsSingleCell(r0)
                r0 = r1
                goto L_0x0042
            L_0x0020:
                org.telegram.ui.Cells.SharedDocumentCell r1 = new org.telegram.ui.Cells.SharedDocumentCell
                android.content.Context r2 = r4.mContext
                if (r6 != r0) goto L_0x0027
                goto L_0x0028
            L_0x0027:
                r0 = 2
            L_0x0028:
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r3 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                r1.<init>(r2, r0, r3)
                r0 = r1
                r1 = 0
                r0.setDrawDownloadIcon(r1)
                r1 = r0
                goto L_0x0042
            L_0x0036:
                org.telegram.ui.Cells.GraySectionCell r0 = new org.telegram.ui.Cells.GraySectionCell
                android.content.Context r1 = r4.mContext
                org.telegram.ui.Components.ChatAttachAlertDocumentLayout r2 = org.telegram.ui.Components.ChatAttachAlertDocumentLayout.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r2 = r2.resourcesProvider
                r0.<init>(r1, r2)
            L_0x0042:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -1
                r3 = -2
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertDocumentLayout.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            String str;
            int position2 = position;
            RecyclerView.ViewHolder viewHolder = holder;
            int viewType = holder.getItemViewType();
            if (viewType != 2 && viewType != 3) {
                boolean z = false;
                switch (viewType) {
                    case 0:
                        int section2 = section - 1;
                        ArrayList<MessageObject> messageObjects = this.sectionArrays.get(this.sections.get(section2));
                        if (messageObjects != null) {
                            MessageObject messageObject = messageObjects.get(0);
                            if (section2 != 0 || this.searchResult.isEmpty()) {
                                str = LocaleController.formatSectionDate((long) messageObject.messageOwner.date);
                            } else {
                                str = LocaleController.getString("GlobalSearch", NUM);
                            }
                            ((GraySectionCell) viewHolder.itemView).setText(str);
                            return;
                        }
                        return;
                    case 1:
                    case 4:
                        final SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                        if (section == 0) {
                            ListItem item = (ListItem) getItem(position2);
                            SharedDocumentCell documentCell = (SharedDocumentCell) viewHolder.itemView;
                            if (item.icon != 0) {
                                documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, (String) null, (String) null, item.icon, false);
                            } else {
                                documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4)), item.thumb, 0, false);
                            }
                            if (item.file == null) {
                                documentCell.setChecked(false, true ^ ChatAttachAlertDocumentLayout.this.scrolling);
                                break;
                            } else {
                                documentCell.setChecked(ChatAttachAlertDocumentLayout.this.selectedFiles.containsKey(item.file.toString()), true ^ ChatAttachAlertDocumentLayout.this.scrolling);
                                break;
                            }
                        } else {
                            int section3 = section - 1;
                            if (section3 != 0 || !this.searchResult.isEmpty()) {
                                position2--;
                            }
                            ArrayList<MessageObject> messageObjects2 = this.sectionArrays.get(this.sections.get(section3));
                            if (messageObjects2 != null) {
                                final MessageObject messageObject2 = messageObjects2.get(position2);
                                final boolean animated = sharedDocumentCell.getMessage() != null && sharedDocumentCell.getMessage().getId() == messageObject2.getId();
                                if (position2 != messageObjects2.size() - 1 || (section3 == this.sections.size() - 1 && this.isLoading)) {
                                    z = true;
                                }
                                sharedDocumentCell.setDocument(messageObject2, z);
                                sharedDocumentCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                    public boolean onPreDraw() {
                                        sharedDocumentCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                        if (ChatAttachAlertDocumentLayout.this.parentAlert.actionBar.isActionModeShowed()) {
                                            SearchAdapter.this.messageHashIdTmp.set(messageObject2.getId(), messageObject2.getDialogId());
                                            sharedDocumentCell.setChecked(ChatAttachAlertDocumentLayout.this.selectedMessages.containsKey(SearchAdapter.this.messageHashIdTmp), animated);
                                            return true;
                                        }
                                        sharedDocumentCell.setChecked(false, animated);
                                        return true;
                                    }
                                });
                                int i = section3;
                                return;
                            }
                            return;
                        }
                        break;
                }
            }
        }

        public int getItemViewType(int section, int position) {
            if (section == 0) {
                return 1;
            }
            if (section == getSectionCount() - 1) {
                return 3;
            }
            int section2 = section - 1;
            if (section2 >= this.sections.size()) {
                return 2;
            }
            if ((section2 != 0 || !this.searchResult.isEmpty()) && position == 0) {
                return 0;
            }
            return 4;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            ChatAttachAlertDocumentLayout.this.updateEmptyView();
        }

        public String getLetter(int position) {
            return null;
        }

        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = 0;
            position[1] = 0;
        }
    }

    /* access modifiers changed from: package-private */
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.searchItem.getSearchField(), ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_folderIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_folderIconBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_iconText"));
        return themeDescriptions;
    }
}
