package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;

public class DocumentSelectActivity extends BaseFragment {
    private static final int done = 3;
    private ArrayList<View> actionModeViews = new ArrayList();
    private File currentDir;
    private DocumentSelectActivityDelegate delegate;
    private TextView emptyView;
    private ArrayList<HistoryEntry> history = new ArrayList();
    private ArrayList<ListItem> items = new ArrayList();
    private ListAdapter listAdapter;
    private ListView listView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        if (DocumentSelectActivity.this.currentDir == null) {
                            DocumentSelectActivity.this.listRoots();
                        } else {
                            DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
                        }
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            };
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                DocumentSelectActivity.this.listView.postDelayed(r, 1000);
            } else {
                r.run();
            }
        }
    };
    private boolean receiverRegistered = false;
    private ArrayList<ListItem> recentItems = new ArrayList();
    private boolean scrolling;
    private HashMap<String, ListItem> selectedFiles = new HashMap();
    private NumberTextView selectedMessagesCountTextView;
    private long sizeLimit = NUM;

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
            this.subtitle = "";
            this.ext = "";
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getCount() {
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

        public long getItemId(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public int getItemViewType(int position) {
            return getItem(position) != null ? 1 : 0;
        }

        public View getView(int position, View view, ViewGroup parent) {
            boolean z = true;
            ListItem item = getItem(position);
            if (item != null) {
                if (view == null) {
                    view = new SharedDocumentCell(this.mContext);
                }
                SharedDocumentCell documentCell = (SharedDocumentCell) view;
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
                    return view;
                }
                boolean z2;
                boolean containsKey = DocumentSelectActivity.this.selectedFiles.containsKey(item.file.toString());
                if (DocumentSelectActivity.this.scrolling) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                documentCell.setChecked(containsKey, z2);
                return view;
            } else if (view != null) {
                return view;
            } else {
                view = new GreySectionCell(this.mContext);
                ((GreySectionCell) view).setText(LocaleController.getString("Recent", R.string.Recent).toUpperCase());
                return view;
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
            FileLog.e("tmessages", e);
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                        DocumentSelectActivity.this.selectedFiles.clear();
                        DocumentSelectActivity.this.actionBar.hideActionMode();
                        DocumentSelectActivity.this.listView.invalidateViews();
                        return;
                    }
                    DocumentSelectActivity.this.finishFragment();
                } else if (id == 3 && DocumentSelectActivity.this.delegate != null) {
                    ArrayList<String> files = new ArrayList();
                    files.addAll(DocumentSelectActivity.this.selectedFiles.keySet());
                    DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, files);
                    for (ListItem item : DocumentSelectActivity.this.selectedFiles.values()) {
                        item.date = System.currentTimeMillis();
                    }
                }
            }
        });
        this.selectedFiles.clear();
        this.actionModeViews.clear();
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        this.selectedMessagesCountTextView = new NumberTextView(actionMode.getContext());
        this.selectedMessagesCountTextView.setTextSize(18);
        this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedMessagesCountTextView.setTextColor(Theme.ACTION_BAR_ACTION_MODE_TEXT_COLOR);
        this.selectedMessagesCountTextView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, (float) DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 65, 0, 0, 0));
        this.actionModeViews.add(actionMode.addItem(3, R.drawable.ic_ab_done_gray, Theme.ACTION_BAR_MODE_SELECTOR_COLOR, null, AndroidUtilities.dp(54.0f)));
        this.fragmentView = getParentActivity().getLayoutInflater().inflate(R.layout.document_select_layout, null, false);
        this.listAdapter = new ListAdapter(context);
        this.emptyView = (TextView) this.fragmentView.findViewById(R.id.searchEmptyView);
        this.emptyView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.listView = (ListView) this.fragmentView.findViewById(R.id.listView);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                DocumentSelectActivity.this.scrolling = scrollState != 0;
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long id) {
                if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                    return false;
                }
                ListItem item = DocumentSelectActivity.this.listAdapter.getItem(i);
                if (item == null) {
                    return false;
                }
                File file = item.file;
                if (!(file == null || file.isDirectory())) {
                    if (!file.canRead()) {
                        DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", R.string.AccessError));
                        return false;
                    } else if (DocumentSelectActivity.this.sizeLimit != 0 && file.length() > DocumentSelectActivity.this.sizeLimit) {
                        DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", R.string.FileUploadLimit, AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit)));
                        return false;
                    } else if (file.length() == 0) {
                        return false;
                    } else {
                        DocumentSelectActivity.this.selectedFiles.put(file.toString(), item);
                        DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(1, false);
                        AnimatorSet animatorSet = new AnimatorSet();
                        ArrayList<Animator> animators = new ArrayList();
                        for (int a = 0; a < DocumentSelectActivity.this.actionModeViews.size(); a++) {
                            View view2 = (View) DocumentSelectActivity.this.actionModeViews.get(a);
                            AndroidUtilities.clearDrawableAnimation(view2);
                            animators.add(ObjectAnimator.ofFloat(view2, "scaleY", new float[]{0.1f, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT}));
                        }
                        animatorSet.playTogether(animators);
                        animatorSet.setDuration(250);
                        animatorSet.start();
                        DocumentSelectActivity.this.scrolling = false;
                        if (view instanceof SharedDocumentCell) {
                            ((SharedDocumentCell) view).setChecked(true, true);
                        }
                        DocumentSelectActivity.this.actionBar.showActionMode();
                    }
                }
                return true;
            }
        });
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItem item = DocumentSelectActivity.this.listAdapter.getItem(i);
                if (item != null) {
                    File file = item.file;
                    HistoryEntry he;
                    if (file == null) {
                        if (item.icon == R.drawable.ic_storage_gallery) {
                            if (DocumentSelectActivity.this.delegate != null) {
                                DocumentSelectActivity.this.delegate.startDocumentSelectActivity();
                            }
                            DocumentSelectActivity.this.finishFragment(false);
                            return;
                        }
                        he = (HistoryEntry) DocumentSelectActivity.this.history.remove(DocumentSelectActivity.this.history.size() - 1);
                        DocumentSelectActivity.this.actionBar.setTitle(he.title);
                        if (he.dir != null) {
                            DocumentSelectActivity.this.listFiles(he.dir);
                        } else {
                            DocumentSelectActivity.this.listRoots();
                        }
                        DocumentSelectActivity.this.listView.setSelectionFromTop(he.scrollItem, he.scrollOffset);
                    } else if (file.isDirectory()) {
                        he = new HistoryEntry();
                        he.scrollItem = DocumentSelectActivity.this.listView.getFirstVisiblePosition();
                        he.scrollOffset = DocumentSelectActivity.this.listView.getChildAt(0).getTop();
                        he.dir = DocumentSelectActivity.this.currentDir;
                        he.title = DocumentSelectActivity.this.actionBar.getTitle();
                        DocumentSelectActivity.this.history.add(he);
                        if (DocumentSelectActivity.this.listFiles(file)) {
                            DocumentSelectActivity.this.actionBar.setTitle(item.title);
                            DocumentSelectActivity.this.listView.setSelection(0);
                            return;
                        }
                        DocumentSelectActivity.this.history.remove(he);
                    } else {
                        if (!file.canRead()) {
                            DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", R.string.AccessError));
                            file = new File("/mnt/sdcard");
                        }
                        if (DocumentSelectActivity.this.sizeLimit != 0 && file.length() > DocumentSelectActivity.this.sizeLimit) {
                            DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", R.string.FileUploadLimit, AndroidUtilities.formatFileSize(DocumentSelectActivity.this.sizeLimit)));
                        } else if (file.length() == 0) {
                        } else {
                            if (DocumentSelectActivity.this.actionBar.isActionModeShowed()) {
                                if (DocumentSelectActivity.this.selectedFiles.containsKey(file.toString())) {
                                    DocumentSelectActivity.this.selectedFiles.remove(file.toString());
                                } else {
                                    DocumentSelectActivity.this.selectedFiles.put(file.toString(), item);
                                }
                                if (DocumentSelectActivity.this.selectedFiles.isEmpty()) {
                                    DocumentSelectActivity.this.actionBar.hideActionMode();
                                } else {
                                    DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(DocumentSelectActivity.this.selectedFiles.size(), true);
                                }
                                DocumentSelectActivity.this.scrolling = false;
                                if (view instanceof SharedDocumentCell) {
                                    ((SharedDocumentCell) view).setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(item.file.toString()), true);
                                }
                            } else if (DocumentSelectActivity.this.delegate != null) {
                                ArrayList<String> files = new ArrayList();
                                files.add(file.getAbsolutePath());
                                DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, files);
                            }
                        }
                    }
                }
            }
        });
        listRoots();
        return this.fragmentView;
    }

    public void loadRecentFiles() {
        try {
            File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File lhs, File rhs) {
                    if (lhs.isDirectory() == rhs.isDirectory()) {
                        long lm = lhs.lastModified();
                        long rm = lhs.lastModified();
                        if (lm == rm) {
                            return 0;
                        }
                        if (lm >= rm) {
                            return 1;
                        }
                        return -1;
                    } else if (lhs.isDirectory()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            for (File file : files) {
                if (!file.isDirectory()) {
                    ListItem item = new ListItem();
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
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
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
            this.listView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    DocumentSelectActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    DocumentSelectActivity.this.fixLayoutInternal();
                    return true;
                }
            });
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
        this.listView.setSelectionFromTop(he.scrollItem, he.scrollOffset);
        return false;
    }

    public void setDelegate(DocumentSelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private boolean listFiles(File dir) {
        if (dir.canRead()) {
            this.emptyView.setText(LocaleController.getString("NoFiles", R.string.NoFiles));
            try {
                File[] files = dir.listFiles();
                if (files == null) {
                    showErrorBox(LocaleController.getString("UnknownError", R.string.UnknownError));
                    return false;
                }
                ListItem item;
                this.currentDir = dir;
                this.items.clear();
                Arrays.sort(files, new Comparator<File>() {
                    public int compare(File lhs, File rhs) {
                        if (lhs.isDirectory() != rhs.isDirectory()) {
                            return lhs.isDirectory() ? -1 : 1;
                        } else {
                            return lhs.getName().compareToIgnoreCase(rhs.getName());
                        }
                    }
                });
                for (File file : files) {
                    if (file.getName().indexOf(46) != 0) {
                        item = new ListItem();
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
                item = new ListItem();
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

    private void showErrorBox(String error) {
        if (getParentActivity() != null) {
            new Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(error).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @SuppressLint({"NewApi"})
    private void listRoots() {
        Throwable e;
        Throwable th;
        ListItem fs;
        File file;
        ListItem fs2;
        this.currentDir = null;
        this.items.clear();
        HashSet<String> paths = new HashSet();
        String defaultPath = Environment.getExternalStorageDirectory().getPath();
        boolean isDefaultPathRemovable = Environment.isExternalStorageRemovable();
        String defaultPathState = Environment.getExternalStorageState();
        if (defaultPathState.equals("mounted") || defaultPathState.equals("mounted_ro")) {
            ListItem ext = new ListItem();
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
                    if (line == null) {
                        break;
                    } else if (line.contains("vfat") || line.contains("/mnt")) {
                        FileLog.e("tmessages", line);
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
                            ListItem item = new ListItem();
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
                } catch (Exception e2) {
                    e = e2;
                    bufferedReader = bufferedReader2;
                } catch (Throwable th2) {
                    th = th2;
                    bufferedReader = bufferedReader2;
                }
            }
            if (bufferedReader2 != null) {
                try {
                    bufferedReader2.close();
                    bufferedReader = bufferedReader2;
                } catch (Throwable e3) {
                    FileLog.e("tmessages", e3);
                    bufferedReader = bufferedReader2;
                }
            }
        } catch (Exception e4) {
            e3 = e4;
            try {
                FileLog.e("tmessages", e3);
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable e32) {
                        FileLog.e("tmessages", e32);
                    }
                }
                fs = new ListItem();
                fs.title = "/";
                fs.subtitle = LocaleController.getString("SystemRoot", R.string.SystemRoot);
                fs.icon = R.drawable.ic_directory;
                fs.file = new File("/");
                this.items.add(fs);
                file = new File(Environment.getExternalStorageDirectory(), "Telegram");
                if (file.exists()) {
                    fs2 = new ListItem();
                    try {
                        fs2.title = "Telegram";
                        fs2.subtitle = file.toString();
                        fs2.icon = R.drawable.ic_directory;
                        fs2.file = file;
                        this.items.add(fs2);
                        fs = fs2;
                    } catch (Exception e5) {
                        e32 = e5;
                        fs = fs2;
                        FileLog.e("tmessages", e32);
                        fs = new ListItem();
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
                fs = new ListItem();
                fs.title = LocaleController.getString("Gallery", R.string.Gallery);
                fs.subtitle = LocaleController.getString("GalleryInfo", R.string.GalleryInfo);
                fs.icon = R.drawable.ic_storage_gallery;
                fs.file = null;
                this.items.add(fs);
                AndroidUtilities.clearDrawableAnimation(this.listView);
                this.scrolling = true;
                this.listAdapter.notifyDataSetChanged();
            } catch (Throwable th3) {
                th = th3;
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable e322) {
                        FileLog.e("tmessages", e322);
                    }
                }
                throw th;
            }
        }
        fs = new ListItem();
        fs.title = "/";
        fs.subtitle = LocaleController.getString("SystemRoot", R.string.SystemRoot);
        fs.icon = R.drawable.ic_directory;
        fs.file = new File("/");
        this.items.add(fs);
        try {
            file = new File(Environment.getExternalStorageDirectory(), "Telegram");
            if (file.exists()) {
                fs2 = new ListItem();
                fs2.title = "Telegram";
                fs2.subtitle = file.toString();
                fs2.icon = R.drawable.ic_directory;
                fs2.file = file;
                this.items.add(fs2);
                fs = fs2;
            }
        } catch (Exception e6) {
            e322 = e6;
            FileLog.e("tmessages", e322);
            fs = new ListItem();
            fs.title = LocaleController.getString("Gallery", R.string.Gallery);
            fs.subtitle = LocaleController.getString("GalleryInfo", R.string.GalleryInfo);
            fs.icon = R.drawable.ic_storage_gallery;
            fs.file = null;
            this.items.add(fs);
            AndroidUtilities.clearDrawableAnimation(this.listView);
            this.scrolling = true;
            this.listAdapter.notifyDataSetChanged();
        }
        fs = new ListItem();
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
                return "";
            }
            return LocaleController.formatString("FreeOfTotal", R.string.FreeOfTotal, AndroidUtilities.formatFileSize(free), AndroidUtilities.formatFileSize(((long) stat.getBlockCount()) * ((long) stat.getBlockSize())));
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return path;
        }
    }
}
