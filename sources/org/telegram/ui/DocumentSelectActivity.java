package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$InputDocument;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.DocumentSelectActivity;
import org.telegram.ui.PhotoPickerActivity;

public class DocumentSelectActivity extends BaseFragment {
    private boolean allowMusic;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private boolean canSelectOnlyImageFiles;
    private ChatActivity chatActivity;
    /* access modifiers changed from: private */
    public EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public File currentDir;
    /* access modifiers changed from: private */
    public DocumentSelectActivityDelegate delegate;
    private ImageView emptyImageView;
    /* access modifiers changed from: private */
    public TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    /* access modifiers changed from: private */
    public LinearLayout emptyView;
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    private boolean hasFiles;
    /* access modifiers changed from: private */
    public ArrayList<HistoryEntry> history = new ArrayList<>();
    private ActionBarMenuSubItem[] itemCells;
    /* access modifiers changed from: private */
    public ArrayList<ListItem> items = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private int maxSelectedFiles = -1;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            $$Lambda$DocumentSelectActivity$1$QUHI_wOoyDp1Dwx1fGWii_dVi9g r3 = new Runnable() {
                public final void run() {
                    DocumentSelectActivity.AnonymousClass1.this.lambda$onReceive$0$DocumentSelectActivity$1();
                }
            };
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                DocumentSelectActivity.this.listView.postDelayed(r3, 1000);
            } else {
                r3.run();
            }
        }

        public /* synthetic */ void lambda$onReceive$0$DocumentSelectActivity$1() {
            try {
                if (DocumentSelectActivity.this.currentDir == null) {
                    DocumentSelectActivity.this.listRoots();
                } else {
                    boolean unused = DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
                }
                DocumentSelectActivity.this.updateSearchButton();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    };
    private boolean receiverRegistered = false;
    /* access modifiers changed from: private */
    public ArrayList<ListItem> recentItems = new ArrayList<>();
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public boolean scrolling;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private View selectedCountView;
    /* access modifiers changed from: private */
    public HashMap<String, ListItem> selectedFiles = new HashMap<>();
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    private View shadow;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public boolean sortByName;
    /* access modifiers changed from: private */
    public ActionBarMenuItem sortItem;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    private ImageView writeButton;
    /* access modifiers changed from: private */
    public FrameLayout writeButtonContainer;

    public interface DocumentSelectActivityDelegate {

        /* renamed from: org.telegram.ui.DocumentSelectActivity$DocumentSelectActivityDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$startMusicSelectActivity(DocumentSelectActivityDelegate documentSelectActivityDelegate, BaseFragment baseFragment) {
            }
        }

        void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList, String str, boolean z, int i);

        void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i);

        void startDocumentSelectActivity();

        void startMusicSelectActivity(BaseFragment baseFragment);
    }

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$createView$3(View view, MotionEvent motionEvent) {
        return true;
    }

    private class ListItem {
        public String ext;
        public File file;
        public int icon;
        public String subtitle;
        public String thumb;
        public String title;

        private ListItem(DocumentSelectActivity documentSelectActivity) {
            this.subtitle = "";
            this.ext = "";
        }
    }

    private class HistoryEntry {
        File dir;
        int scrollItem;
        int scrollOffset;
        String title;

        private HistoryEntry(DocumentSelectActivity documentSelectActivity) {
        }
    }

    public DocumentSelectActivity(boolean z) {
        this.allowMusic = z;
    }

    public boolean onFragmentCreate() {
        this.sortByName = SharedConfig.sortFilesByName;
        loadRecentFiles();
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        try {
            if (this.receiverRegistered) {
                ApplicationLoader.applicationContext.unregisterReceiver(this.receiver);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        Context context2 = context;
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
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
        this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SelectFile", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (DocumentSelectActivity.this.canClosePicker()) {
                        DocumentSelectActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    SharedConfig.toggleSortFilesByName();
                    boolean unused = DocumentSelectActivity.this.sortByName = SharedConfig.sortFilesByName;
                    DocumentSelectActivity.this.sortRecentItems();
                    DocumentSelectActivity.this.sortFileItems();
                    DocumentSelectActivity.this.listAdapter.notifyDataSetChanged();
                    DocumentSelectActivity.this.sortItem.setIcon(DocumentSelectActivity.this.sortByName ? NUM : NUM);
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        ActionBarMenuItem addItem = createMenu.addItem(0, NUM);
        addItem.setIsSearchField(true);
        addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = DocumentSelectActivity.this.searching = true;
                DocumentSelectActivity.this.sortItem.setVisibility(8);
            }

            public void onSearchCollapse() {
                boolean unused = DocumentSelectActivity.this.searching = false;
                boolean unused2 = DocumentSelectActivity.this.searchWas = false;
                DocumentSelectActivity.this.sortItem.setVisibility(0);
                if (DocumentSelectActivity.this.listView.getAdapter() != DocumentSelectActivity.this.listAdapter) {
                    DocumentSelectActivity.this.listView.setAdapter(DocumentSelectActivity.this.listAdapter);
                }
                DocumentSelectActivity.this.updateEmptyView();
                DocumentSelectActivity.this.searchAdapter.search((String) null);
            }

            public void onTextChanged(EditText editText) {
                DocumentSelectActivity.this.searchAdapter.search(editText.getText().toString());
            }
        });
        this.searchItem = addItem;
        addItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchItem.setContentDescription(LocaleController.getString("Search", NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(Theme.getColor("dialogTextBlack"));
        searchField.setCursorColor(Theme.getColor("dialogTextBlack"));
        searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        ActionBarMenuItem addItem2 = createMenu.addItem(1, this.sortByName ? NUM : NUM);
        this.sortItem = addItem2;
        addItem2.setContentDescription(LocaleController.getString("AccDescrContactSorting", NUM));
        this.selectedFiles.clear();
        AnonymousClass4 r3 = new SizeNotifierFrameLayout(context2, SharedConfig.smoothKeyboard) {
            private boolean ignoreLayout;
            private int lastNotifyWidth;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int i3;
                int i4;
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int keyboardHeight = getKeyboardHeight();
                if ((SharedConfig.smoothKeyboard ? 0 : keyboardHeight) > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || DocumentSelectActivity.this.commentTextView == null || DocumentSelectActivity.this.frameLayout2.getParent() != this) {
                    i4 = i2;
                    i3 = size2;
                } else {
                    int emojiPadding = size2 - DocumentSelectActivity.this.commentTextView.getEmojiPadding();
                    i3 = emojiPadding;
                    i4 = View.MeasureSpec.makeMeasureSpec(emojiPadding, NUM);
                }
                if (keyboardHeight > AndroidUtilities.dp(20.0f) && DocumentSelectActivity.this.commentTextView != null) {
                    this.ignoreLayout = true;
                    DocumentSelectActivity.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                if (SharedConfig.smoothKeyboard && DocumentSelectActivity.this.commentTextView != null && DocumentSelectActivity.this.commentTextView.isPopupShowing()) {
                    DocumentSelectActivity.this.fragmentView.setTranslationY((float) DocumentSelectActivity.this.getCurrentPanTranslationY());
                    DocumentSelectActivity.this.listView.setTranslationY(0.0f);
                    DocumentSelectActivity.this.emptyView.setTranslationY(0.0f);
                }
                int childCount = getChildCount();
                for (int i5 = 0; i5 < childCount; i5++) {
                    View childAt = getChildAt(i5);
                    if (!(childAt == null || childAt.getVisibility() == 8)) {
                        if (DocumentSelectActivity.this.commentTextView == null || !DocumentSelectActivity.this.commentTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i4, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (i3 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((i3 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x00b9  */
            /* JADX WARNING: Removed duplicated region for block: B:47:0x00d3  */
            /* JADX WARNING: Removed duplicated region for block: B:55:0x00fa  */
            /* JADX WARNING: Removed duplicated region for block: B:56:0x0103  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                    r9 = this;
                    int r10 = r9.lastNotifyWidth
                    int r13 = r13 - r11
                    if (r10 == r13) goto L_0x0024
                    r9.lastNotifyWidth = r13
                    org.telegram.ui.DocumentSelectActivity r10 = org.telegram.ui.DocumentSelectActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r10 = r10.sendPopupWindow
                    if (r10 == 0) goto L_0x0024
                    org.telegram.ui.DocumentSelectActivity r10 = org.telegram.ui.DocumentSelectActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r10 = r10.sendPopupWindow
                    boolean r10 = r10.isShowing()
                    if (r10 == 0) goto L_0x0024
                    org.telegram.ui.DocumentSelectActivity r10 = org.telegram.ui.DocumentSelectActivity.this
                    org.telegram.ui.ActionBar.ActionBarPopupWindow r10 = r10.sendPopupWindow
                    r10.dismiss()
                L_0x0024:
                    int r10 = r9.getChildCount()
                    boolean r11 = org.telegram.messenger.SharedConfig.smoothKeyboard
                    r0 = 0
                    if (r11 == 0) goto L_0x002f
                    r11 = 0
                    goto L_0x0033
                L_0x002f:
                    int r11 = r9.getKeyboardHeight()
                L_0x0033:
                    org.telegram.ui.DocumentSelectActivity r1 = org.telegram.ui.DocumentSelectActivity.this
                    org.telegram.ui.Components.EditTextEmoji r1 = r1.commentTextView
                    if (r1 == 0) goto L_0x0064
                    org.telegram.ui.DocumentSelectActivity r1 = org.telegram.ui.DocumentSelectActivity.this
                    android.widget.FrameLayout r1 = r1.frameLayout2
                    android.view.ViewParent r1 = r1.getParent()
                    if (r1 != r9) goto L_0x0064
                    r1 = 1101004800(0x41a00000, float:20.0)
                    int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                    if (r11 > r1) goto L_0x0064
                    boolean r1 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
                    if (r1 != 0) goto L_0x0064
                    boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r1 != 0) goto L_0x0064
                    org.telegram.ui.DocumentSelectActivity r1 = org.telegram.ui.DocumentSelectActivity.this
                    org.telegram.ui.Components.EditTextEmoji r1 = r1.commentTextView
                    int r1 = r1.getEmojiPadding()
                    goto L_0x0065
                L_0x0064:
                    r1 = 0
                L_0x0065:
                    r9.setBottomClip(r1)
                L_0x0068:
                    if (r0 >= r10) goto L_0x0116
                    android.view.View r2 = r9.getChildAt(r0)
                    int r3 = r2.getVisibility()
                    r4 = 8
                    if (r3 != r4) goto L_0x0078
                    goto L_0x0112
                L_0x0078:
                    android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
                    int r4 = r2.getMeasuredWidth()
                    int r5 = r2.getMeasuredHeight()
                    int r6 = r3.gravity
                    r7 = -1
                    if (r6 != r7) goto L_0x008d
                    r6 = 51
                L_0x008d:
                    r7 = r6 & 7
                    r6 = r6 & 112(0x70, float:1.57E-43)
                    r7 = r7 & 7
                    r8 = 1
                    if (r7 == r8) goto L_0x00ab
                    r8 = 5
                    if (r7 == r8) goto L_0x00a1
                    int r7 = r3.leftMargin
                    int r8 = r9.getPaddingLeft()
                    int r7 = r7 + r8
                    goto L_0x00b5
                L_0x00a1:
                    int r7 = r13 - r4
                    int r8 = r3.rightMargin
                    int r7 = r7 - r8
                    int r8 = r9.getPaddingRight()
                    goto L_0x00b4
                L_0x00ab:
                    int r7 = r13 - r4
                    int r7 = r7 / 2
                    int r8 = r3.leftMargin
                    int r7 = r7 + r8
                    int r8 = r3.rightMargin
                L_0x00b4:
                    int r7 = r7 - r8
                L_0x00b5:
                    r8 = 16
                    if (r6 == r8) goto L_0x00d3
                    r8 = 48
                    if (r6 == r8) goto L_0x00cb
                    r8 = 80
                    if (r6 == r8) goto L_0x00c4
                    int r3 = r3.topMargin
                    goto L_0x00e0
                L_0x00c4:
                    int r6 = r14 - r1
                    int r6 = r6 - r12
                    int r6 = r6 - r5
                    int r3 = r3.bottomMargin
                    goto L_0x00de
                L_0x00cb:
                    int r3 = r3.topMargin
                    int r6 = r9.getPaddingTop()
                    int r3 = r3 + r6
                    goto L_0x00e0
                L_0x00d3:
                    int r6 = r14 - r1
                    int r6 = r6 - r12
                    int r6 = r6 - r5
                    int r6 = r6 / 2
                    int r8 = r3.topMargin
                    int r6 = r6 + r8
                    int r3 = r3.bottomMargin
                L_0x00de:
                    int r3 = r6 - r3
                L_0x00e0:
                    org.telegram.ui.DocumentSelectActivity r6 = org.telegram.ui.DocumentSelectActivity.this
                    org.telegram.ui.Components.EditTextEmoji r6 = r6.commentTextView
                    if (r6 == 0) goto L_0x010d
                    org.telegram.ui.DocumentSelectActivity r6 = org.telegram.ui.DocumentSelectActivity.this
                    org.telegram.ui.Components.EditTextEmoji r6 = r6.commentTextView
                    boolean r6 = r6.isPopupView(r2)
                    if (r6 == 0) goto L_0x010d
                    boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
                    if (r3 == 0) goto L_0x0103
                    int r3 = r9.getMeasuredHeight()
                    int r6 = r2.getMeasuredHeight()
                    goto L_0x010c
                L_0x0103:
                    int r3 = r9.getMeasuredHeight()
                    int r3 = r3 + r11
                    int r6 = r2.getMeasuredHeight()
                L_0x010c:
                    int r3 = r3 - r6
                L_0x010d:
                    int r4 = r4 + r7
                    int r5 = r5 + r3
                    r2.layout(r7, r3, r4, r5)
                L_0x0112:
                    int r0 = r0 + 1
                    goto L_0x0068
                L_0x0116:
                    r9.notifyHeightChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DocumentSelectActivity.AnonymousClass4.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout = r3;
        r3.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.fragmentView = this.sizeNotifierFrameLayout;
        LinearLayout linearLayout = new LinearLayout(context2);
        this.emptyView = linearLayout;
        linearLayout.setOrientation(1);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener($$Lambda$DocumentSelectActivity$wOAW0__TUXtkjQZ0vg8d4FmxpE.INSTANCE);
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
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setClipToPadding(false);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView3.setAdapter(listAdapter2);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchAdapter = new SearchAdapter(context2);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                boolean unused = DocumentSelectActivity.this.scrolling = i != 0;
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(DocumentSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                DocumentSelectActivity.this.lambda$createView$1$DocumentSelectActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return DocumentSelectActivity.this.lambda$createView$2$DocumentSelectActivity(view, i);
            }
        });
        View view = new View(context2);
        this.shadow = view;
        view.setBackgroundResource(NUM);
        this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.frameLayout2 = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener($$Lambda$DocumentSelectActivity$_4RF1KR3EMjps5KjB_LWZWCJnu0.INSTANCE);
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, (BaseFragment) null, 1);
        this.commentTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
        this.commentTextView.setHint(LocaleController.getString("AddCaption", NUM));
        this.commentTextView.onResume();
        EditTextBoldCursor editText = this.commentTextView.getEditText();
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
        if (this.chatActivity == null) {
            this.commentTextView.setVisibility(8);
        }
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.writeButtonContainer = frameLayout3;
        frameLayout3.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", NUM));
        this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
        this.writeButton = new ImageView(context2);
        int dp = AndroidUtilities.dp(56.0f);
        String str = "dialogFloatingButton";
        int color = Theme.getColor(str);
        if (Build.VERSION.SDK_INT >= 21) {
            str = "dialogFloatingButtonPressed";
        }
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(dp, color, Theme.getColor(str));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.writeButton.setImageResource(NUM);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        if (Build.VERSION.SDK_INT >= 21) {
            this.writeButton.setOutlineProvider(new ViewOutlineProvider(this) {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        this.writeButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                DocumentSelectActivity.this.lambda$createView$4$DocumentSelectActivity(view);
            }
        });
        this.writeButton.setOnLongClickListener(new View.OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return DocumentSelectActivity.this.lambda$createView$7$DocumentSelectActivity(view);
            }
        });
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        AnonymousClass9 r32 = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, DocumentSelectActivity.this.selectedFiles.size()))});
                int ceil = (int) Math.ceil((double) DocumentSelectActivity.this.textPaint.measureText(format));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ceil, AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                DocumentSelectActivity.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                DocumentSelectActivity.this.paint.setColor(Theme.getColor("dialogBackground"));
                int i = max / 2;
                int i2 = measuredWidth - i;
                int i3 = i + measuredWidth;
                DocumentSelectActivity.this.rect.set((float) i2, 0.0f, (float) i3, (float) getMeasuredHeight());
                canvas.drawRoundRect(DocumentSelectActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), DocumentSelectActivity.this.paint);
                DocumentSelectActivity.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                DocumentSelectActivity.this.rect.set((float) (i2 + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (i3 - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                canvas.drawRoundRect(DocumentSelectActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), DocumentSelectActivity.this.paint);
                canvas.drawText(format, (float) (measuredWidth - (ceil / 2)), (float) AndroidUtilities.dp(16.2f), DocumentSelectActivity.this.textPaint);
            }
        };
        this.selectedCountView = r32;
        r32.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
        listRoots();
        updateSearchButton();
        updateEmptyView();
        updateCountButton(0);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$DocumentSelectActivity(View view, int i) {
        ListItem listItem;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        ListAdapter listAdapter2 = this.listAdapter;
        if (adapter == listAdapter2) {
            listItem = listAdapter2.getItem(i);
        } else {
            listItem = this.searchAdapter.getItem(i);
        }
        if (listItem != null) {
            File file = listItem.file;
            if (file == null) {
                int i2 = listItem.icon;
                if (i2 == NUM) {
                    final HashMap hashMap = new HashMap();
                    final ArrayList arrayList = new ArrayList();
                    PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(0, MediaController.allMediaAlbumEntry, hashMap, arrayList, 0, this.chatActivity != null, this.chatActivity);
                    photoPickerActivity.setDocumentsPicker(true);
                    photoPickerActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                        public void onCaptionChanged(CharSequence charSequence) {
                        }

                        public void selectedPhotosChanged() {
                        }

                        public void actionButtonPressed(boolean z, boolean z2, int i) {
                            DocumentSelectActivity.this.removeSelfFromStack();
                            if (!z) {
                                DocumentSelectActivity.this.sendSelectedPhotos(hashMap, arrayList, z2, i);
                            }
                        }

                        public void onOpenInPressed() {
                            DocumentSelectActivity.this.removeSelfFromStack();
                            DocumentSelectActivity.this.delegate.startDocumentSelectActivity();
                        }
                    });
                    photoPickerActivity.setMaxSelectedPhotos(this.maxSelectedFiles, false);
                    presentFragment(photoPickerActivity);
                } else if (i2 == NUM) {
                    DocumentSelectActivityDelegate documentSelectActivityDelegate = this.delegate;
                    if (documentSelectActivityDelegate != null) {
                        documentSelectActivityDelegate.startMusicSelectActivity(this);
                    }
                } else {
                    ArrayList<HistoryEntry> arrayList2 = this.history;
                    HistoryEntry remove = arrayList2.remove(arrayList2.size() - 1);
                    this.actionBar.setTitle(remove.title);
                    File file2 = remove.dir;
                    if (file2 != null) {
                        listFiles(file2);
                    } else {
                        listRoots();
                    }
                    updateSearchButton();
                    this.layoutManager.scrollToPositionWithOffset(remove.scrollItem, remove.scrollOffset);
                }
            } else if (file.isDirectory()) {
                HistoryEntry historyEntry = new HistoryEntry();
                int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
                historyEntry.scrollItem = findLastVisibleItemPosition;
                View findViewByPosition = this.layoutManager.findViewByPosition(findLastVisibleItemPosition);
                if (findViewByPosition != null) {
                    historyEntry.scrollOffset = findViewByPosition.getTop();
                }
                historyEntry.dir = this.currentDir;
                historyEntry.title = this.actionBar.getTitle();
                this.history.add(historyEntry);
                if (!listFiles(file)) {
                    this.history.remove(historyEntry);
                } else {
                    this.actionBar.setTitle(listItem.title);
                }
            } else {
                onItemClick(view, listItem);
            }
        }
    }

    public /* synthetic */ boolean lambda$createView$2$DocumentSelectActivity(View view, int i) {
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

    public /* synthetic */ void lambda$createView$4$DocumentSelectActivity(View view) {
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null || !chatActivity2.isInScheduleMode()) {
            sendSelectedFiles(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    DocumentSelectActivity.this.sendSelectedFiles(z, i);
                }
            });
        }
    }

    public /* synthetic */ boolean lambda$createView$7$DocumentSelectActivity(View view) {
        View view2 = view;
        ChatActivity chatActivity2 = this.chatActivity;
        if (chatActivity2 == null) {
            return false;
        }
        chatActivity2.getCurrentChat();
        TLRPC$User currentUser = this.chatActivity.getCurrentUser();
        if (this.chatActivity.getCurrentEncryptedChat() != null) {
            return false;
        }
        if (this.sendPopupLayout == null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity());
            this.sendPopupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setAnimationEnabled(false);
            this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getActionMasked() != 0 || DocumentSelectActivity.this.sendPopupWindow == null || !DocumentSelectActivity.this.sendPopupWindow.isShowing()) {
                        return false;
                    }
                    view.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                        return false;
                    }
                    DocumentSelectActivity.this.sendPopupWindow.dismiss();
                    return false;
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    DocumentSelectActivity.this.lambda$null$5$DocumentSelectActivity(keyEvent);
                }
            });
            this.sendPopupLayout.setShowedFromBotton(false);
            this.itemCells = new ActionBarMenuSubItem[2];
            for (int i = 0; i < 2; i++) {
                if (i != 1 || !UserObject.isUserSelf(currentUser)) {
                    this.itemCells[i] = new ActionBarMenuSubItem(getParentActivity());
                    if (i == 0) {
                        if (UserObject.isUserSelf(currentUser)) {
                            this.itemCells[i].setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                        } else {
                            this.itemCells[i].setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                        }
                    } else if (i == 1) {
                        this.itemCells[i].setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                    }
                    this.itemCells[i].setMinimumWidth(AndroidUtilities.dp(196.0f));
                    this.sendPopupLayout.addView(this.itemCells[i], LayoutHelper.createFrame(-1, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i * 48), 0.0f, 0.0f));
                    this.itemCells[i].setOnClickListener(new View.OnClickListener(i) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(View view) {
                            DocumentSelectActivity.this.lambda$null$6$DocumentSelectActivity(this.f$1, view);
                        }
                    });
                }
            }
            ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
            this.sendPopupWindow = actionBarPopupWindow;
            actionBarPopupWindow.setAnimationEnabled(false);
            this.sendPopupWindow.setAnimationStyle(NUM);
            this.sendPopupWindow.setOutsideTouchable(true);
            this.sendPopupWindow.setClippingEnabled(true);
            this.sendPopupWindow.setInputMethodMode(2);
            this.sendPopupWindow.setSoftInputMode(0);
            this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        }
        this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] iArr = new int[2];
        view2.getLocationInWindow(iArr);
        this.sendPopupWindow.showAtLocation(view2, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
        this.sendPopupWindow.dimBehind();
        view2.performHapticFeedback(3, 2);
        return false;
    }

    public /* synthetic */ void lambda$null$5$DocumentSelectActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$6$DocumentSelectActivity(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    DocumentSelectActivity.this.sendSelectedFiles(z, i);
                }
            });
        } else if (i == 1) {
            sendSelectedFiles(true, 0);
        }
    }

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(int i) {
        if (this.listView != null) {
            if (this.commentTextView.isPopupShowing()) {
                this.fragmentView.setTranslationY((float) i);
                this.listView.setTranslationY(0.0f);
                this.emptyView.setTranslationY(0.0f);
                return;
            }
            float f = (float) i;
            this.listView.setTranslationY(f);
            this.emptyView.setTranslationY(f);
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
            z = true;
        }
        this.scrolling = false;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(z, true);
        }
        updateCountButton(z ? 1 : 2);
        return true;
    }

    public void setChatActivity(ChatActivity chatActivity2) {
        this.chatActivity = chatActivity2;
    }

    public void setMaxSelectedFiles(int i) {
        this.maxSelectedFiles = i;
    }

    public void setCanSelectOnlyImageFiles(boolean z) {
        this.canSelectOnlyImageFiles = true;
    }

    private boolean showCommentTextView(final boolean z, boolean z2) {
        if (this.commentTextView == null) {
            return false;
        }
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.frameLayout2.setTag(z ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        float f = 0.0f;
        float f2 = 0.2f;
        float f3 = 1.0f;
        if (z2) {
            this.animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            FrameLayout frameLayout = this.writeButtonContainer;
            Property property = View.SCALE_X;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout3, property2, fArr2));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout4, property3, fArr3));
            View view = this.selectedCountView;
            Property property4 = View.SCALE_X;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(view, property4, fArr4));
            View view2 = this.selectedCountView;
            Property property5 = View.SCALE_Y;
            float[] fArr5 = new float[1];
            if (z) {
                f2 = 1.0f;
            }
            fArr5[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(view2, property5, fArr5));
            View view3 = this.selectedCountView;
            Property property6 = View.ALPHA;
            float[] fArr6 = new float[1];
            if (!z) {
                f3 = 0.0f;
            }
            fArr6[0] = f3;
            arrayList.add(ObjectAnimator.ofFloat(view3, property6, fArr6));
            FrameLayout frameLayout5 = this.frameLayout2;
            Property property7 = View.TRANSLATION_Y;
            float[] fArr7 = new float[1];
            fArr7[0] = z ? 0.0f : (float) AndroidUtilities.dp(48.0f);
            arrayList.add(ObjectAnimator.ofFloat(frameLayout5, property7, fArr7));
            View view4 = this.shadow;
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            fArr8[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
            this.animatorSet.playTogether(arrayList);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(DocumentSelectActivity.this.animatorSet)) {
                        if (!z) {
                            DocumentSelectActivity.this.frameLayout2.setVisibility(4);
                            DocumentSelectActivity.this.writeButtonContainer.setVisibility(4);
                        }
                        AnimatorSet unused = DocumentSelectActivity.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(DocumentSelectActivity.this.animatorSet)) {
                        AnimatorSet unused = DocumentSelectActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
            View view5 = this.selectedCountView;
            if (z) {
                f2 = 1.0f;
            }
            view5.setScaleY(f2);
            View view6 = this.selectedCountView;
            if (!z) {
                f3 = 0.0f;
            }
            view6.setAlpha(f3);
            this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
            View view7 = this.shadow;
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            view7.setTranslationY(f);
            if (!z) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    public void updateCountButton(int i) {
        boolean z = true;
        if (this.selectedFiles.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            if (i == 0) {
                z = false;
            }
            showCommentTextView(false, z);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true, i != 0) || i == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX((float) AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY((float) AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet2 = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = i == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        View view2 = this.selectedCountView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (i != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
        animatorSet2.playTogether(animatorArr);
        animatorSet2.setInterpolator(new OvershootInterpolator());
        animatorSet2.setDuration(180);
        animatorSet2.start();
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
                    if (photoEntry.isVideo) {
                        sendingMediaInfo.path = photoEntry.path;
                        sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                    } else {
                        String str = photoEntry.imagePath;
                        if (str != null) {
                            sendingMediaInfo.path = str;
                        } else {
                            String str2 = photoEntry.path;
                            if (str2 != null) {
                                sendingMediaInfo.path = str2;
                            }
                        }
                    }
                    sendingMediaInfo.isVideo = photoEntry.isVideo;
                    CharSequence charSequence = photoEntry.caption;
                    ArrayList<TLRPC$InputDocument> arrayList3 = null;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = photoEntry.entities;
                    if (!photoEntry.stickers.isEmpty()) {
                        arrayList3 = new ArrayList<>(photoEntry.stickers);
                    }
                    sendingMediaInfo.masks = arrayList3;
                    sendingMediaInfo.ttl = photoEntry.ttl;
                }
            }
            this.delegate.didSelectPhotos(arrayList2, z, i);
        }
    }

    /* access modifiers changed from: private */
    public void sendSelectedFiles(boolean z, int i) {
        if (this.selectedFiles.size() != 0 && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            this.delegate.didSelectFiles(this, new ArrayList(this.selectedFiles.keySet()), this.commentTextView.getText().toString(), z, i);
            finishFragment();
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

    /* access modifiers changed from: private */
    public void sortRecentItems() {
        Collections.sort(this.recentItems, new Comparator() {
            public final int compare(Object obj, Object obj2) {
                return DocumentSelectActivity.this.lambda$sortRecentItems$8$DocumentSelectActivity((DocumentSelectActivity.ListItem) obj, (DocumentSelectActivity.ListItem) obj2);
            }
        });
    }

    public /* synthetic */ int lambda$sortRecentItems$8$DocumentSelectActivity(ListItem listItem, ListItem listItem2) {
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

    /* access modifiers changed from: private */
    public void sortFileItems() {
        if (this.currentDir != null) {
            Collections.sort(this.items, new Comparator() {
                public final int compare(Object obj, Object obj2) {
                    return DocumentSelectActivity.this.lambda$sortFileItems$9$DocumentSelectActivity((DocumentSelectActivity.ListItem) obj, (DocumentSelectActivity.ListItem) obj2);
                }
            });
        }
    }

    public /* synthetic */ int lambda$sortFileItems$9$DocumentSelectActivity(ListItem listItem, ListItem listItem2) {
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
        boolean isDirectory = listItem.file.isDirectory();
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
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        SearchAdapter searchAdapter2 = this.searchAdapter;
        if (searchAdapter2 != null) {
            searchAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        if (this.searching) {
            this.emptyTitleTextView.setText(LocaleController.getString("NoFilesFound", NUM));
            this.emptyView.setGravity(1);
            this.emptyView.setPadding(0, AndroidUtilities.dp(60.0f), 0, 0);
            this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        } else {
            this.emptyTitleTextView.setText(LocaleController.getString("NoFilesFound", NUM));
            this.emptySubtitleTextView.setText(LocaleController.getString("NoFilesInfo", NUM));
            this.emptyView.setGravity(17);
            this.emptyView.setPadding(0, 0, 0, 0);
            this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        }
        this.listView.setEmptyView(this.emptyView);
    }

    /* access modifiers changed from: private */
    public void updateSearchButton() {
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setVisibility(this.hasFiles ? 0 : 8);
            if (this.history.isEmpty()) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchRecentFiles", NUM));
            } else {
                this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean canClosePicker() {
        if (this.history.size() <= 0) {
            return true;
        }
        ArrayList<HistoryEntry> arrayList = this.history;
        HistoryEntry remove = arrayList.remove(arrayList.size() - 1);
        this.actionBar.setTitle(remove.title);
        File file = remove.dir;
        if (file != null) {
            listFiles(file);
        } else {
            listRoots();
        }
        updateSearchButton();
        this.layoutManager.scrollToPositionWithOffset(remove.scrollItem, remove.scrollOffset);
        return false;
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null && editTextEmoji.isPopupShowing()) {
            this.commentTextView.hidePopup(true);
            return false;
        } else if (!canClosePicker()) {
            return false;
        } else {
            return super.onBackPressed();
        }
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
                this.listAdapter.notifyDataSetChanged();
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
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(str);
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.show();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01b4 A[Catch:{ Exception -> 0x01d0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01ff  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0242 A[SYNTHETIC, Splitter:B:84:0x0242] */
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
            java.util.ArrayList<org.telegram.ui.DocumentSelectActivity$ListItem> r2 = r13.items
            r2.clear()
            java.util.HashSet r2 = new java.util.HashSet
            r2.<init>()
            java.io.File r3 = android.os.Environment.getExternalStorageDirectory()
            java.lang.String r3 = r3.getPath()
            android.os.Environment.isExternalStorageRemovable()
            java.lang.String r4 = android.os.Environment.getExternalStorageState()
            java.lang.String r5 = "mounted"
            boolean r5 = r4.equals(r5)
            r6 = 2131625177(0x7f0e04d9, float:1.8877555E38)
            java.lang.String r7 = "ExternalFolderInfo"
            r8 = 2131165380(0x7var_c4, float:1.7944975E38)
            r9 = 2131626605(0x7f0e0a6d, float:1.888045E38)
            java.lang.String r10 = "SdCard"
            if (r5 != 0) goto L_0x003e
            java.lang.String r5 = "mounted_ro"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0081
        L_0x003e:
            org.telegram.ui.DocumentSelectActivity$ListItem r4 = new org.telegram.ui.DocumentSelectActivity$ListItem
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
            r5 = 2131625466(0x7f0e05fa, float:1.887814E38)
            java.lang.String r11 = "InternalStorage"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r4.title = r5
            r5 = 2131165382(0x7var_c6, float:1.794498E38)
            r4.icon = r5
            r5 = 2131625465(0x7f0e05f9, float:1.8878139E38)
            java.lang.String r11 = "InternalFolderInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r4.subtitle = r5
        L_0x0073:
            java.io.File r5 = android.os.Environment.getExternalStorageDirectory()
            r4.file = r5
            java.util.ArrayList<org.telegram.ui.DocumentSelectActivity$ListItem> r5 = r13.items
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
            org.telegram.ui.DocumentSelectActivity$ListItem r4 = new org.telegram.ui.DocumentSelectActivity$ListItem     // Catch:{ Exception -> 0x0162 }
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
            r12 = 2131625178(0x7f0e04da, float:1.8877557E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11, r12)     // Catch:{ Exception -> 0x0162 }
            r4.title = r11     // Catch:{ Exception -> 0x0162 }
        L_0x014c:
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r7, r6)     // Catch:{ Exception -> 0x0162 }
            r4.subtitle = r11     // Catch:{ Exception -> 0x0162 }
            r4.icon = r8     // Catch:{ Exception -> 0x0162 }
            java.io.File r11 = new java.io.File     // Catch:{ Exception -> 0x0162 }
            r11.<init>(r5)     // Catch:{ Exception -> 0x0162 }
            r4.file = r11     // Catch:{ Exception -> 0x0162 }
            java.util.ArrayList<org.telegram.ui.DocumentSelectActivity$ListItem> r5 = r13.items     // Catch:{ Exception -> 0x0162 }
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
            goto L_0x0240
        L_0x0171:
            r2 = move-exception
            r3 = r1
        L_0x0173:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ all -> 0x023e }
            if (r3 == 0) goto L_0x0180
            r3.close()     // Catch:{ Exception -> 0x017c }
            goto L_0x0180
        L_0x017c:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0180:
            org.telegram.ui.DocumentSelectActivity$ListItem r2 = new org.telegram.ui.DocumentSelectActivity$ListItem
            r2.<init>()
            java.lang.String r3 = "/"
            r2.title = r3
            r4 = 2131626897(0x7f0e0b91, float:1.8881043E38)
            java.lang.String r5 = "SystemRoot"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.subtitle = r4
            r4 = 2131165378(0x7var_c2, float:1.7944971E38)
            r2.icon = r4
            java.io.File r5 = new java.io.File
            r5.<init>(r3)
            r2.file = r5
            java.util.ArrayList<org.telegram.ui.DocumentSelectActivity$ListItem> r3 = r13.items
            r3.add(r2)
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x01d0 }
            java.io.File r3 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x01d0 }
            r2.<init>(r3, r0)     // Catch:{ Exception -> 0x01d0 }
            boolean r3 = r2.exists()     // Catch:{ Exception -> 0x01d0 }
            if (r3 == 0) goto L_0x01d4
            org.telegram.ui.DocumentSelectActivity$ListItem r3 = new org.telegram.ui.DocumentSelectActivity$ListItem     // Catch:{ Exception -> 0x01d0 }
            r3.<init>()     // Catch:{ Exception -> 0x01d0 }
            r3.title = r0     // Catch:{ Exception -> 0x01d0 }
            java.lang.String r0 = "AppFolderInfo"
            r5 = 2131624194(0x7f0e0102, float:1.887556E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r5)     // Catch:{ Exception -> 0x01d0 }
            r3.subtitle = r0     // Catch:{ Exception -> 0x01d0 }
            r3.icon = r4     // Catch:{ Exception -> 0x01d0 }
            r3.file = r2     // Catch:{ Exception -> 0x01d0 }
            java.util.ArrayList<org.telegram.ui.DocumentSelectActivity$ListItem> r0 = r13.items     // Catch:{ Exception -> 0x01d0 }
            r0.add(r3)     // Catch:{ Exception -> 0x01d0 }
            goto L_0x01d4
        L_0x01d0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01d4:
            org.telegram.ui.DocumentSelectActivity$ListItem r0 = new org.telegram.ui.DocumentSelectActivity$ListItem
            r0.<init>()
            r2 = 2131625365(0x7f0e0595, float:1.8877936E38)
            java.lang.String r3 = "Gallery"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.title = r2
            r2 = 2131625366(0x7f0e0596, float:1.8877938E38)
            java.lang.String r3 = "GalleryInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.subtitle = r2
            r2 = 2131165379(0x7var_c3, float:1.7944973E38)
            r0.icon = r2
            r0.file = r1
            java.util.ArrayList<org.telegram.ui.DocumentSelectActivity$ListItem> r2 = r13.items
            r2.add(r0)
            boolean r0 = r13.allowMusic
            if (r0 == 0) goto L_0x0226
            org.telegram.ui.DocumentSelectActivity$ListItem r0 = new org.telegram.ui.DocumentSelectActivity$ListItem
            r0.<init>()
            r2 = 2131624294(0x7f0e0166, float:1.8875764E38)
            java.lang.String r3 = "AttachMusic"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.title = r2
            r2 = 2131625738(0x7f0e070a, float:1.8878692E38)
            java.lang.String r3 = "MusicInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.subtitle = r2
            r2 = 2131165381(0x7var_c5, float:1.7944978E38)
            r0.icon = r2
            r0.file = r1
            java.util.ArrayList<org.telegram.ui.DocumentSelectActivity$ListItem> r1 = r13.items
            r1.add(r0)
        L_0x0226:
            java.util.ArrayList<org.telegram.ui.DocumentSelectActivity$ListItem> r0 = r13.recentItems
            boolean r0 = r0.isEmpty()
            r1 = 1
            if (r0 != 0) goto L_0x0231
            r13.hasFiles = r1
        L_0x0231:
            org.telegram.ui.Components.RecyclerListView r0 = r13.listView
            org.telegram.messenger.AndroidUtilities.clearDrawableAnimation(r0)
            r13.scrolling = r1
            org.telegram.ui.DocumentSelectActivity$ListAdapter r0 = r13.listAdapter
            r0.notifyDataSetChanged()
            return
        L_0x023e:
            r0 = move-exception
            r1 = r3
        L_0x0240:
            if (r1 == 0) goto L_0x024a
            r1.close()     // Catch:{ Exception -> 0x0246 }
            goto L_0x024a
        L_0x0246:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x024a:
            goto L_0x024c
        L_0x024b:
            throw r0
        L_0x024c:
            goto L_0x024b
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DocumentSelectActivity.listRoots():void");
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 0;
        }

        public int getItemCount() {
            int size = DocumentSelectActivity.this.items.size();
            return (!DocumentSelectActivity.this.history.isEmpty() || DocumentSelectActivity.this.recentItems.isEmpty()) ? size : size + DocumentSelectActivity.this.recentItems.size() + 1;
        }

        public ListItem getItem(int i) {
            int size;
            int size2 = DocumentSelectActivity.this.items.size();
            if (i < size2) {
                return (ListItem) DocumentSelectActivity.this.items.get(i);
            }
            if (!DocumentSelectActivity.this.history.isEmpty() || DocumentSelectActivity.this.recentItems.isEmpty() || i == size2 || i == size2 + 1 || (size = i - (DocumentSelectActivity.this.items.size() + 2)) >= DocumentSelectActivity.this.recentItems.size()) {
                return null;
            }
            return (ListItem) DocumentSelectActivity.this.recentItems.get(size);
        }

        public int getItemViewType(int i) {
            int size = DocumentSelectActivity.this.items.size();
            if (i == size) {
                return 2;
            }
            return i == size + 1 ? 0 : 1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ShadowSectionCell shadowSectionCell;
            if (i == 0) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setText(LocaleController.getString("RecentFiles", NUM));
                shadowSectionCell = headerCell;
            } else if (i != 1) {
                ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(this.mContext);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable((Context) DocumentSelectActivity.this.getParentActivity(), NUM, "windowBackgroundGrayShadow"));
                combinedDrawable.setFullsize(true);
                shadowSectionCell2.setBackgroundDrawable(combinedDrawable);
                shadowSectionCell = shadowSectionCell2;
            } else {
                shadowSectionCell = new SharedDocumentCell(this.mContext, true);
            }
            return new RecyclerListView.Holder(shadowSectionCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 1) {
                ListItem item = getItem(i);
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                int i2 = item.icon;
                if (i2 != 0) {
                    sharedDocumentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, (String) null, (String) null, i2, i != DocumentSelectActivity.this.items.size() - 1);
                } else {
                    sharedDocumentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4)), item.thumb, 0, false);
                }
                if (item.file != null) {
                    sharedDocumentCell.setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(item.file.toString()), !DocumentSelectActivity.this.scrolling);
                } else {
                    sharedDocumentCell.setChecked(false, !DocumentSelectActivity.this.scrolling);
                }
            }
        }
    }

    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<ListItem> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

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
                if (DocumentSelectActivity.this.listView.getAdapter() != DocumentSelectActivity.this.listAdapter) {
                    DocumentSelectActivity.this.listView.setAdapter(DocumentSelectActivity.this.listAdapter);
                }
                notifyDataSetChanged();
                return;
            }
            $$Lambda$DocumentSelectActivity$SearchAdapter$wdWIwVm5hiS39Qz2qKrMbxOJm8 r0 = new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    DocumentSelectActivity.SearchAdapter.this.lambda$search$1$DocumentSelectActivity$SearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 300);
        }

        public /* synthetic */ void lambda$search$1$DocumentSelectActivity$SearchAdapter(String str) {
            ArrayList arrayList = new ArrayList(DocumentSelectActivity.this.items);
            if (DocumentSelectActivity.this.history.isEmpty()) {
                arrayList.addAll(0, DocumentSelectActivity.this.recentItems);
            }
            Utilities.searchQueue.postRunnable(new Runnable(str, arrayList) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DocumentSelectActivity.SearchAdapter.this.lambda$null$0$DocumentSelectActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$DocumentSelectActivity$SearchAdapter(String str, ArrayList arrayList) {
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
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    DocumentSelectActivity.SearchAdapter.this.lambda$updateSearchResults$2$DocumentSelectActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$2$DocumentSelectActivity$SearchAdapter(String str, ArrayList arrayList) {
            if (DocumentSelectActivity.this.searching) {
                if (DocumentSelectActivity.this.listView.getAdapter() != DocumentSelectActivity.this.searchAdapter) {
                    DocumentSelectActivity.this.listView.setAdapter(DocumentSelectActivity.this.searchAdapter);
                    DocumentSelectActivity.this.updateEmptyView();
                }
                DocumentSelectActivity.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoFilesFoundInfo", NUM, str)));
            }
            boolean unused = DocumentSelectActivity.this.searchWas = true;
            this.searchResult = arrayList;
            notifyDataSetChanged();
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public ListItem getItem(int i) {
            if (i < this.searchResult.size()) {
                return this.searchResult.get(i);
            }
            return null;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new SharedDocumentCell(this.mContext, true));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
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
                sharedDocumentCell.setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(item.file.toString()), !DocumentSelectActivity.this.scrolling);
            } else {
                sharedDocumentCell.setChecked(false, !DocumentSelectActivity.this.scrolling);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[26];
        themeDescriptionArr[0] = new ThemeDescription(this.sizeNotifierFrameLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogButtonSelector");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelHint");
        themeDescriptionArr[7] = new ThemeDescription(this.searchItem.getSearchField(), ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack");
        themeDescriptionArr[8] = new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage");
        themeDescriptionArr[9] = new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText");
        themeDescriptionArr[10] = new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider");
        themeDescriptionArr[16] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[17] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[18] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkbox");
        themeDescriptionArr[19] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck");
        themeDescriptionArr[20] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_folderIcon");
        themeDescriptionArr[21] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_folderIconBackground");
        themeDescriptionArr[22] = new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "files_iconText");
        themeDescriptionArr[23] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogFloatingIcon");
        themeDescriptionArr[24] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogFloatingButton");
        themeDescriptionArr[25] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Build.VERSION.SDK_INT >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton");
        return themeDescriptionArr;
    }
}
