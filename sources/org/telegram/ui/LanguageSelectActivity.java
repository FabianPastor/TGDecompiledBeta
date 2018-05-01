package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class LanguageSelectActivity extends BaseFragment implements NotificationCenterDelegate {
    private EmptyTextProgressView emptyView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ListAdapter searchListViewAdapter;
    private ArrayList<LocaleInfo> searchResult;
    private Timer searchTimer;
    private boolean searchWas;
    private boolean searching;
    private ArrayList<LocaleInfo> sortedLanguages;

    /* renamed from: org.telegram.ui.LanguageSelectActivity$1 */
    class C21611 extends ActionBarMenuOnItemClick {
        C21611() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                LanguageSelectActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity$2 */
    class C21622 extends ActionBarMenuItemSearchListener {
        C21622() {
        }

        public void onSearchExpand() {
            LanguageSelectActivity.this.searching = true;
        }

        public void onSearchCollapse() {
            LanguageSelectActivity.this.search(null);
            LanguageSelectActivity.this.searching = false;
            LanguageSelectActivity.this.searchWas = false;
            if (LanguageSelectActivity.this.listView != null) {
                LanguageSelectActivity.this.emptyView.setVisibility(8);
                LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.listAdapter);
            }
        }

        public void onTextChanged(EditText editText) {
            editText = editText.getText().toString();
            LanguageSelectActivity.this.search(editText);
            if (editText.length() != null) {
                LanguageSelectActivity.this.searchWas = true;
                if (LanguageSelectActivity.this.listView != null) {
                    LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity$3 */
    class C21633 implements OnItemClickListener {
        C21633() {
        }

        public void onItemClick(View view, int i) {
            if (LanguageSelectActivity.this.getParentActivity() != null) {
                if (LanguageSelectActivity.this.parentLayout != null) {
                    view = null;
                    if (LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                        if (i >= 0 && i < LanguageSelectActivity.this.searchResult.size()) {
                            view = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(i);
                        }
                    } else if (i >= 0 && i < LanguageSelectActivity.this.sortedLanguages.size()) {
                        view = (LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(i);
                    }
                    View view2 = view;
                    if (view2 != null) {
                        LocaleController.getInstance().applyLanguage(view2, true, false, false, true, LanguageSelectActivity.this.currentAccount);
                        LanguageSelectActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
                    }
                    LanguageSelectActivity.this.finishFragment();
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity$4 */
    class C21644 implements OnItemLongClickListener {
        C21644() {
        }

        public boolean onItemClick(View view, int i) {
            if (LanguageSelectActivity.this.searching == null || LanguageSelectActivity.this.searchWas == null) {
                if (i >= 0 && i < LanguageSelectActivity.this.sortedLanguages.size()) {
                    view = (LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(i);
                    if (!(view == null || view.pathToFile == 0 || LanguageSelectActivity.this.getParentActivity() == 0)) {
                        if (view.isRemote() != 0) {
                            i = new Builder(LanguageSelectActivity.this.getParentActivity());
                            i.setMessage(LocaleController.getString("DeleteLocalization", C0446R.string.DeleteLocalization));
                            i.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            i.setPositiveButton(LocaleController.getString("Delete", C0446R.string.Delete), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (LocaleController.getInstance().deleteLanguage(view, LanguageSelectActivity.this.currentAccount) != null) {
                                        LanguageSelectActivity.this.fillLanguages();
                                        if (LanguageSelectActivity.this.searchResult != null) {
                                            LanguageSelectActivity.this.searchResult.remove(view);
                                        }
                                        if (LanguageSelectActivity.this.listAdapter != null) {
                                            LanguageSelectActivity.this.listAdapter.notifyDataSetChanged();
                                        }
                                        if (LanguageSelectActivity.this.searchListViewAdapter != null) {
                                            LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                            i.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                            LanguageSelectActivity.this.showDialog(i.create());
                            return true;
                        }
                    }
                    return null;
                }
            } else if (i >= 0 && i < LanguageSelectActivity.this.searchResult.size()) {
                view = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(i);
                if (view.isRemote() != 0) {
                    return null;
                }
                i = new Builder(LanguageSelectActivity.this.getParentActivity());
                i.setMessage(LocaleController.getString("DeleteLocalization", C0446R.string.DeleteLocalization));
                i.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                i.setPositiveButton(LocaleController.getString("Delete", C0446R.string.Delete), /* anonymous class already generated */);
                i.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                LanguageSelectActivity.this.showDialog(i.create());
                return true;
            }
            view = null;
            if (view.isRemote() != 0) {
                return null;
            }
            i = new Builder(LanguageSelectActivity.this.getParentActivity());
            i.setMessage(LocaleController.getString("DeleteLocalization", C0446R.string.DeleteLocalization));
            i.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            i.setPositiveButton(LocaleController.getString("Delete", C0446R.string.Delete), /* anonymous class already generated */);
            i.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
            LanguageSelectActivity.this.showDialog(i.create());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity$5 */
    class C21655 extends OnScrollListener {
        C21655() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1 && LanguageSelectActivity.this.searching != null && LanguageSelectActivity.this.searchWas != null) {
                AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;
        private boolean search;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context, boolean z) {
            this.mContext = context;
            this.search = z;
        }

        public int getItemCount() {
            if (!this.search) {
                return LanguageSelectActivity.this.sortedLanguages.size();
            }
            if (LanguageSelectActivity.this.searchResult == null) {
                return 0;
            }
            return LanguageSelectActivity.this.searchResult.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new LanguageCell(this.mContext, false));
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            LocaleInfo localeInfo;
            LanguageCell languageCell = (LanguageCell) viewHolder.itemView;
            boolean z = false;
            if (this.search) {
                localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(i);
            } else {
                localeInfo = (LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(i);
                if (i == LanguageSelectActivity.this.sortedLanguages.size() - 1) {
                }
                i = 0;
                if (localeInfo.isLocal()) {
                    languageCell.setLanguage(localeInfo, null, i ^ 1);
                } else {
                    languageCell.setLanguage(localeInfo, String.format("%1$s (%2$s)", new Object[]{localeInfo.name, LocaleController.getString("LanguageCustom", C0446R.string.LanguageCustom)}), i ^ 1);
                }
                if (localeInfo == LocaleController.getInstance().getCurrentLocaleInfo()) {
                    z = true;
                }
                languageCell.setLanguageSelected(z);
            }
            i = 1;
            if (localeInfo.isLocal()) {
                languageCell.setLanguage(localeInfo, null, i ^ 1);
            } else {
                languageCell.setLanguage(localeInfo, String.format("%1$s (%2$s)", new Object[]{localeInfo.name, LocaleController.getString("LanguageCustom", C0446R.string.LanguageCustom)}), i ^ 1);
            }
            if (localeInfo == LocaleController.getInstance().getCurrentLocaleInfo()) {
                z = true;
            }
            languageCell.setLanguageSelected(z);
        }
    }

    public boolean onFragmentCreate() {
        fillLanguages();
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Language", C0446R.string.Language));
        this.actionBar.setActionBarMenuOnItemClick(new C21611());
        this.actionBar.createMenu().addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C21622()).getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        this.listAdapter = new ListAdapter(context, false);
        this.searchListViewAdapter = new ListAdapter(context, true);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
        this.emptyView.showTextView();
        this.emptyView.setShowAtCenter(true);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C21633());
        this.listView.setOnItemLongClickListener(new C21644());
        this.listView.setOnScrollListener(new C21655());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.suggestedLangpack && this.listAdapter != 0) {
            fillLanguages();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void fillLanguages() {
        this.sortedLanguages = new ArrayList(LocaleController.getInstance().languages);
        final LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        Collections.sort(this.sortedLanguages, new Comparator<LocaleInfo>() {
            public int compare(LocaleInfo localeInfo, LocaleInfo localeInfo2) {
                if (localeInfo == currentLocaleInfo) {
                    return -1;
                }
                if (localeInfo2 == currentLocaleInfo) {
                    return 1;
                }
                return localeInfo.name.compareTo(localeInfo2.name);
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void search(final String str) {
        if (str == null) {
            this.searchResult = null;
            return;
        }
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask() {
            public void run() {
                try {
                    LanguageSelectActivity.this.searchTimer.cancel();
                    LanguageSelectActivity.this.searchTimer = null;
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                LanguageSelectActivity.this.processSearch(str);
            }
        }, 100, 300);
    }

    private void processSearch(final String str) {
        Utilities.searchQueue.postRunnable(new Runnable() {
            public void run() {
                if (str.trim().toLowerCase().length() == 0) {
                    LanguageSelectActivity.this.updateSearchResults(new ArrayList());
                    return;
                }
                System.currentTimeMillis();
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < LanguageSelectActivity.this.sortedLanguages.size(); i++) {
                    LocaleInfo localeInfo = (LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(i);
                    if (localeInfo.name.toLowerCase().startsWith(str) || localeInfo.nameEnglish.toLowerCase().startsWith(str)) {
                        arrayList.add(localeInfo);
                    }
                }
                LanguageSelectActivity.this.updateSearchResults(arrayList);
            }
        });
    }

    private void updateSearchResults(final ArrayList<LocaleInfo> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                LanguageSelectActivity.this.searchResult = arrayList;
                LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[14];
        r1[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[12] = new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon);
        return r1;
    }
}
