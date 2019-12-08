package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
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
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate;

public class DocumentSelectActivity extends BaseFragment {
    private static final int search_button = 0;
    private static final long sizeLimit = NUM;
    private static final int sort_button = 1;
    private boolean allowMusic;
    private AnimatorSet animatorSet;
    private boolean canSelectOnlyImageFiles;
    private ChatActivity chatActivity;
    private EditTextEmoji commentTextView;
    private File currentDir;
    private DocumentSelectActivityDelegate delegate;
    private ImageView emptyImageView;
    private TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    private FrameLayout frameLayout2;
    private boolean hasFiles;
    private ArrayList<HistoryEntry> history = new ArrayList();
    private ActionBarMenuSubItem[] itemCells;
    private ArrayList<ListItem> items = new ArrayList();
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int maxSelectedFiles = -1;
    private Paint paint = new Paint(1);
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            -$$Lambda$DocumentSelectActivity$1$QUHI_wOoyDp1Dwx1fGWii_dVi9g -__lambda_documentselectactivity_1_quhi_wooydp1dwx1fgwii_dvi9g = new -$$Lambda$DocumentSelectActivity$1$QUHI_wOoyDp1Dwx1fGWii_dVi9g(this);
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                DocumentSelectActivity.this.listView.postDelayed(-__lambda_documentselectactivity_1_quhi_wooydp1dwx1fgwii_dvi9g, 1000);
            } else {
                -__lambda_documentselectactivity_1_quhi_wooydp1dwx1fgwii_dvi9g.run();
            }
        }

        public /* synthetic */ void lambda$onReceive$0$DocumentSelectActivity$1() {
            try {
                if (DocumentSelectActivity.this.currentDir == null) {
                    DocumentSelectActivity.this.listRoots();
                } else {
                    DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
                }
                DocumentSelectActivity.this.updateSearchButton();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    };
    private boolean receiverRegistered = false;
    private ArrayList<ListItem> recentItems = new ArrayList();
    private RectF rect = new RectF();
    private boolean scrolling;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private View selectedCountView;
    private HashMap<String, ListItem> selectedFiles = new HashMap();
    private ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    private View shadow;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private boolean sortByName;
    private ActionBarMenuItem sortItem;
    private TextPaint textPaint = new TextPaint(1);
    private ImageView writeButton;
    private FrameLayout writeButtonContainer;

    public interface DocumentSelectActivityDelegate {

        public final /* synthetic */ class -CC {
            public static void $default$startMusicSelectActivity(DocumentSelectActivityDelegate documentSelectActivityDelegate, BaseFragment baseFragment) {
            }
        }

        void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList, String str, boolean z, int i);

        void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList, boolean z, int i);

        void startDocumentSelectActivity();

        void startMusicSelectActivity(BaseFragment baseFragment);
    }

    private class HistoryEntry {
        File dir;
        int scrollItem;
        int scrollOffset;
        String title;

        private HistoryEntry() {
        }

        /* synthetic */ HistoryEntry(DocumentSelectActivity documentSelectActivity, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private class ListItem {
        public String ext;
        public File file;
        public int icon;
        public String subtitle;
        public String thumb;
        public String title;

        private ListItem() {
            String str = "";
            this.subtitle = str;
            this.ext = str;
        }

        /* synthetic */ ListItem(DocumentSelectActivity documentSelectActivity, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 0;
        }

        public int getItemCount() {
            int size = DocumentSelectActivity.this.items.size();
            return (!DocumentSelectActivity.this.history.isEmpty() || DocumentSelectActivity.this.recentItems.isEmpty()) ? size : size + (DocumentSelectActivity.this.recentItems.size() + 1);
        }

        public ListItem getItem(int i) {
            int size = DocumentSelectActivity.this.items.size();
            if (i < size) {
                return (ListItem) DocumentSelectActivity.this.items.get(i);
            }
            if (!(!DocumentSelectActivity.this.history.isEmpty() || DocumentSelectActivity.this.recentItems.isEmpty() || i == size || i == size + 1)) {
                i -= DocumentSelectActivity.this.items.size() + 2;
                if (i < DocumentSelectActivity.this.recentItems.size()) {
                    return (ListItem) DocumentSelectActivity.this.recentItems.get(i);
                }
            }
            return null;
        }

        public int getItemViewType(int i) {
            int size = DocumentSelectActivity.this.items.size();
            if (i == size) {
                return 2;
            }
            return i == size + 1 ? 0 : 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            if (i == 0) {
                headerCell = new HeaderCell(this.mContext);
                headerCell.setText(LocaleController.getString("RecentFiles", NUM));
            } else if (i != 1) {
                headerCell = new ShadowSectionCell(this.mContext);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(DocumentSelectActivity.this.getParentActivity(), NUM, "windowBackgroundGrayShadow"));
                combinedDrawable.setFullsize(true);
                headerCell.setBackgroundDrawable(combinedDrawable);
            } else {
                headerCell = new SharedDocumentCell(this.mContext, true);
            }
            return new Holder(headerCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 1) {
                ListItem item = getItem(i);
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                int i2 = item.icon;
                if (i2 != 0) {
                    sharedDocumentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, null, null, i2, i != DocumentSelectActivity.this.items.size() - 1);
                } else {
                    sharedDocumentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4)), item.thumb, 0, false);
                }
                if (item.file != null) {
                    sharedDocumentCell.setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(item.file.toString()), DocumentSelectActivity.this.scrolling ^ 1);
                } else {
                    sharedDocumentCell.setChecked(false, DocumentSelectActivity.this.scrolling ^ 1);
                }
            }
        }
    }

    public class SearchAdapter extends SelectionAdapter {
        private int lastReqId;
        private Context mContext;
        private int reqId = 0;
        private ArrayList<ListItem> searchResult = new ArrayList();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
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
            -$$Lambda$DocumentSelectActivity$SearchAdapter$wdWIwVm5hiS39Qz2qKrMbxOJ-m8 -__lambda_documentselectactivity_searchadapter_wdwiwvm5his39qz2qkrmbxoj-m8 = new -$$Lambda$DocumentSelectActivity$SearchAdapter$wdWIwVm5hiS39Qz2qKrMbxOJ-m8(this, str);
            this.searchRunnable = -__lambda_documentselectactivity_searchadapter_wdwiwvm5his39qz2qkrmbxoj-m8;
            AndroidUtilities.runOnUIThread(-__lambda_documentselectactivity_searchadapter_wdwiwvm5his39qz2qkrmbxoj-m8, 300);
        }

        public /* synthetic */ void lambda$search$1$DocumentSelectActivity$SearchAdapter(String str) {
            ArrayList arrayList = new ArrayList(DocumentSelectActivity.this.items);
            if (DocumentSelectActivity.this.history.isEmpty()) {
                arrayList.addAll(0, DocumentSelectActivity.this.recentItems);
            }
            Utilities.searchQueue.postRunnable(new -$$Lambda$DocumentSelectActivity$SearchAdapter$0CN4LuwcSVNS6ARtyNz1-wX_ESg(this, str, arrayList));
        }

        public /* synthetic */ void lambda$null$0$DocumentSelectActivity$SearchAdapter(String str, ArrayList arrayList) {
            String toLowerCase = str.trim().toLowerCase();
            if (toLowerCase.length() == 0) {
                updateSearchResults(new ArrayList(), str);
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
            if (toLowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
            strArr[0] = toLowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                ListItem listItem = (ListItem) arrayList.get(i);
                File file = listItem.file;
                if (file != null && !file.isDirectory()) {
                    for (CharSequence charSequence : strArr) {
                        String str2 = listItem.title;
                        if (str2 != null ? str2.toLowerCase().contains(charSequence) : false) {
                            arrayList2.add(listItem);
                            break;
                        }
                    }
                }
            }
            updateSearchResults(arrayList2, str);
        }

        private void updateSearchResults(ArrayList<ListItem> arrayList, String str) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$DocumentSelectActivity$SearchAdapter$zsjwT3woo4-0qQZmzshQj5d0bZM(this, str, arrayList));
        }

        public /* synthetic */ void lambda$updateSearchResults$2$DocumentSelectActivity$SearchAdapter(String str, ArrayList arrayList) {
            if (DocumentSelectActivity.this.searching) {
                if (DocumentSelectActivity.this.listView.getAdapter() != DocumentSelectActivity.this.searchAdapter) {
                    DocumentSelectActivity.this.listView.setAdapter(DocumentSelectActivity.this.searchAdapter);
                    DocumentSelectActivity.this.updateEmptyView();
                }
                DocumentSelectActivity.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoFilesFoundInfo", NUM, str)));
            }
            DocumentSelectActivity.this.searchWas = true;
            this.searchResult = arrayList;
            notifyDataSetChanged();
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public ListItem getItem(int i) {
            return i < this.searchResult.size() ? (ListItem) this.searchResult.get(i) : null;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new SharedDocumentCell(this.mContext, true));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ListItem item = getItem(i);
            SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
            int i2 = item.icon;
            if (i2 != 0) {
                sharedDocumentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, null, null, i2, false);
            } else {
                SharedDocumentCell sharedDocumentCell2 = sharedDocumentCell;
                sharedDocumentCell2.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4)), item.thumb, 0, false);
            }
            if (item.file != null) {
                sharedDocumentCell.setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(item.file.toString()), DocumentSelectActivity.this.scrolling ^ 1);
            } else {
                sharedDocumentCell.setChecked(false, DocumentSelectActivity.this.scrolling ^ 1);
            }
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
            FileLog.e(e);
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
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
        String str = "dialogBackground";
        this.actionBar.setBackgroundColor(Theme.getColor(str));
        String str2 = "dialogTextBlack";
        this.actionBar.setTitleColor(Theme.getColor(str2));
        this.actionBar.setItemsColor(Theme.getColor(str2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SelectFile", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (DocumentSelectActivity.this.canClosePicker()) {
                        DocumentSelectActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    SharedConfig.toggleSortFilesByName();
                    DocumentSelectActivity.this.sortByName = SharedConfig.sortFilesByName;
                    DocumentSelectActivity.this.sortRecentItems();
                    DocumentSelectActivity.this.sortFileItems();
                    DocumentSelectActivity.this.listAdapter.notifyDataSetChanged();
                    DocumentSelectActivity.this.sortItem.setIcon(DocumentSelectActivity.this.sortByName ? NUM : NUM);
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.searchItem = createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                DocumentSelectActivity.this.searching = true;
                DocumentSelectActivity.this.sortItem.setVisibility(8);
            }

            public void onSearchCollapse() {
                DocumentSelectActivity.this.searching = false;
                DocumentSelectActivity.this.searchWas = false;
                DocumentSelectActivity.this.sortItem.setVisibility(0);
                if (DocumentSelectActivity.this.listView.getAdapter() != DocumentSelectActivity.this.listAdapter) {
                    DocumentSelectActivity.this.listView.setAdapter(DocumentSelectActivity.this.listAdapter);
                }
                DocumentSelectActivity.this.updateEmptyView();
                DocumentSelectActivity.this.searchAdapter.search(null);
            }

            public void onTextChanged(EditText editText) {
                DocumentSelectActivity.this.searchAdapter.search(editText.getText().toString());
            }
        });
        String str3 = "Search";
        this.searchItem.setSearchFieldHint(LocaleController.getString(str3, NUM));
        this.searchItem.setContentDescription(LocaleController.getString(str3, NUM));
        EditTextBoldCursor searchField = this.searchItem.getSearchField();
        searchField.setTextColor(Theme.getColor(str2));
        searchField.setCursorColor(Theme.getColor(str2));
        searchField.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
        this.sortItem = createMenu.addItem(1, this.sortByName ? NUM : NUM);
        this.sortItem.setContentDescription(LocaleController.getString("AccDescrContactSorting", NUM));
        this.selectedFiles.clear();
        this.sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private int lastItemSize;
            private int lastNotifyWidth;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int i3 = 0;
                if (getKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    if (!(AndroidUtilities.isInMultiwindow || DocumentSelectActivity.this.commentTextView == null || DocumentSelectActivity.this.frameLayout2.getParent() != this)) {
                        size2 -= DocumentSelectActivity.this.commentTextView.getEmojiPadding();
                        i2 = MeasureSpec.makeMeasureSpec(size2, NUM);
                    }
                } else if (DocumentSelectActivity.this.commentTextView != null) {
                    this.ignoreLayout = true;
                    DocumentSelectActivity.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                while (i3 < childCount) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8)) {
                        if (DocumentSelectActivity.this.commentTextView == null || !DocumentSelectActivity.this.commentTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                    i3++;
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:52:0x00fd  */
            /* JADX WARNING: Removed duplicated region for block: B:51:0x00f4  */
            /* JADX WARNING: Removed duplicated region for block: B:43:0x00cd  */
            /* JADX WARNING: Removed duplicated region for block: B:36:0x00b3  */
            /* JADX WARNING: Removed duplicated region for block: B:51:0x00f4  */
            /* JADX WARNING: Removed duplicated region for block: B:52:0x00fd  */
            public void onLayout(boolean r9, int r10, int r11, int r12, int r13) {
                /*
                r8 = this;
                r9 = r8.lastNotifyWidth;
                r12 = r12 - r10;
                if (r9 == r12) goto L_0x0024;
            L_0x0005:
                r8.lastNotifyWidth = r12;
                r9 = org.telegram.ui.DocumentSelectActivity.this;
                r9 = r9.sendPopupWindow;
                if (r9 == 0) goto L_0x0024;
            L_0x000f:
                r9 = org.telegram.ui.DocumentSelectActivity.this;
                r9 = r9.sendPopupWindow;
                r9 = r9.isShowing();
                if (r9 == 0) goto L_0x0024;
            L_0x001b:
                r9 = org.telegram.ui.DocumentSelectActivity.this;
                r9 = r9.sendPopupWindow;
                r9.dismiss();
            L_0x0024:
                r9 = r8.getChildCount();
                r10 = org.telegram.ui.DocumentSelectActivity.this;
                r10 = r10.commentTextView;
                r0 = 0;
                if (r10 == 0) goto L_0x005e;
            L_0x0031:
                r10 = org.telegram.ui.DocumentSelectActivity.this;
                r10 = r10.frameLayout2;
                r10 = r10.getParent();
                if (r10 != r8) goto L_0x005e;
            L_0x003d:
                r10 = r8.getKeyboardHeight();
                r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                if (r10 > r1) goto L_0x005e;
            L_0x0049:
                r10 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
                if (r10 != 0) goto L_0x005e;
            L_0x004d:
                r10 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r10 != 0) goto L_0x005e;
            L_0x0053:
                r10 = org.telegram.ui.DocumentSelectActivity.this;
                r10 = r10.commentTextView;
                r10 = r10.getEmojiPadding();
                goto L_0x005f;
            L_0x005e:
                r10 = 0;
            L_0x005f:
                r8.setBottomClip(r10);
            L_0x0062:
                if (r0 >= r9) goto L_0x0114;
            L_0x0064:
                r1 = r8.getChildAt(r0);
                r2 = r1.getVisibility();
                r3 = 8;
                if (r2 != r3) goto L_0x0072;
            L_0x0070:
                goto L_0x0110;
            L_0x0072:
                r2 = r1.getLayoutParams();
                r2 = (android.widget.FrameLayout.LayoutParams) r2;
                r3 = r1.getMeasuredWidth();
                r4 = r1.getMeasuredHeight();
                r5 = r2.gravity;
                r6 = -1;
                if (r5 != r6) goto L_0x0087;
            L_0x0085:
                r5 = 51;
            L_0x0087:
                r6 = r5 & 7;
                r5 = r5 & 112;
                r6 = r6 & 7;
                r7 = 1;
                if (r6 == r7) goto L_0x00a5;
            L_0x0090:
                r7 = 5;
                if (r6 == r7) goto L_0x009b;
            L_0x0093:
                r6 = r2.leftMargin;
                r7 = r8.getPaddingLeft();
                r6 = r6 + r7;
                goto L_0x00af;
            L_0x009b:
                r6 = r12 - r3;
                r7 = r2.rightMargin;
                r6 = r6 - r7;
                r7 = r8.getPaddingRight();
                goto L_0x00ae;
            L_0x00a5:
                r6 = r12 - r3;
                r6 = r6 / 2;
                r7 = r2.leftMargin;
                r6 = r6 + r7;
                r7 = r2.rightMargin;
            L_0x00ae:
                r6 = r6 - r7;
            L_0x00af:
                r7 = 16;
                if (r5 == r7) goto L_0x00cd;
            L_0x00b3:
                r7 = 48;
                if (r5 == r7) goto L_0x00c5;
            L_0x00b7:
                r7 = 80;
                if (r5 == r7) goto L_0x00be;
            L_0x00bb:
                r2 = r2.topMargin;
                goto L_0x00da;
            L_0x00be:
                r5 = r13 - r10;
                r5 = r5 - r11;
                r5 = r5 - r4;
                r2 = r2.bottomMargin;
                goto L_0x00d8;
            L_0x00c5:
                r2 = r2.topMargin;
                r5 = r8.getPaddingTop();
                r2 = r2 + r5;
                goto L_0x00da;
            L_0x00cd:
                r5 = r13 - r10;
                r5 = r5 - r11;
                r5 = r5 - r4;
                r5 = r5 / 2;
                r7 = r2.topMargin;
                r5 = r5 + r7;
                r2 = r2.bottomMargin;
            L_0x00d8:
                r2 = r5 - r2;
            L_0x00da:
                r5 = org.telegram.ui.DocumentSelectActivity.this;
                r5 = r5.commentTextView;
                if (r5 == 0) goto L_0x010b;
            L_0x00e2:
                r5 = org.telegram.ui.DocumentSelectActivity.this;
                r5 = r5.commentTextView;
                r5 = r5.isPopupView(r1);
                if (r5 == 0) goto L_0x010b;
            L_0x00ee:
                r2 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r2 == 0) goto L_0x00fd;
            L_0x00f4:
                r2 = r8.getMeasuredHeight();
                r5 = r1.getMeasuredHeight();
                goto L_0x010a;
            L_0x00fd:
                r2 = r8.getMeasuredHeight();
                r5 = r8.getKeyboardHeight();
                r2 = r2 + r5;
                r5 = r1.getMeasuredHeight();
            L_0x010a:
                r2 = r2 - r5;
            L_0x010b:
                r3 = r3 + r6;
                r4 = r4 + r2;
                r1.layout(r6, r2, r3, r4);
            L_0x0110:
                r0 = r0 + 1;
                goto L_0x0062;
            L_0x0114:
                r8.notifyHeightChanged();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DocumentSelectActivity$AnonymousClass4.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.sizeNotifierFrameLayout.setBackgroundColor(Theme.getColor(str));
        this.fragmentView = this.sizeNotifierFrameLayout;
        this.emptyView = new LinearLayout(context2);
        this.emptyView.setOrientation(1);
        this.emptyView.setGravity(17);
        this.emptyView.setVisibility(8);
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(-$$Lambda$DocumentSelectActivity$wOAW0__TUXtkjQ-Z0vg8d4FmxpE.INSTANCE);
        this.emptyImageView = new ImageView(context2);
        this.emptyImageView.setImageResource(NUM);
        this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogEmptyImage"), Mode.MULTIPLY));
        this.emptyView.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
        this.emptyTitleTextView = new TextView(context2);
        this.emptyTitleTextView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptyTitleTextView.setGravity(17);
        this.emptyTitleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.emptyTitleTextView.setTextSize(1, 17.0f);
        this.emptyTitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        this.emptyView.addView(this.emptyTitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 11, 0, 0));
        this.emptySubtitleTextView = new TextView(context2);
        this.emptySubtitleTextView.setTextColor(Theme.getColor("dialogEmptyText"));
        this.emptySubtitleTextView.setGravity(17);
        this.emptySubtitleTextView.setTextSize(1, 15.0f);
        this.emptyView.addView(this.emptySubtitleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 6, 0, 0));
        this.listView = new RecyclerListView(context2);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setClipToPadding(false);
        recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.listAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchAdapter = new SearchAdapter(context2);
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                DocumentSelectActivity.this.scrolling = i != 0;
                if (i == 1 && DocumentSelectActivity.this.searching && DocumentSelectActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(DocumentSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.listView.setOnItemClickListener(new -$$Lambda$DocumentSelectActivity$E5YWhXqdE3EAEFs0pq60gNB94D8(this));
        this.listView.setOnItemLongClickListener(new -$$Lambda$DocumentSelectActivity$NbdKSlNvDrFttvexviyyO8O2cvk(this));
        this.shadow = new View(context2);
        this.shadow.setBackgroundResource(NUM);
        this.shadow.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        this.frameLayout2 = new FrameLayout(context2);
        this.frameLayout2.setBackgroundColor(Theme.getColor(str));
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setTranslationY((float) AndroidUtilities.dp(48.0f));
        this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener(-$$Lambda$DocumentSelectActivity$_4RF1KR3EMjps5KjB_LWZWCJnu0.INSTANCE);
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.commentTextView = new EditTextEmoji(context2, this.sizeNotifierFrameLayout, null, 1);
        this.commentTextView.setFilters(new InputFilter[]{new LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
        this.commentTextView.setHint(LocaleController.getString("AddCaption", NUM));
        this.commentTextView.onResume();
        EditTextBoldCursor editText = this.commentTextView.getEditText();
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
        if (this.chatActivity == null) {
            this.commentTextView.setVisibility(8);
        }
        this.writeButtonContainer = new FrameLayout(context2);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", NUM));
        this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
        this.writeButton = new ImageView(context2);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("dialogFloatingButton"), Theme.getColor(VERSION.SDK_INT >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.writeButton.setImageResource(NUM);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), Mode.MULTIPLY));
        this.writeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        this.writeButton.setOnClickListener(new -$$Lambda$DocumentSelectActivity$qhRC1y2bieE94R9c_ZacTxqjRzs(this));
        this.writeButton.setOnLongClickListener(new -$$Lambda$DocumentSelectActivity$wEPxYGnkp_eiNGh_hQ1VsbHolfw(this));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedCountView = new View(context2) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, DocumentSelectActivity.this.selectedFiles.size()))});
                int ceil = (int) Math.ceil((double) DocumentSelectActivity.this.textPaint.measureText(format));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ceil, AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                DocumentSelectActivity.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                DocumentSelectActivity.this.paint.setColor(Theme.getColor("dialogBackground"));
                max /= 2;
                int i = measuredWidth - max;
                max += measuredWidth;
                DocumentSelectActivity.this.rect.set((float) i, 0.0f, (float) max, (float) getMeasuredHeight());
                canvas.drawRoundRect(DocumentSelectActivity.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), DocumentSelectActivity.this.paint);
                DocumentSelectActivity.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                DocumentSelectActivity.this.rect.set((float) (i + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (max - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                canvas.drawRoundRect(DocumentSelectActivity.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), DocumentSelectActivity.this.paint);
                canvas.drawText(format, (float) (measuredWidth - (ceil / 2)), (float) AndroidUtilities.dp(16.2f), DocumentSelectActivity.this.textPaint);
            }
        };
        this.selectedCountView.setAlpha(0.0f);
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
        ListItem item;
        Adapter adapter = this.listView.getAdapter();
        Adapter adapter2 = this.listAdapter;
        if (adapter == adapter2) {
            item = adapter2.getItem(i);
        } else {
            item = this.searchAdapter.getItem(i);
        }
        if (item != null) {
            File file = item.file;
            HistoryEntry historyEntry;
            if (file == null) {
                int i2 = item.icon;
                if (i2 == NUM) {
                    final HashMap hashMap = new HashMap();
                    final ArrayList arrayList = new ArrayList();
                    PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(0, MediaController.allMediaAlbumEntry, hashMap, arrayList, 0, this.chatActivity != null, this.chatActivity);
                    photoPickerActivity.setDocumentsPicker(true);
                    photoPickerActivity.setDelegate(new PhotoPickerActivityDelegate() {
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
                    ArrayList arrayList2 = this.history;
                    historyEntry = (HistoryEntry) arrayList2.remove(arrayList2.size() - 1);
                    this.actionBar.setTitle(historyEntry.title);
                    File file2 = historyEntry.dir;
                    if (file2 != null) {
                        listFiles(file2);
                    } else {
                        listRoots();
                    }
                    updateSearchButton();
                    this.layoutManager.scrollToPositionWithOffset(historyEntry.scrollItem, historyEntry.scrollOffset);
                }
            } else if (file.isDirectory()) {
                historyEntry = new HistoryEntry(this, null);
                historyEntry.scrollItem = this.layoutManager.findLastVisibleItemPosition();
                View findViewByPosition = this.layoutManager.findViewByPosition(historyEntry.scrollItem);
                if (findViewByPosition != null) {
                    historyEntry.scrollOffset = findViewByPosition.getTop();
                }
                historyEntry.dir = this.currentDir;
                historyEntry.title = this.actionBar.getTitle();
                this.history.add(historyEntry);
                if (listFiles(file)) {
                    this.actionBar.setTitle(item.title);
                } else {
                    this.history.remove(historyEntry);
                }
            } else {
                onItemClick(view, item);
            }
        }
    }

    public /* synthetic */ boolean lambda$createView$2$DocumentSelectActivity(View view, int i) {
        ListItem item;
        Adapter adapter = this.listView.getAdapter();
        Adapter adapter2 = this.listAdapter;
        if (adapter == adapter2) {
            item = adapter2.getItem(i);
        } else {
            item = this.searchAdapter.getItem(i);
        }
        return onItemClick(view, item);
    }

    public /* synthetic */ void lambda$createView$4$DocumentSelectActivity(View view) {
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity == null || !chatActivity.isInScheduleMode()) {
            sendSelectedFiles(true, 0);
        } else {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new -$$Lambda$DocumentSelectActivity$gnHA3uCcnNOMtdSeprkWkUaVbNw(this));
        }
    }

    public /* synthetic */ boolean lambda$createView$7$DocumentSelectActivity(View view) {
        View view2 = view;
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity == null) {
            return false;
        }
        chatActivity.getCurrentChat();
        User currentUser = this.chatActivity.getCurrentUser();
        if (this.chatActivity.getCurrentEncryptedChat() != null) {
            return false;
        }
        if (this.sendPopupLayout == null) {
            this.sendPopupLayout = new ActionBarPopupWindowLayout(getParentActivity());
            this.sendPopupLayout.setAnimationEnabled(false);
            this.sendPopupLayout.setOnTouchListener(new OnTouchListener() {
                private Rect popupRect = new Rect();

                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getActionMasked() == 0 && DocumentSelectActivity.this.sendPopupWindow != null && DocumentSelectActivity.this.sendPopupWindow.isShowing()) {
                        view.getHitRect(this.popupRect);
                        if (!this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            DocumentSelectActivity.this.sendPopupWindow.dismiss();
                        }
                    }
                    return false;
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new -$$Lambda$DocumentSelectActivity$jE0Elau-EiwqhRlyNO4AzdibGr8(this));
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
                    this.itemCells[i].setOnClickListener(new -$$Lambda$DocumentSelectActivity$9Q-YHHE1EYjmRFbyY0oGlj5IWMQ(this, i));
                }
            }
            this.sendPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
            this.sendPopupWindow.setAnimationEnabled(false);
            this.sendPopupWindow.setAnimationStyle(NUM);
            this.sendPopupWindow.setOutsideTouchable(true);
            this.sendPopupWindow.setClippingEnabled(true);
            this.sendPopupWindow.setInputMethodMode(2);
            this.sendPopupWindow.setSoftInputMode(0);
            this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        }
        this.sendPopupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] iArr = new int[2];
        view2.getLocationInWindow(iArr);
        this.sendPopupWindow.showAtLocation(view2, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
        this.sendPopupWindow.dimBehind();
        view2.performHapticFeedback(3, 2);
        return false;
    }

    public /* synthetic */ void lambda$null$5$DocumentSelectActivity(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$null$6$DocumentSelectActivity(int i, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new -$$Lambda$DocumentSelectActivity$gnHA3uCcnNOMtdSeprkWkUaVbNw(this));
        } else if (i == 1) {
            sendSelectedFiles(true, 0);
        }
    }

    private boolean onItemClick(View view, ListItem listItem) {
        if (listItem != null) {
            File file = listItem.file;
            if (!(file == null || file.isDirectory())) {
                boolean z;
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
                } else if (this.maxSelectedFiles >= 0 && this.selectedFiles.size() >= this.maxSelectedFiles) {
                    showErrorBox(LocaleController.formatString("PassportUploadMaxReached", NUM, LocaleController.formatPluralString("Files", this.maxSelectedFiles)));
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
        }
        return false;
    }

    public void setChatActivity(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
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
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(z ? Integer.valueOf(1) : null);
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
            frameLayout = this.writeButtonContainer;
            property = View.SCALE_Y;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            frameLayout = this.writeButtonContainer;
            property = View.ALPHA;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            View view = this.selectedCountView;
            property = View.SCALE_X;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
            view = this.selectedCountView;
            property = View.SCALE_Y;
            fArr = new float[1];
            if (z) {
                f2 = 1.0f;
            }
            fArr[0] = f2;
            arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
            View view2 = this.selectedCountView;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!z) {
                f3 = 0.0f;
            }
            fArr2[0] = f3;
            arrayList.add(ObjectAnimator.ofFloat(view2, property2, fArr2));
            FrameLayout frameLayout2 = this.frameLayout2;
            Property property3 = View.TRANSLATION_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 0.0f : (float) AndroidUtilities.dp(48.0f);
            arrayList.add(ObjectAnimator.ofFloat(frameLayout2, property3, fArr3));
            view2 = this.shadow;
            property3 = View.TRANSLATION_Y;
            fArr3 = new float[1];
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            fArr3[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view2, property3, fArr3));
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
                        DocumentSelectActivity.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(DocumentSelectActivity.this.animatorSet)) {
                        DocumentSelectActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
            View view3 = this.selectedCountView;
            if (z) {
                f2 = 1.0f;
            }
            view3.setScaleY(f2);
            view3 = this.selectedCountView;
            if (!z) {
                f3 = 0.0f;
            }
            view3.setAlpha(f3);
            this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
            view3 = this.shadow;
            if (!z) {
                f = (float) AndroidUtilities.dp(48.0f);
            }
            view3.setTranslationY(f);
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
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = i == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        view = this.selectedCountView;
        property = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (i != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view, property, fArr2);
        animatorSet.playTogether(animatorArr);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(180);
        animatorSet.start();
    }

    private void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, boolean z, int i) {
        if (!hashMap.isEmpty() && this.delegate != null && !this.sendPressed) {
            this.sendPressed = true;
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                Object obj = hashMap.get(arrayList.get(i2));
                SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
                arrayList2.add(sendingMediaInfo);
                if (obj instanceof PhotoEntry) {
                    PhotoEntry photoEntry = (PhotoEntry) obj;
                    if (photoEntry.isVideo) {
                        sendingMediaInfo.path = photoEntry.path;
                        sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                    } else {
                        String str = photoEntry.imagePath;
                        if (str != null) {
                            sendingMediaInfo.path = str;
                        } else {
                            str = photoEntry.path;
                            if (str != null) {
                                sendingMediaInfo.path = str;
                            }
                        }
                    }
                    sendingMediaInfo.isVideo = photoEntry.isVideo;
                    CharSequence charSequence = photoEntry.caption;
                    ArrayList arrayList3 = null;
                    sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                    sendingMediaInfo.entities = photoEntry.entities;
                    if (!photoEntry.stickers.isEmpty()) {
                        arrayList3 = new ArrayList(photoEntry.stickers);
                    }
                    sendingMediaInfo.masks = arrayList3;
                    sendingMediaInfo.ttl = photoEntry.ttl;
                }
            }
            this.delegate.didSelectPhotos(arrayList2, z, i);
        }
    }

    private void sendSelectedFiles(boolean z, int i) {
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
                    ListItem listItem = new ListItem(this, null);
                    listItem.title = file.getName();
                    listItem.file = file;
                    String name = file.getName();
                    String[] split = name.split("\\.");
                    listItem.ext = split.length > 1 ? split[split.length - 1] : "?";
                    listItem.subtitle = AndroidUtilities.formatFileSize(file.length());
                    name = name.toLowerCase();
                    if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".jpeg")) {
                        listItem.thumb = file.getAbsolutePath();
                    }
                    this.recentItems.add(listItem);
                }
            }
            sortRecentItems();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void sortRecentItems() {
        Collections.sort(this.recentItems, new -$$Lambda$DocumentSelectActivity$EXTKWJRh5FfXbSgiWT73pCjinPA(this));
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

    private void sortFileItems() {
        if (this.currentDir != null) {
            Collections.sort(this.items, new -$$Lambda$DocumentSelectActivity$pYaVpEB1hFW4rgcN674CNpa50C8(this));
        }
    }

    public /* synthetic */ int lambda$sortFileItems$9$DocumentSelectActivity(ListItem listItem, ListItem listItem2) {
        File file = listItem.file;
        int i = -1;
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
            if (!isDirectory) {
                i = 1;
            }
            return i;
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
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        SearchAdapter searchAdapter = this.searchAdapter;
        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();
        }
    }

    private void updateEmptyView() {
        String str = "NoFilesFound";
        if (this.searching) {
            this.emptyTitleTextView.setText(LocaleController.getString(str, NUM));
            this.emptyView.setGravity(1);
            this.emptyView.setPadding(0, AndroidUtilities.dp(60.0f), 0, 0);
            this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        } else {
            this.emptyTitleTextView.setText(LocaleController.getString(str, NUM));
            this.emptySubtitleTextView.setText(LocaleController.getString("NoFilesInfo", NUM));
            this.emptyView.setGravity(17);
            this.emptyView.setPadding(0, 0, 0, 0);
            this.emptySubtitleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(128.0f));
        }
        this.listView.setEmptyView(this.emptyView);
    }

    private void updateSearchButton() {
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

    private boolean canClosePicker() {
        if (this.history.size() <= 0) {
            return true;
        }
        ArrayList arrayList = this.history;
        HistoryEntry historyEntry = (HistoryEntry) arrayList.remove(arrayList.size() - 1);
        this.actionBar.setTitle(historyEntry.title);
        File file = historyEntry.dir;
        if (file != null) {
            listFiles(file);
        } else {
            listRoots();
        }
        updateSearchButton();
        this.layoutManager.scrollToPositionWithOffset(historyEntry.scrollItem, historyEntry.scrollOffset);
        return false;
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null && editTextEmoji.isPopupShowing()) {
            this.commentTextView.hidePopup(true);
            return false;
        } else if (canClosePicker()) {
            return super.onBackPressed();
        } else {
            return false;
        }
    }

    public void setDelegate(DocumentSelectActivityDelegate documentSelectActivityDelegate) {
        this.delegate = documentSelectActivityDelegate;
    }

    private boolean listFiles(File file) {
        this.hasFiles = false;
        if (file.canRead()) {
            try {
                File[] listFiles = file.listFiles();
                if (listFiles == null) {
                    showErrorBox(LocaleController.getString("UnknownError", NUM));
                    return false;
                }
                String str;
                this.currentDir = file;
                this.items.clear();
                int i = 0;
                while (true) {
                    str = "Folder";
                    if (i >= listFiles.length) {
                        break;
                    }
                    File file2 = listFiles[i];
                    if (file2.getName().indexOf(46) != 0) {
                        ListItem listItem = new ListItem(this, null);
                        listItem.title = file2.getName();
                        listItem.file = file2;
                        if (file2.isDirectory()) {
                            listItem.icon = NUM;
                            listItem.subtitle = LocaleController.getString(str, NUM);
                        } else {
                            this.hasFiles = true;
                            String name = file2.getName();
                            String[] split = name.split("\\.");
                            listItem.ext = split.length > 1 ? split[split.length - 1] : "?";
                            listItem.subtitle = AndroidUtilities.formatFileSize(file2.length());
                            name = name.toLowerCase();
                            if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".jpeg")) {
                                listItem.thumb = file2.getAbsolutePath();
                            }
                        }
                        this.items.add(listItem);
                    }
                    i++;
                }
                ListItem listItem2 = new ListItem(this, null);
                listItem2.title = "..";
                if (this.history.size() > 0) {
                    ArrayList arrayList = this.history;
                    File file3 = ((HistoryEntry) arrayList.get(arrayList.size() - 1)).dir;
                    if (file3 == null) {
                        listItem2.subtitle = LocaleController.getString(str, NUM);
                    } else {
                        listItem2.subtitle = file3.toString();
                    }
                } else {
                    listItem2.subtitle = LocaleController.getString(str, NUM);
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
        } else if ((!file.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString()) && !file.getAbsolutePath().startsWith("/sdcard") && !file.getAbsolutePath().startsWith("/mnt/sdcard")) || Environment.getExternalStorageState().equals("mounted") || Environment.getExternalStorageState().equals("mounted_ro")) {
            showErrorBox(LocaleController.getString("AccessError", NUM));
            return false;
        } else {
            this.currentDir = file;
            this.items.clear();
            Environment.getExternalStorageState();
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
            return true;
        }
    }

    private void showErrorBox(String str) {
        if (getParentActivity() != null) {
            new Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", NUM)).setMessage(str).setPositiveButton(LocaleController.getString("OK", NUM), null).show();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x0143 A:{Catch:{ Exception -> 0x0164 }} */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x013c A:{Catch:{ Exception -> 0x0164 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01b7 A:{Catch:{ Exception -> 0x01d3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0202  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0232  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x017b A:{SYNTHETIC, Splitter:B:66:0x017b} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01b7 A:{Catch:{ Exception -> 0x01d3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0202  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0232  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0244 A:{SYNTHETIC, Splitter:B:87:0x0244} */
    @android.annotation.SuppressLint({"NewApi"})
    private void listRoots() {
        /*
        r13 = this;
        r0 = "Telegram";
        r1 = 0;
        r13.currentDir = r1;
        r2 = 0;
        r13.hasFiles = r2;
        r2 = r13.items;
        r2.clear();
        r2 = new java.util.HashSet;
        r2.<init>();
        r3 = android.os.Environment.getExternalStorageDirectory();
        r3 = r3.getPath();
        android.os.Environment.isExternalStorageRemovable();
        r4 = android.os.Environment.getExternalStorageState();
        r5 = "mounted";
        r5 = r4.equals(r5);
        r6 = NUM; // 0x7f0e04a5 float:1.887745E38 double:1.053162744E-314;
        r7 = "ExternalFolderInfo";
        r8 = NUM; // 0x7var_bf float:1.7944965E38 double:1.0529355974E-314;
        r9 = NUM; // 0x7f0e09a5 float:1.8880045E38 double:1.0531633765E-314;
        r10 = "SdCard";
        if (r5 != 0) goto L_0x003e;
    L_0x0036:
        r5 = "mounted_ro";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0081;
    L_0x003e:
        r4 = new org.telegram.ui.DocumentSelectActivity$ListItem;
        r4.<init>(r13, r1);
        r5 = android.os.Environment.isExternalStorageRemovable();
        if (r5 == 0) goto L_0x0058;
    L_0x0049:
        r5 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r4.title = r5;
        r4.icon = r8;
        r5 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r4.subtitle = r5;
        goto L_0x0073;
    L_0x0058:
        r5 = NUM; // 0x7f0e0575 float:1.887787E38 double:1.053162847E-314;
        r11 = "InternalStorage";
        r5 = org.telegram.messenger.LocaleController.getString(r11, r5);
        r4.title = r5;
        r5 = NUM; // 0x7var_c1 float:1.794497E38 double:1.0529355984E-314;
        r4.icon = r5;
        r5 = NUM; // 0x7f0e0574 float:1.8877869E38 double:1.0531628463E-314;
        r11 = "InternalFolderInfo";
        r5 = org.telegram.messenger.LocaleController.getString(r11, r5);
        r4.subtitle = r5;
    L_0x0073:
        r5 = android.os.Environment.getExternalStorageDirectory();
        r4.file = r5;
        r5 = r13.items;
        r5.add(r4);
        r2.add(r3);
    L_0x0081:
        r3 = new java.io.BufferedReader;	 Catch:{ Exception -> 0x0174, all -> 0x0170 }
        r4 = new java.io.FileReader;	 Catch:{ Exception -> 0x0174, all -> 0x0170 }
        r5 = "/proc/mounts";
        r4.<init>(r5);	 Catch:{ Exception -> 0x0174, all -> 0x0170 }
        r3.<init>(r4);	 Catch:{ Exception -> 0x0174, all -> 0x0170 }
    L_0x008d:
        r4 = r3.readLine();	 Catch:{ Exception -> 0x016e }
        if (r4 == 0) goto L_0x016a;
    L_0x0093:
        r5 = "vfat";
        r5 = r4.contains(r5);	 Catch:{ Exception -> 0x016e }
        if (r5 != 0) goto L_0x00a4;
    L_0x009c:
        r5 = "/mnt";
        r5 = r4.contains(r5);	 Catch:{ Exception -> 0x016e }
        if (r5 == 0) goto L_0x008d;
    L_0x00a4:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x016e }
        if (r5 == 0) goto L_0x00ab;
    L_0x00a8:
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x016e }
    L_0x00ab:
        r5 = new java.util.StringTokenizer;	 Catch:{ Exception -> 0x016e }
        r11 = " ";
        r5.<init>(r4, r11);	 Catch:{ Exception -> 0x016e }
        r5.nextToken();	 Catch:{ Exception -> 0x016e }
        r5 = r5.nextToken();	 Catch:{ Exception -> 0x016e }
        r11 = r2.contains(r5);	 Catch:{ Exception -> 0x016e }
        if (r11 == 0) goto L_0x00c0;
    L_0x00bf:
        goto L_0x008d;
    L_0x00c0:
        r11 = "/dev/block/vold";
        r11 = r4.contains(r11);	 Catch:{ Exception -> 0x016e }
        if (r11 == 0) goto L_0x008d;
    L_0x00c8:
        r11 = "/mnt/secure";
        r11 = r4.contains(r11);	 Catch:{ Exception -> 0x016e }
        if (r11 != 0) goto L_0x008d;
    L_0x00d0:
        r11 = "/mnt/asec";
        r11 = r4.contains(r11);	 Catch:{ Exception -> 0x016e }
        if (r11 != 0) goto L_0x008d;
    L_0x00d8:
        r11 = "/mnt/obb";
        r11 = r4.contains(r11);	 Catch:{ Exception -> 0x016e }
        if (r11 != 0) goto L_0x008d;
    L_0x00e0:
        r11 = "/dev/mapper";
        r11 = r4.contains(r11);	 Catch:{ Exception -> 0x016e }
        if (r11 != 0) goto L_0x008d;
    L_0x00e8:
        r11 = "tmpfs";
        r4 = r4.contains(r11);	 Catch:{ Exception -> 0x016e }
        if (r4 != 0) goto L_0x008d;
    L_0x00f0:
        r4 = new java.io.File;	 Catch:{ Exception -> 0x016e }
        r4.<init>(r5);	 Catch:{ Exception -> 0x016e }
        r4 = r4.isDirectory();	 Catch:{ Exception -> 0x016e }
        if (r4 != 0) goto L_0x0127;
    L_0x00fb:
        r4 = 47;
        r4 = r5.lastIndexOf(r4);	 Catch:{ Exception -> 0x016e }
        r11 = -1;
        if (r4 == r11) goto L_0x0127;
    L_0x0104:
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x016e }
        r11.<init>();	 Catch:{ Exception -> 0x016e }
        r12 = "/storage/";
        r11.append(r12);	 Catch:{ Exception -> 0x016e }
        r4 = r4 + 1;
        r4 = r5.substring(r4);	 Catch:{ Exception -> 0x016e }
        r11.append(r4);	 Catch:{ Exception -> 0x016e }
        r4 = r11.toString();	 Catch:{ Exception -> 0x016e }
        r11 = new java.io.File;	 Catch:{ Exception -> 0x016e }
        r11.<init>(r4);	 Catch:{ Exception -> 0x016e }
        r11 = r11.isDirectory();	 Catch:{ Exception -> 0x016e }
        if (r11 == 0) goto L_0x0127;
    L_0x0126:
        goto L_0x0128;
    L_0x0127:
        r4 = r5;
    L_0x0128:
        r2.add(r4);	 Catch:{ Exception -> 0x016e }
        r5 = new org.telegram.ui.DocumentSelectActivity$ListItem;	 Catch:{ Exception -> 0x0164 }
        r5.<init>(r13, r1);	 Catch:{ Exception -> 0x0164 }
        r11 = r4.toLowerCase();	 Catch:{ Exception -> 0x0164 }
        r12 = "sd";
        r11 = r11.contains(r12);	 Catch:{ Exception -> 0x0164 }
        if (r11 == 0) goto L_0x0143;
    L_0x013c:
        r11 = org.telegram.messenger.LocaleController.getString(r10, r9);	 Catch:{ Exception -> 0x0164 }
        r5.title = r11;	 Catch:{ Exception -> 0x0164 }
        goto L_0x014e;
    L_0x0143:
        r11 = "ExternalStorage";
        r12 = NUM; // 0x7f0e04a6 float:1.8877451E38 double:1.0531627446E-314;
        r11 = org.telegram.messenger.LocaleController.getString(r11, r12);	 Catch:{ Exception -> 0x0164 }
        r5.title = r11;	 Catch:{ Exception -> 0x0164 }
    L_0x014e:
        r11 = org.telegram.messenger.LocaleController.getString(r7, r6);	 Catch:{ Exception -> 0x0164 }
        r5.subtitle = r11;	 Catch:{ Exception -> 0x0164 }
        r5.icon = r8;	 Catch:{ Exception -> 0x0164 }
        r11 = new java.io.File;	 Catch:{ Exception -> 0x0164 }
        r11.<init>(r4);	 Catch:{ Exception -> 0x0164 }
        r5.file = r11;	 Catch:{ Exception -> 0x0164 }
        r4 = r13.items;	 Catch:{ Exception -> 0x0164 }
        r4.add(r5);	 Catch:{ Exception -> 0x0164 }
        goto L_0x008d;
    L_0x0164:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);	 Catch:{ Exception -> 0x016e }
        goto L_0x008d;
    L_0x016a:
        r3.close();	 Catch:{ Exception -> 0x017f }
        goto L_0x0183;
    L_0x016e:
        r2 = move-exception;
        goto L_0x0176;
    L_0x0170:
        r0 = move-exception;
        r3 = r1;
        goto L_0x0242;
    L_0x0174:
        r2 = move-exception;
        r3 = r1;
    L_0x0176:
        org.telegram.messenger.FileLog.e(r2);	 Catch:{ all -> 0x0241 }
        if (r3 == 0) goto L_0x0183;
    L_0x017b:
        r3.close();	 Catch:{ Exception -> 0x017f }
        goto L_0x0183;
    L_0x017f:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0183:
        r2 = new org.telegram.ui.DocumentSelectActivity$ListItem;
        r2.<init>(r13, r1);
        r3 = "/";
        r2.title = r3;
        r4 = NUM; // 0x7f0e0aaf float:1.8880585E38 double:1.053163508E-314;
        r5 = "SystemRoot";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r2.subtitle = r4;
        r4 = NUM; // 0x7var_bd float:1.7944961E38 double:1.0529355964E-314;
        r2.icon = r4;
        r5 = new java.io.File;
        r5.<init>(r3);
        r2.file = r5;
        r3 = r13.items;
        r3.add(r2);
        r2 = new java.io.File;	 Catch:{ Exception -> 0x01d3 }
        r3 = android.os.Environment.getExternalStorageDirectory();	 Catch:{ Exception -> 0x01d3 }
        r2.<init>(r3, r0);	 Catch:{ Exception -> 0x01d3 }
        r3 = r2.exists();	 Catch:{ Exception -> 0x01d3 }
        if (r3 == 0) goto L_0x01d7;
    L_0x01b7:
        r3 = new org.telegram.ui.DocumentSelectActivity$ListItem;	 Catch:{ Exception -> 0x01d3 }
        r3.<init>(r13, r1);	 Catch:{ Exception -> 0x01d3 }
        r3.title = r0;	 Catch:{ Exception -> 0x01d3 }
        r0 = "AppFolderInfo";
        r5 = NUM; // 0x7f0e00f7 float:1.8875539E38 double:1.0531622787E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r5);	 Catch:{ Exception -> 0x01d3 }
        r3.subtitle = r0;	 Catch:{ Exception -> 0x01d3 }
        r3.icon = r4;	 Catch:{ Exception -> 0x01d3 }
        r3.file = r2;	 Catch:{ Exception -> 0x01d3 }
        r0 = r13.items;	 Catch:{ Exception -> 0x01d3 }
        r0.add(r3);	 Catch:{ Exception -> 0x01d3 }
        goto L_0x01d7;
    L_0x01d3:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x01d7:
        r0 = new org.telegram.ui.DocumentSelectActivity$ListItem;
        r0.<init>(r13, r1);
        r2 = NUM; // 0x7f0e0515 float:1.8877676E38 double:1.0531627994E-314;
        r3 = "Gallery";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.title = r2;
        r2 = NUM; // 0x7f0e0516 float:1.8877678E38 double:1.0531628E-314;
        r3 = "GalleryInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.subtitle = r2;
        r2 = NUM; // 0x7var_be float:1.7944963E38 double:1.052935597E-314;
        r0.icon = r2;
        r0.file = r1;
        r2 = r13.items;
        r2.add(r0);
        r0 = r13.allowMusic;
        if (r0 == 0) goto L_0x0229;
    L_0x0202:
        r0 = new org.telegram.ui.DocumentSelectActivity$ListItem;
        r0.<init>(r13, r1);
        r2 = NUM; // 0x7f0e0159 float:1.8875737E38 double:1.053162327E-314;
        r3 = "AttachMusic";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.title = r2;
        r2 = NUM; // 0x7f0e0667 float:1.8878362E38 double:1.0531629664E-314;
        r3 = "MusicInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r0.subtitle = r2;
        r2 = NUM; // 0x7var_c0 float:1.7944967E38 double:1.052935598E-314;
        r0.icon = r2;
        r0.file = r1;
        r1 = r13.items;
        r1.add(r0);
    L_0x0229:
        r0 = r13.recentItems;
        r0 = r0.isEmpty();
        r1 = 1;
        if (r0 != 0) goto L_0x0234;
    L_0x0232:
        r13.hasFiles = r1;
    L_0x0234:
        r0 = r13.listView;
        org.telegram.messenger.AndroidUtilities.clearDrawableAnimation(r0);
        r13.scrolling = r1;
        r0 = r13.listAdapter;
        r0.notifyDataSetChanged();
        return;
    L_0x0241:
        r0 = move-exception;
    L_0x0242:
        if (r3 == 0) goto L_0x024c;
    L_0x0244:
        r3.close();	 Catch:{ Exception -> 0x0248 }
        goto L_0x024c;
    L_0x0248:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
    L_0x024c:
        goto L_0x024e;
    L_0x024d:
        throw r0;
    L_0x024e:
        goto L_0x024d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DocumentSelectActivity.listRoots():void");
    }

    private String getRootSubtitle(String str) {
        try {
            StatFs statFs = new StatFs(str);
            long availableBlocks = ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
            if (((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize()) == 0) {
                return "";
            }
            return LocaleController.formatString("FreeOfTotal", NUM, AndroidUtilities.formatFileSize(availableBlocks), AndroidUtilities.formatFileSize(((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize())));
        } catch (Exception e) {
            FileLog.e(e);
            return str;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[26];
        r1[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        r1[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r1[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        View view = this.listView;
        int i = ThemeDescription.FLAG_CHECKBOX;
        Class[] clsArr = new Class[]{SharedDocumentCell.class};
        String[] strArr = new String[1];
        strArr[0] = "checkBox";
        r1[18] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "checkbox");
        r1[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, "checkboxCheck");
        view = this.listView;
        i = ThemeDescription.FLAG_IMAGECOLOR;
        clsArr = new Class[]{SharedDocumentCell.class};
        strArr = new String[1];
        strArr[0] = "thumbImageView";
        r1[20] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "files_folderIcon");
        r1[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, null, null, null, "files_folderIconBackground");
        r1[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, null, null, null, "files_iconText");
        r1[23] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "dialogFloatingIcon");
        r1[24] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "dialogFloatingButton");
        r1[25] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, VERSION.SDK_INT >= 21 ? "dialogFloatingButtonPressed" : "dialogFloatingButton");
        return r1;
    }
}
