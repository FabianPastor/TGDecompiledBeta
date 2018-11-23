package org.telegram.p005ui;

import android.animation.Animator;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.ActionBarMenu;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BackDrawable;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.C0403ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.GraySectionCell;
import org.telegram.p005ui.Cells.SharedDocumentCell;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.NumberTextView;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;

/* renamed from: org.telegram.ui.DocumentSelectActivity */
public class DocumentSelectActivity extends BaseFragment {
    private static final int done = 3;
    private ArrayList<View> actionModeViews = new ArrayList();
    private boolean canSelectOnlyImageFiles;
    private File currentDir;
    private DocumentSelectActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private ArrayList<HistoryEntry> history = new ArrayList();
    private ArrayList<ListItem> items = new ArrayList();
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int maxSelectedFiles = -1;
    private BroadcastReceiver receiver = new C09311();
    private boolean receiverRegistered = false;
    private ArrayList<ListItem> recentItems = new ArrayList();
    private boolean scrolling;
    private HashMap<String, ListItem> selectedFiles = new HashMap();
    private NumberTextView selectedMessagesCountTextView;
    private long sizeLimit = NUM;

    /* renamed from: org.telegram.ui.DocumentSelectActivity$1 */
    class C09311 extends BroadcastReceiver {
        C09311() {
        }

        public void onReceive(Context arg0, Intent intent) {
            Runnable r = new DocumentSelectActivity$1$$Lambda$0(this);
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                DocumentSelectActivity.this.listView.postDelayed(r, 1000);
            } else {
                r.run();
            }
        }

