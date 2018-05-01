package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.StatFs;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DocumentSelectActivity extends BaseFragment {
    private static final int done = 3;
    private ArrayList<View> actionModeViews = new ArrayList();
    private File currentDir;
    private DocumentSelectActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private ArrayList<HistoryEntry> history = new ArrayList();
    private ArrayList<ListItem> items = new ArrayList();
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private BroadcastReceiver receiver = new C13871();
    private boolean receiverRegistered = false;
    private ArrayList<ListItem> recentItems = new ArrayList();
    private boolean scrolling;
    private HashMap<String, ListItem> selectedFiles = new HashMap();
    private NumberTextView selectedMessagesCountTextView;
    private long sizeLimit = NUM;

    /* renamed from: org.telegram.ui.DocumentSelectActivity$1 */
    class C13871 extends BroadcastReceiver {

        /* renamed from: org.telegram.ui.DocumentSelectActivity$1$1 */
        class C13861 implements Runnable {
            C13861() {
            }

            public void run() {
                try {
                    if (DocumentSelectActivity.this.currentDir == null) {
                        DocumentSelectActivity.this.listRoots();
                    } else {
                        DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }

        C13871() {
        }

        public void onReceive(Context context, Intent intent) {
            context = new C13861();
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction()) != null) {
                DocumentSelectActivity.this.listView.postDelayed(context, 1000);
            } else {
                context.run();
            }
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$3 */
    class C13883 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C13883() {
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$7 */
    class C13897 implements Comparator<ListItem> {
        C13897() {
        }

        public int compare(ListItem listItem, ListItem listItem2) {
            long lastModified = listItem.file.lastModified();
            listItem = listItem2.file.lastModified();
            if (lastModified == listItem) {
                return null;
            }
            return lastModified > listItem ? -1 : 1;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$8 */
    class C13908 implements OnPreDrawListener {
        C13908() {
        }

        public boolean onPreDraw() {
            DocumentSelectActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            DocumentSelectActivity.this.fixLayoutInternal();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$9 */
    class C13919 implements Comparator<File> {
        C13919() {
        }

        public int compare(File file, File file2) {
            if (file.isDirectory() == file2.isDirectory()) {
                return file.getName().compareToIgnoreCase(file2.getName());
            }
            return file.isDirectory() != null ? -1 : true;
        }
    }

    public interface DocumentSelectActivityDelegate {
        void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList);

        void startDocumentSelectActivity();
    }

    private class HistoryEntry {
        File dir;
        int scrollItem;
        int scrollOffset;
        String title;

        private HistoryEntry() {
        }
    }

    private class ListItem {
        long date;
        String ext;
        File file;
        int icon;
        String subtitle;
        String thumb;
        String title;

        private ListItem() {
            this.subtitle = TtmlNode.ANONYMOUS_REGION_ID;
            this.ext = TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$2 */
    class C21322 extends ActionBarMenuOnItemClick {
        C21322() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                if (DocumentSelectActivity.this.actionBar.isActionModeShowed() != 0) {
                    DocumentSelectActivity.this.selectedFiles.clear();
                    DocumentSelectActivity.this.actionBar.hideActionMode();
                    i = DocumentSelectActivity.this.listView.getChildCount();
                    for (int i2 = 0; i2 < i; i2++) {
                        View childAt = DocumentSelectActivity.this.listView.getChildAt(i2);
                        if (childAt instanceof SharedDocumentCell) {
                            ((SharedDocumentCell) childAt).setChecked(false, true);
                        }
                    }
                    return;
                }
                DocumentSelectActivity.this.finishFragment();
            } else if (i == 3 && DocumentSelectActivity.this.delegate != 0) {
                i = new ArrayList();
                i.addAll(DocumentSelectActivity.this.selectedFiles.keySet());
                DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, i);
                for (ListItem listItem : DocumentSelectActivity.this.selectedFiles.values()) {
                    listItem.date = System.currentTimeMillis();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$4 */
    class C21334 extends OnScrollListener {
        C21334() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            DocumentSelectActivity.this.scrolling = i != 0 ? 1 : 0;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$5 */
    class C21345 implements OnItemLongClickListener {
        C21345() {
        }

        public boolean onItemClick(View view, int i) {
            if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                return false;
            }
            i = DocumentSelectActivity.this.listAdapter.getItem(i);
            if (i == 0) {
                return false;
            }
            File file = i.file;
            if (!(file == null || file.isDirectory())) {
                if (!file.canRead()) {
                    DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", C0446R.string.AccessError));
                    return false;
                } else if (DocumentSelectActivity.this.sizeLimit != 0 && file.length() > DocumentSelectActivity.this.sizeLimit) {
                    DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", C0446R.string.FileUploadLimit, AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit)));
                    return false;
                } else if (file.length() == 0) {
                    return false;
                } else {
                    DocumentSelectActivity.this.selectedFiles.put(file.toString(), i);
                    DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(1, false);
                    i = new AnimatorSet();
                    Collection arrayList = new ArrayList();
                    for (int i2 = 0; i2 < DocumentSelectActivity.this.actionModeViews.size(); i2++) {
                        View view2 = (View) DocumentSelectActivity.this.actionModeViews.get(i2);
                        AndroidUtilities.clearDrawableAnimation(view2);
                        arrayList.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, 1.0f}));
                    }
                    i.playTogether(arrayList);
                    i.setDuration(250);
                    i.start();
                    DocumentSelectActivity.this.scrolling = false;
                    if ((view instanceof SharedDocumentCell) != 0) {
                        ((SharedDocumentCell) view).setChecked(true, true);
                    }
                    DocumentSelectActivity.this.actionBar.showActionMode();
                }
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$6 */
    class C21356 implements OnItemClickListener {
        C21356() {
        }

        public void onItemClick(View view, int i) {
            i = DocumentSelectActivity.this.listAdapter.getItem(i);
            if (i != 0) {
                File file = i.file;
                if (file == null) {
                    if (i.icon == C0446R.drawable.ic_storage_gallery) {
                        if (DocumentSelectActivity.this.delegate != null) {
                            DocumentSelectActivity.this.delegate.startDocumentSelectActivity();
                        }
                        DocumentSelectActivity.this.finishFragment(false);
                    } else {
                        HistoryEntry historyEntry = (HistoryEntry) DocumentSelectActivity.this.history.remove(DocumentSelectActivity.this.history.size() - 1);
                        DocumentSelectActivity.this.actionBar.setTitle(historyEntry.title);
                        if (historyEntry.dir != 0) {
                            DocumentSelectActivity.this.listFiles(historyEntry.dir);
                        } else {
                            DocumentSelectActivity.this.listRoots();
                        }
                        DocumentSelectActivity.this.layoutManager.scrollToPositionWithOffset(historyEntry.scrollItem, historyEntry.scrollOffset);
                    }
                } else if (file.isDirectory()) {
                    view = new HistoryEntry();
                    view.scrollItem = DocumentSelectActivity.this.layoutManager.findLastVisibleItemPosition();
                    View findViewByPosition = DocumentSelectActivity.this.layoutManager.findViewByPosition(view.scrollItem);
                    if (findViewByPosition != null) {
                        view.scrollOffset = findViewByPosition.getTop();
                    }
                    view.dir = DocumentSelectActivity.this.currentDir;
                    view.title = DocumentSelectActivity.this.actionBar.getTitle();
                    DocumentSelectActivity.this.history.add(view);
                    if (DocumentSelectActivity.this.listFiles(file)) {
                        DocumentSelectActivity.this.actionBar.setTitle(i.title);
                    } else {
                        DocumentSelectActivity.this.history.remove(view);
                    }
                } else {
                    if (!file.canRead()) {
                        DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", C0446R.string.AccessError));
                        file = new File("/mnt/sdcard");
                    }
                    if (DocumentSelectActivity.this.sizeLimit != 0 && file.length() > DocumentSelectActivity.this.sizeLimit) {
                        DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", C0446R.string.FileUploadLimit, AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit)));
                    } else if (file.length() != 0) {
                        if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                            if (DocumentSelectActivity.this.selectedFiles.containsKey(file.toString())) {
                                DocumentSelectActivity.this.selectedFiles.remove(file.toString());
                            } else {
                                DocumentSelectActivity.this.selectedFiles.put(file.toString(), i);
                            }
                            if (DocumentSelectActivity.this.selectedFiles.isEmpty()) {
                                DocumentSelectActivity.this.actionBar.hideActionMode();
                            } else {
                                DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(DocumentSelectActivity.this.selectedFiles.size(), true);
                            }
                            DocumentSelectActivity.this.scrolling = false;
                            if (view instanceof SharedDocumentCell) {
                                ((SharedDocumentCell) view).setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(i.file.toString()), true);
                            }
                        } else if (DocumentSelectActivity.this.delegate != null) {
                            view = new ArrayList();
                            view.add(file.getAbsolutePath());
                            DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, view);
                        }
                    }
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != null ? true : null;
        }

        public int getItemCount() {
            int size = DocumentSelectActivity.this.items.size();
            return (!DocumentSelectActivity.this.history.isEmpty() || DocumentSelectActivity.this.recentItems.isEmpty()) ? size : size + (DocumentSelectActivity.this.recentItems.size() + 1);
        }

        public ListItem getItem(int i) {
            if (i < DocumentSelectActivity.this.items.size()) {
                return (ListItem) DocumentSelectActivity.this.items.get(i);
            }
            if (!(!DocumentSelectActivity.this.history.isEmpty() || DocumentSelectActivity.this.recentItems.isEmpty() || i == DocumentSelectActivity.this.items.size())) {
                i -= DocumentSelectActivity.this.items.size() + 1;
                if (i < DocumentSelectActivity.this.recentItems.size()) {
                    return (ListItem) DocumentSelectActivity.this.recentItems.get(i);
                }
            }
            return 0;
        }

        public int getItemViewType(int i) {
            return getItem(i) != 0 ? 1 : 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new SharedDocumentCell(this.mContext);
            } else {
                viewGroup = new GraySectionCell(this.mContext);
                ((GraySectionCell) viewGroup).setText(LocaleController.getString("Recent", C0446R.string.Recent).toUpperCase());
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 1) {
                i = getItem(i);
                SharedDocumentCell sharedDocumentCell = (SharedDocumentCell) viewHolder.itemView;
                if (i.icon != 0) {
                    sharedDocumentCell.setTextAndValueAndTypeAndThumb(i.title, i.subtitle, null, null, i.icon);
                } else {
                    SharedDocumentCell sharedDocumentCell2 = sharedDocumentCell;
                    sharedDocumentCell2.setTextAndValueAndTypeAndThumb(i.title, i.subtitle, i.ext.toUpperCase().substring(0, Math.min(i.ext.length(), 4)), i.thumb, 0);
                }
                if (i.file == null || !DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                    sharedDocumentCell.setChecked(false, DocumentSelectActivity.this.scrolling ^ 1);
                } else {
                    sharedDocumentCell.setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(i.file.toString()), DocumentSelectActivity.this.scrolling ^ true);
                }
            }
        }
    }

    public boolean onFragmentCreate() {
        loadRecentFiles();
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        try {
            if (this.receiverRegistered) {
                ApplicationLoader.applicationContext.unregisterReceiver(this.receiver);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
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
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SelectFile", C0446R.string.SelectFile));
        this.actionBar.setActionBarMenuOnItemClick(new C21322());
        this.selectedFiles.clear();
        this.actionModeViews.clear();
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(createActionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        this.selectedMessagesCountTextView.setOnTouchListener(new C13883());
        createActionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        this.actionModeViews.add(createActionMode.addItemWithWidth(3, C0446R.drawable.ic_ab_done, AndroidUtilities.dp(54.0f)));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnScrollListener(new C21334());
        this.listView.setOnItemLongClickListener(new C21345());
        this.listView.setOnItemClickListener(new C21356());
        listRoots();
        return this.fragmentView;
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
                    name = name.toLowerCase();
                    if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".jpeg")) {
                        listItem.thumb = file.getAbsolutePath();
                    }
                    this.recentItems.add(listItem);
                }
            }
            Collections.sort(this.recentItems, new C13897());
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        fixLayoutInternal();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C13908());
        }
    }

    private void fixLayoutInternal() {
        if (this.selectedMessagesCountTextView != null) {
            if (AndroidUtilities.isTablet() || ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2) {
                this.selectedMessagesCountTextView.setTextSize(20);
            } else {
                this.selectedMessagesCountTextView.setTextSize(18);
            }
        }
    }

    public boolean onBackPressed() {
        if (this.history.size() <= 0) {
            return super.onBackPressed();
        }
        HistoryEntry historyEntry = (HistoryEntry) this.history.remove(this.history.size() - 1);
        this.actionBar.setTitle(historyEntry.title);
        if (historyEntry.dir != null) {
            listFiles(historyEntry.dir);
        } else {
            listRoots();
        }
        this.layoutManager.scrollToPositionWithOffset(historyEntry.scrollItem, historyEntry.scrollOffset);
        return false;
    }

    public void setDelegate(DocumentSelectActivityDelegate documentSelectActivityDelegate) {
        this.delegate = documentSelectActivityDelegate;
    }

    private boolean listFiles(File file) {
        if (file.canRead()) {
            try {
                File[] listFiles = file.listFiles();
                if (listFiles == null) {
                    showErrorBox(LocaleController.getString("UnknownError", C0446R.string.UnknownError));
                    return false;
                }
                this.currentDir = file;
                this.items.clear();
                Arrays.sort(listFiles, new C13919());
                for (File file2 : listFiles) {
                    if (file2.getName().indexOf(46) != 0) {
                        ListItem listItem = new ListItem();
                        listItem.title = file2.getName();
                        listItem.file = file2;
                        if (file2.isDirectory()) {
                            listItem.icon = C0446R.drawable.ic_directory;
                            listItem.subtitle = LocaleController.getString("Folder", C0446R.string.Folder);
                        } else {
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
                }
                file = new ListItem();
                file.title = "..";
                if (this.history.size() > 0) {
                    HistoryEntry historyEntry = (HistoryEntry) this.history.get(this.history.size() - 1);
                    if (historyEntry.dir == null) {
                        file.subtitle = LocaleController.getString("Folder", C0446R.string.Folder);
                    } else {
                        file.subtitle = historyEntry.dir.toString();
                    }
                } else {
                    file.subtitle = LocaleController.getString("Folder", C0446R.string.Folder);
                }
                file.icon = C0446R.drawable.ic_directory;
                file.file = null;
                this.items.add(0, file);
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                this.listAdapter.notifyDataSetChanged();
                return true;
            } catch (File file3) {
                showErrorBox(file3.getLocalizedMessage());
                return false;
            }
        } else if ((!file3.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString()) && !file3.getAbsolutePath().startsWith("/sdcard") && !file3.getAbsolutePath().startsWith("/mnt/sdcard")) || Environment.getExternalStorageState().equals("mounted") || Environment.getExternalStorageState().equals("mounted_ro")) {
            showErrorBox(LocaleController.getString("AccessError", C0446R.string.AccessError));
            return false;
        } else {
            this.currentDir = file3;
            this.items.clear();
            if ("shared".equals(Environment.getExternalStorageState()) != null) {
                this.emptyView.setText(LocaleController.getString("UsbActive", C0446R.string.UsbActive));
            } else {
                this.emptyView.setText(LocaleController.getString("NotMounted", C0446R.string.NotMounted));
            }
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
            return true;
        }
    }

    private void showErrorBox(String str) {
        if (getParentActivity() != null) {
            new Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", C0446R.string.AppName)).setMessage(str).setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null).show();
        }
    }

    @SuppressLint({"NewApi"})
    private void listRoots() {
        ListItem listItem;
        Throwable e;
        ListItem listItem2;
        File file;
        Throwable th;
        this.currentDir = null;
        this.items.clear();
        HashSet hashSet = new HashSet();
        String path = Environment.getExternalStorageDirectory().getPath();
        Environment.isExternalStorageRemovable();
        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals("mounted") || externalStorageState.equals("mounted_ro")) {
            listItem = new ListItem();
            if (Environment.isExternalStorageRemovable()) {
                listItem.title = LocaleController.getString("SdCard", C0446R.string.SdCard);
                listItem.icon = C0446R.drawable.ic_external_storage;
            } else {
                listItem.title = LocaleController.getString("InternalStorage", C0446R.string.InternalStorage);
                listItem.icon = C0446R.drawable.ic_storage;
            }
            listItem.subtitle = getRootSubtitle(path);
            listItem.file = Environment.getExternalStorageDirectory();
            this.items.add(listItem);
            hashSet.add(path);
        }
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/mounts"));
            while (true) {
                externalStorageState = bufferedReader.readLine();
                if (externalStorageState == null) {
                    break;
                } else if (externalStorageState.contains("vfat") || externalStorageState.contains("/mnt")) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d(externalStorageState);
                    }
                    StringTokenizer stringTokenizer = new StringTokenizer(externalStorageState, " ");
                    stringTokenizer.nextToken();
                    String nextToken = stringTokenizer.nextToken();
                    if (!hashSet.contains(nextToken)) {
                        if (!(!externalStorageState.contains("/dev/block/vold") || externalStorageState.contains("/mnt/secure") || externalStorageState.contains("/mnt/asec") || externalStorageState.contains("/mnt/obb") || externalStorageState.contains("/dev/mapper") || externalStorageState.contains("tmpfs"))) {
                            ListItem listItem3;
                            if (!new File(nextToken).isDirectory()) {
                                int lastIndexOf = nextToken.lastIndexOf(47);
                                if (lastIndexOf != -1) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("/storage/");
                                    stringBuilder.append(nextToken.substring(lastIndexOf + 1));
                                    externalStorageState = stringBuilder.toString();
                                    if (new File(externalStorageState).isDirectory()) {
                                        hashSet.add(externalStorageState);
                                        listItem3 = new ListItem();
                                        if (externalStorageState.toLowerCase().contains("sd")) {
                                            listItem3.title = LocaleController.getString("ExternalStorage", C0446R.string.ExternalStorage);
                                        } else {
                                            listItem3.title = LocaleController.getString("SdCard", C0446R.string.SdCard);
                                        }
                                        listItem3.icon = C0446R.drawable.ic_external_storage;
                                        listItem3.subtitle = getRootSubtitle(externalStorageState);
                                        listItem3.file = new File(externalStorageState);
                                        this.items.add(listItem3);
                                    }
                                }
                            }
                            externalStorageState = nextToken;
                            hashSet.add(externalStorageState);
                            try {
                                listItem3 = new ListItem();
                                if (externalStorageState.toLowerCase().contains("sd")) {
                                    listItem3.title = LocaleController.getString("ExternalStorage", C0446R.string.ExternalStorage);
                                } else {
                                    listItem3.title = LocaleController.getString("SdCard", C0446R.string.SdCard);
                                }
                                listItem3.icon = C0446R.drawable.ic_external_storage;
                                listItem3.subtitle = getRootSubtitle(externalStorageState);
                                listItem3.file = new File(externalStorageState);
                                this.items.add(listItem3);
                            } catch (Throwable e2) {
                                try {
                                    FileLog.m3e(e2);
                                } catch (Exception e3) {
                                    e = e3;
                                }
                            }
                        }
                    }
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Throwable e4) {
                    FileLog.m3e(e4);
                }
            }
        } catch (Exception e5) {
            e4 = e5;
            bufferedReader = null;
            try {
                FileLog.m3e(e4);
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                listItem2 = new ListItem();
                listItem2.title = "/";
                listItem2.subtitle = LocaleController.getString("SystemRoot", C0446R.string.SystemRoot);
                listItem2.icon = C0446R.drawable.ic_directory;
                listItem2.file = new File("/");
                this.items.add(listItem2);
                file = new File(Environment.getExternalStorageDirectory(), "Telegram");
                if (file.exists()) {
                    listItem = new ListItem();
                    listItem.title = "Telegram";
                    listItem.subtitle = file.toString();
                    listItem.icon = C0446R.drawable.ic_directory;
                    listItem.file = file;
                    this.items.add(listItem);
                }
                listItem2 = new ListItem();
                listItem2.title = LocaleController.getString("Gallery", C0446R.string.Gallery);
                listItem2.subtitle = LocaleController.getString("GalleryInfo", C0446R.string.GalleryInfo);
                listItem2.icon = C0446R.drawable.ic_storage_gallery;
                listItem2.file = null;
                this.items.add(listItem2);
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                this.listAdapter.notifyDataSetChanged();
            } catch (Throwable th2) {
                th = th2;
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable e42) {
                        FileLog.m3e(e42);
                    }
                }
                throw th;
            }
        } catch (Throwable e422) {
            bufferedReader = null;
            th = e422;
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            throw th;
        }
        listItem2 = new ListItem();
        listItem2.title = "/";
        listItem2.subtitle = LocaleController.getString("SystemRoot", C0446R.string.SystemRoot);
        listItem2.icon = C0446R.drawable.ic_directory;
        listItem2.file = new File("/");
        this.items.add(listItem2);
        try {
            file = new File(Environment.getExternalStorageDirectory(), "Telegram");
            if (file.exists()) {
                listItem = new ListItem();
                listItem.title = "Telegram";
                listItem.subtitle = file.toString();
                listItem.icon = C0446R.drawable.ic_directory;
                listItem.file = file;
                this.items.add(listItem);
            }
        } catch (Throwable e4222) {
            FileLog.m3e(e4222);
        }
        listItem2 = new ListItem();
        listItem2.title = LocaleController.getString("Gallery", C0446R.string.Gallery);
        listItem2.subtitle = LocaleController.getString("GalleryInfo", C0446R.string.GalleryInfo);
        listItem2.icon = C0446R.drawable.ic_storage_gallery;
        listItem2.file = null;
        this.items.add(listItem2);
        AndroidUtilities.clearDrawableAnimation(this.listView);
        this.scrolling = true;
        this.listAdapter.notifyDataSetChanged();
    }

    private String getRootSubtitle(String str) {
        try {
            StatFs statFs = new StatFs(str);
            long availableBlocks = ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
            if (((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize()) == 0) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            return LocaleController.formatString("FreeOfTotal", C0446R.string.FreeOfTotal, AndroidUtilities.formatFileSize(availableBlocks), AndroidUtilities.formatFileSize(((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize())));
        } catch (Throwable e) {
            FileLog.m3e(e);
            return str;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[22];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[6] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefault);
        themeDescriptionArr[10] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefaultTop);
        themeDescriptionArr[11] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector);
        themeDescriptionArr[12] = new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultIcon);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkbox);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkboxCheck);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, null, null, null, Theme.key_files_folderIcon);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, null, null, null, Theme.key_files_folderIconBackground);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, null, null, null, Theme.key_files_iconText);
        return themeDescriptionArr;
    }
}