        final /* synthetic */ void lambda$onReceive$0$DocumentSelectActivity$1() {
            try {
                if (DocumentSelectActivity.this.currentDir == null) {
                    DocumentSelectActivity.this.listRoots();
                } else {
                    DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
                }
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$4 */
    class C09324 implements OnPreDrawListener {
        C09324() {
        }

        public boolean onPreDraw() {
            DocumentSelectActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
            DocumentSelectActivity.this.fixLayoutInternal();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$DocumentSelectActivityDelegate */
    public interface DocumentSelectActivityDelegate {
        void didSelectFiles(DocumentSelectActivity documentSelectActivity, ArrayList<String> arrayList);

        void startDocumentSelectActivity();
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$HistoryEntry */
    private class HistoryEntry {
        File dir;
        int scrollItem;
        int scrollOffset;
        String title;

        private HistoryEntry() {
        }

        /* synthetic */ HistoryEntry(DocumentSelectActivity x0, C09311 x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$ListItem */
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

        /* synthetic */ ListItem(DocumentSelectActivity x0, C09311 x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$2 */
    class C14352 extends ActionBarMenuOnItemClick {
        C14352() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                    DocumentSelectActivity.this.selectedFiles.clear();
                    DocumentSelectActivity.this.actionBar.hideActionMode();
                    int count = DocumentSelectActivity.this.listView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = DocumentSelectActivity.this.listView.getChildAt(a);
                        if (child instanceof SharedDocumentCell) {
                            ((SharedDocumentCell) child).setChecked(false, true);
                        }
                    }
                    return;
                }
                DocumentSelectActivity.this.lambda$checkDiscard$70$PassportActivity();
            } else if (id == 3 && DocumentSelectActivity.this.delegate != null) {
                DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, new ArrayList(DocumentSelectActivity.this.selectedFiles.keySet()));
                for (ListItem item : DocumentSelectActivity.this.selectedFiles.values()) {
                    item.date = System.currentTimeMillis();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$3 */
    class C14363 extends OnScrollListener {
        C14363() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            DocumentSelectActivity.this.scrolling = newState != 0;
        }
    }

    /* renamed from: org.telegram.ui.DocumentSelectActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() != 0;
        }

        public int getItemCount() {
            int count = DocumentSelectActivity.this.items.size();
            if (!DocumentSelectActivity.this.history.isEmpty() || DocumentSelectActivity.this.recentItems.isEmpty()) {
                return count;
            }
            return count + (DocumentSelectActivity.this.recentItems.size() + 1);
        }

        public ListItem getItem(int position) {
            if (position < DocumentSelectActivity.this.items.size()) {
                return (ListItem) DocumentSelectActivity.this.items.get(position);
            }
            if (!(!DocumentSelectActivity.this.history.isEmpty() || DocumentSelectActivity.this.recentItems.isEmpty() || position == DocumentSelectActivity.this.items.size())) {
                position -= DocumentSelectActivity.this.items.size() + 1;
                if (position < DocumentSelectActivity.this.recentItems.size()) {
                    return (ListItem) DocumentSelectActivity.this.recentItems.get(position);
                }
            }
            return null;
        }

        public int getItemViewType(int position) {
            return getItem(position) != null ? 1 : 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GraySectionCell(this.mContext);
                    ((GraySectionCell) view).setText(LocaleController.getString("Recent", R.string.Recent).toUpperCase());
                    break;
                default:
                    view = new SharedDocumentCell(this.mContext);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            if (holder.getItemViewType() == 1) {
                ListItem item = getItem(position);
                SharedDocumentCell documentCell = holder.itemView;
                if (item.icon != 0) {
                    documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, null, null, item.icon);
                } else {
                    documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4)), item.thumb, 0);
                }
                if (item.file == null || !DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                    if (DocumentSelectActivity.this.scrolling) {
                        z = false;
                    }
                    documentCell.setChecked(false, z);
                    return;
                }
                boolean z2;
                boolean containsKey = DocumentSelectActivity.this.selectedFiles.containsKey(item.file.toString());
                if (DocumentSelectActivity.this.scrolling) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                documentCell.setChecked(containsKey, z2);
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
            FileLog.m13e(e);
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        if (!this.receiverRegistered) {
            this.receiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            filter.addAction("android.intent.action.MEDIA_CHECKING");
            filter.addAction("android.intent.action.MEDIA_EJECT");
            filter.addAction("android.intent.action.MEDIA_MOUNTED");
            filter.addAction("android.intent.action.MEDIA_NOFS");
            filter.addAction("android.intent.action.MEDIA_REMOVED");
            filter.addAction("android.intent.action.MEDIA_SHARED");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
            filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            filter.addDataScheme("file");
            ApplicationLoader.applicationContext.registerReceiver(this.receiver, filter);
        }
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SelectFile", R.string.SelectFile));
        this.actionBar.setActionBarMenuOnItemClick(new C14352());
        this.selectedFiles.clear();
        this.actionModeViews.clear();
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        this.selectedMessagesCountTextView.setOnTouchListener(DocumentSelectActivity$$Lambda$0.$instance);
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 65, 0, 0, 0));
        this.actionModeViews.add(actionMode.addItemWithWidth(3, R.drawable.ic_ab_done, AndroidUtilities.m9dp(54.0f)));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
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
        this.listView.setOnScrollListener(new C14363());
        this.listView.setOnItemLongClickListener(new DocumentSelectActivity$$Lambda$1(this));
        this.listView.setOnItemClickListener(new DocumentSelectActivity$$Lambda$2(this));
        listRoots();
        return this.fragmentView;
    }

    final /* synthetic */ boolean lambda$createView$1$DocumentSelectActivity(View view, int position) {
        if (this.actionBar.isActionModeShowed()) {
            return false;
        }
        ListItem item = this.listAdapter.getItem(position);
        if (item == null) {
            return false;
        }
        File file = item.file;
        if (!(file == null || file.isDirectory())) {
            if (!file.canRead()) {
                showErrorBox(LocaleController.getString("AccessError", R.string.AccessError));
                return false;
            } else if (this.canSelectOnlyImageFiles && item.thumb == null) {
                showErrorBox(LocaleController.formatString("PassportUploadNotImage", R.string.PassportUploadNotImage, new Object[0]));
                return false;
            } else if (this.sizeLimit != 0 && file.length() > this.sizeLimit) {
                showErrorBox(LocaleController.formatString("FileUploadLimit", R.string.FileUploadLimit, AndroidUtilities.formatFileSize(this.sizeLimit)));
                return false;
            } else if (this.maxSelectedFiles >= 0 && this.selectedFiles.size() >= this.maxSelectedFiles) {
                showErrorBox(LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", this.maxSelectedFiles)));
                return false;
            } else if (file.length() == 0) {
                return false;
            } else {
                this.selectedFiles.put(file.toString(), item);
                this.selectedMessagesCountTextView.setNumber(1, false);
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList<Animator> animators = new ArrayList();
                for (int a = 0; a < this.actionModeViews.size(); a++) {
                    View view2 = (View) this.actionModeViews.get(a);
                    AndroidUtilities.clearDrawableAnimation(view2);
                    animators.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, 1.0f}));
                }
                animatorSet.playTogether(animators);
                animatorSet.setDuration(250);
                animatorSet.start();
                this.scrolling = false;
                if (view instanceof SharedDocumentCell) {
                    ((SharedDocumentCell) view).setChecked(true, true);
                }
                this.actionBar.showActionMode();
            }
        }
        return true;
    }

    final /* synthetic */ void lambda$createView$2$DocumentSelectActivity(View view, int position) {
        ListItem item = this.listAdapter.getItem(position);
        if (item != null) {
            File file = item.file;
            HistoryEntry he;
            if (file == null) {
                if (item.icon == R.drawable.ic_storage_gallery) {
                    if (this.delegate != null) {
                        this.delegate.startDocumentSelectActivity();
                    }
                    finishFragment(false);
                    return;
                }
                he = (HistoryEntry) this.history.remove(this.history.size() - 1);
                this.actionBar.setTitle(he.title);
                if (he.dir != null) {
                    listFiles(he.dir);
                } else {
                    listRoots();
                }
                this.layoutManager.scrollToPositionWithOffset(he.scrollItem, he.scrollOffset);
            } else if (file.isDirectory()) {
                he = new HistoryEntry(this, null);
                he.scrollItem = this.layoutManager.findLastVisibleItemPosition();
                View topView = this.layoutManager.findViewByPosition(he.scrollItem);
                if (topView != null) {
                    he.scrollOffset = topView.getTop();
                }
                he.dir = this.currentDir;
                he.title = this.actionBar.getTitle();
                this.history.add(he);
                if (listFiles(file)) {
                    this.actionBar.setTitle(item.title);
                } else {
                    this.history.remove(he);
                }
            } else {
                if (!file.canRead()) {
                    showErrorBox(LocaleController.getString("AccessError", R.string.AccessError));
                    file = new File("/mnt/sdcard");
                }
                if (this.canSelectOnlyImageFiles && item.thumb == null) {
                    showErrorBox(LocaleController.formatString("PassportUploadNotImage", R.string.PassportUploadNotImage, new Object[0]));
                } else if (this.sizeLimit != 0 && file.length() > this.sizeLimit) {
                    showErrorBox(LocaleController.formatString("FileUploadLimit", R.string.FileUploadLimit, AndroidUtilities.formatFileSize(this.sizeLimit)));
                } else if (file.length() == 0) {
                } else {
                    if (this.actionBar.isActionModeShowed()) {
                        if (this.selectedFiles.containsKey(file.toString())) {
                            this.selectedFiles.remove(file.toString());
                        } else if (this.maxSelectedFiles < 0 || this.selectedFiles.size() < this.maxSelectedFiles) {
                            this.selectedFiles.put(file.toString(), item);
                        } else {
                            showErrorBox(LocaleController.formatString("PassportUploadMaxReached", R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", this.maxSelectedFiles)));
                            return;
                        }
                        if (this.selectedFiles.isEmpty()) {
                            this.actionBar.hideActionMode();
                        } else {
                            this.selectedMessagesCountTextView.setNumber(this.selectedFiles.size(), true);
                        }
                        this.scrolling = false;
                        if (view instanceof SharedDocumentCell) {
                            ((SharedDocumentCell) view).setChecked(this.selectedFiles.containsKey(item.file.toString()), true);
                        }
                    } else if (this.delegate != null) {
                        ArrayList<String> files = new ArrayList();
                        files.add(file.getAbsolutePath());
                        this.delegate.didSelectFiles(this, files);
                    }
                }
            }
        }
    }

    public void setMaxSelectedFiles(int value) {
        this.maxSelectedFiles = value;
    }

    public void setCanSelectOnlyImageFiles(boolean value) {
        this.canSelectOnlyImageFiles = true;
    }

    public void loadRecentFiles() {
        try {
            File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    ListItem item = new ListItem(this, null);
                    item.title = file.getName();
                    item.file = file;
                    String fname = file.getName();
                    String[] sp = fname.split("\\.");
                    item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                    item.subtitle = AndroidUtilities.formatFileSize(file.length());
                    fname = fname.toLowerCase();
                    if (fname.endsWith(".jpg") || fname.endsWith(".png") || fname.endsWith(".gif") || fname.endsWith(".jpeg")) {
                        item.thumb = file.getAbsolutePath();
                    }
                    this.recentItems.add(item);
                }
            }
            Collections.sort(this.recentItems, DocumentSelectActivity$$Lambda$3.$instance);
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    static final /* synthetic */ int lambda$loadRecentFiles$3$DocumentSelectActivity(ListItem o1, ListItem o2) {
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

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        fixLayoutInternal();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.listView != null) {
            this.listView.getViewTreeObserver().addOnPreDrawListener(new C09324());
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
        HistoryEntry he = (HistoryEntry) this.history.remove(this.history.size() - 1);
        this.actionBar.setTitle(he.title);
        if (he.dir != null) {
            listFiles(he.dir);
        } else {
            listRoots();
        }
        this.layoutManager.scrollToPositionWithOffset(he.scrollItem, he.scrollOffset);
        return false;
    }

    public void setDelegate(DocumentSelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private boolean listFiles(File dir) {
        if (dir.canRead()) {
            try {
                File[] files = dir.listFiles();
                if (files == null) {
                    showErrorBox(LocaleController.getString("UnknownError", R.string.UnknownError));
                    return false;
                }
                ListItem item;
                this.currentDir = dir;
                this.items.clear();
                Arrays.sort(files, DocumentSelectActivity$$Lambda$4.$instance);
                for (File file : files) {
                    if (file.getName().indexOf(46) != 0) {
                        item = new ListItem(this, null);
                        item.title = file.getName();
                        item.file = file;
                        if (file.isDirectory()) {
                            item.icon = R.drawable.ic_directory;
                            item.subtitle = LocaleController.getString("Folder", R.string.Folder);
                        } else {
                            String fname = file.getName();
                            String[] sp = fname.split("\\.");
                            item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                            item.subtitle = AndroidUtilities.formatFileSize(file.length());
                            fname = fname.toLowerCase();
                            if (fname.endsWith(".jpg") || fname.endsWith(".png") || fname.endsWith(".gif") || fname.endsWith(".jpeg")) {
                                item.thumb = file.getAbsolutePath();
                            }
                        }
                        this.items.add(item);
                    }
                }
                item = new ListItem(this, null);
                item.title = "..";
                if (this.history.size() > 0) {
                    HistoryEntry entry = (HistoryEntry) this.history.get(this.history.size() - 1);
                    if (entry.dir == null) {
                        item.subtitle = LocaleController.getString("Folder", R.string.Folder);
                    } else {
                        item.subtitle = entry.dir.toString();
                    }
                } else {
                    item.subtitle = LocaleController.getString("Folder", R.string.Folder);
                }
                item.icon = R.drawable.ic_directory;
                item.file = null;
                this.items.add(0, item);
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                this.listAdapter.notifyDataSetChanged();
                return true;
            } catch (Exception e) {
                showErrorBox(e.getLocalizedMessage());
                return false;
            }
        } else if ((!dir.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString()) && !dir.getAbsolutePath().startsWith("/sdcard") && !dir.getAbsolutePath().startsWith("/mnt/sdcard")) || Environment.getExternalStorageState().equals("mounted") || Environment.getExternalStorageState().equals("mounted_ro")) {
            showErrorBox(LocaleController.getString("AccessError", R.string.AccessError));
            return false;
        } else {
            this.currentDir = dir;
            this.items.clear();
            if ("shared".equals(Environment.getExternalStorageState())) {
                this.emptyView.setText(LocaleController.getString("UsbActive", R.string.UsbActive));
            } else {
                this.emptyView.setText(LocaleController.getString("NotMounted", R.string.NotMounted));
            }
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
            return true;
        }
    }

    static final /* synthetic */ int lambda$listFiles$4$DocumentSelectActivity(File lhs, File rhs) {
        if (lhs.isDirectory() != rhs.isDirectory()) {
            return lhs.isDirectory() ? -1 : 1;
        } else {
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    }

    private void showErrorBox(String error) {
        if (getParentActivity() != null) {
            new Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(error).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:64:0x0223 A:{Catch:{ Exception -> 0x02f7 }} */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x02d5 A:{ExcHandler: all (th java.lang.Throwable), Splitter: B:11:0x0094} */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing block: B:49:0x01bd, code:
            r7 = move-exception;
     */
    /* JADX WARNING: Missing block: B:51:?, code:
            org.telegram.messenger.FileLog.m13e(r7);
     */
    /* JADX WARNING: Missing block: B:73:0x02d5, code:
            r21 = th;
     */
    /* JADX WARNING: Missing block: B:74:0x02d6, code:
            r3 = r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"NewApi"})
    private void listRoots() {
        Throwable e;
        ListItem fs;
        File file;
        this.currentDir = null;
        this.items.clear();
        HashSet<String> paths = new HashSet();
        String defaultPath = Environment.getExternalStorageDirectory().getPath();
        boolean isDefaultPathRemovable = Environment.isExternalStorageRemovable();
        String defaultPathState = Environment.getExternalStorageState();
        if (defaultPathState.equals("mounted") || defaultPathState.equals("mounted_ro")) {
            ListItem ext = new ListItem(this, null);
            if (Environment.isExternalStorageRemovable()) {
                ext.title = LocaleController.getString("SdCard", R.string.SdCard);
                ext.icon = R.drawable.ic_external_storage;
            } else {
                ext.title = LocaleController.getString("InternalStorage", R.string.InternalStorage);
                ext.icon = R.drawable.ic_storage;
            }
            ext.subtitle = getRootSubtitle(defaultPath);
            ext.file = Environment.getExternalStorageDirectory();
            this.items.add(ext);
            paths.add(defaultPath);
        }
        BufferedReader bufferedReader = null;
        try {
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader("/proc/mounts"));
            while (true) {
                try {
                    String line = bufferedReader2.readLine();
                    if (line != null) {
                        if (line.contains("vfat") || line.contains("/mnt")) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m10d(line);
                            }
                            StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
                            String unused = stringTokenizer.nextToken();
                            String path = stringTokenizer.nextToken();
                            if (!(paths.contains(path) || !line.contains("/dev/block/vold") || line.contains("/mnt/secure") || line.contains("/mnt/asec") || line.contains("/mnt/obb") || line.contains("/dev/mapper") || line.contains("tmpfs"))) {
                                if (!new File(path).isDirectory()) {
                                    int index = path.lastIndexOf(47);
                                    if (index != -1) {
                                        String newPath = "/storage/" + path.substring(index + 1);
                                        if (new File(newPath).isDirectory()) {
                                            path = newPath;
                                        }
                                    }
                                }
                                paths.add(path);
                                ListItem item = new ListItem(this, null);
                                if (path.toLowerCase().contains("sd")) {
                                    item.title = LocaleController.getString("SdCard", R.string.SdCard);
                                } else {
                                    item.title = LocaleController.getString("ExternalStorage", R.string.ExternalStorage);
                                }
                                item.icon = R.drawable.ic_external_storage;
                                item.subtitle = getRootSubtitle(path);
                                item.file = new File(path);
                                this.items.add(item);
                            }
                        }
                    } else if (bufferedReader2 != null) {
                        try {
                            bufferedReader2.close();
                            bufferedReader = bufferedReader2;
                        } catch (Throwable e2) {
                            FileLog.m13e(e2);
                            bufferedReader = bufferedReader2;
                        }
                    }
                } catch (Exception e3) {
                    e2 = e3;
                    bufferedReader = bufferedReader2;
                } catch (Throwable th) {
                }
            }
        } catch (Exception e4) {
            e2 = e4;
            try {
                FileLog.m13e(e2);
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable e22) {
                        FileLog.m13e(e22);
                    }
                }
                fs = new ListItem(this, null);
                fs.title = "/";
                fs.subtitle = LocaleController.getString("SystemRoot", R.string.SystemRoot);
                fs.icon = R.drawable.ic_directory;
                fs.file = new File("/");
                this.items.add(fs);
                file = new File(Environment.getExternalStorageDirectory(), "Telegram");
                if (file.exists()) {
                }
                fs = new ListItem(this, null);
                fs.title = LocaleController.getString("Gallery", R.string.Gallery);
                fs.subtitle = LocaleController.getString("GalleryInfo", R.string.GalleryInfo);
                fs.icon = R.drawable.ic_storage_gallery;
                fs.file = null;
                this.items.add(fs);
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                this.listAdapter.notifyDataSetChanged();
            } catch (Throwable th2) {
                Throwable th3 = th2;
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable e222) {
                        FileLog.m13e(e222);
                    }
                }
                throw th3;
            }
        }
        fs = new ListItem(this, null);
        fs.title = "/";
        fs.subtitle = LocaleController.getString("SystemRoot", R.string.SystemRoot);
        fs.icon = R.drawable.ic_directory;
        fs.file = new File("/");
        this.items.add(fs);
        try {
            file = new File(Environment.getExternalStorageDirectory(), "Telegram");
            if (file.exists()) {
                ListItem fs2 = new ListItem(this, null);
                try {
                    fs2.title = "Telegram";
                    fs2.subtitle = file.toString();
                    fs2.icon = R.drawable.ic_directory;
                    fs2.file = file;
                    this.items.add(fs2);
                    fs = fs2;
                } catch (Exception e5) {
                    e222 = e5;
                    fs = fs2;
                    FileLog.m13e(e222);
                    fs = new ListItem(this, null);
                    fs.title = LocaleController.getString("Gallery", R.string.Gallery);
                    fs.subtitle = LocaleController.getString("GalleryInfo", R.string.GalleryInfo);
                    fs.icon = R.drawable.ic_storage_gallery;
                    fs.file = null;
                    this.items.add(fs);
                    AndroidUtilities.clearDrawableAnimation(this.listView);
                    this.scrolling = true;
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e6) {
            e222 = e6;
            FileLog.m13e(e222);
            fs = new ListItem(this, null);
            fs.title = LocaleController.getString("Gallery", R.string.Gallery);
            fs.subtitle = LocaleController.getString("GalleryInfo", R.string.GalleryInfo);
            fs.icon = R.drawable.ic_storage_gallery;
            fs.file = null;
            this.items.add(fs);
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
        }
        fs = new ListItem(this, null);
        fs.title = LocaleController.getString("Gallery", R.string.Gallery);
        fs.subtitle = LocaleController.getString("GalleryInfo", R.string.GalleryInfo);
        fs.icon = R.drawable.ic_storage_gallery;
        fs.file = null;
        this.items.add(fs);
        AndroidUtilities.clearDrawableAnimation(this.listView);
        this.scrolling = true;
        this.listAdapter.notifyDataSetChanged();
    }

    private String getRootSubtitle(String path) {
        try {
            StatFs stat = new StatFs(path);
            long free = ((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize());
            if (((long) stat.getBlockCount()) * ((long) stat.getBlockSize()) == 0) {
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
            return LocaleController.formatString("FreeOfTotal", R.string.FreeOfTotal, AndroidUtilities.formatFileSize(free), AndroidUtilities.formatFileSize(((long) stat.getBlockCount()) * ((long) stat.getBlockSize())));
        } catch (Throwable e) {
            FileLog.m13e(e);
            return path;
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
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_graySectionText);
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
