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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
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

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;
        private boolean search;

        public ListAdapter(Context context, boolean isSearch) {
            this.mContext = context;
            this.search = isSearch;
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
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

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(new LanguageCell(this.mContext, false));
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            LocaleInfo localeInfo;
            boolean last;
            boolean z = true;
            LanguageCell textSettingsCell = holder.itemView;
            if (this.search) {
                localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(position);
                if (position == LanguageSelectActivity.this.searchResult.size() - 1) {
                    last = true;
                } else {
                    last = false;
                }
            } else {
                localeInfo = (LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(position);
                last = position == LanguageSelectActivity.this.sortedLanguages.size() + -1;
            }
            if (localeInfo.isLocal()) {
                boolean z2;
                String format = String.format("%1$s (%2$s)", new Object[]{localeInfo.name, LocaleController.getString("LanguageCustom", R.string.LanguageCustom)});
                if (last) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                textSettingsCell.setLanguage(localeInfo, format, z2);
            } else {
                textSettingsCell.setLanguage(localeInfo, null, !last);
            }
            if (localeInfo != LocaleController.getInstance().getCurrentLocaleInfo()) {
                z = false;
            }
            textSettingsCell.setLanguageSelected(z);
        }

        public int getItemViewType(int i) {
            return 0;
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Language", R.string.Language));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    LanguageSelectActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
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
                String text = editText.getText().toString();
                LanguageSelectActivity.this.search(text);
                if (text.length() != 0) {
                    LanguageSelectActivity.this.searchWas = true;
                    if (LanguageSelectActivity.this.listView != null) {
                        LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
                    }
                }
            }
        }).getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        this.listAdapter = new ListAdapter(context, false);
        this.searchListViewAdapter = new ListAdapter(context, true);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.showTextView();
        this.emptyView.setShowAtCenter(true);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (LanguageSelectActivity.this.getParentActivity() != null && LanguageSelectActivity.this.parentLayout != null) {
                    LocaleInfo localeInfo = null;
                    if (LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                        if (position >= 0 && position < LanguageSelectActivity.this.searchResult.size()) {
                            localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(position);
                        }
                    } else if (position >= 0 && position < LanguageSelectActivity.this.sortedLanguages.size()) {
                        localeInfo = (LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(position);
                    }
                    if (localeInfo != null) {
                        LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, LanguageSelectActivity.this.currentAccount);
                        LanguageSelectActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
                    }
                    LanguageSelectActivity.this.finishFragment();
                }
            }
        });
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemClick(View view, int position) {
                LocaleInfo localeInfo = null;
                if (LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                    if (position >= 0 && position < LanguageSelectActivity.this.searchResult.size()) {
                        localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(position);
                    }
                } else if (position >= 0 && position < LanguageSelectActivity.this.sortedLanguages.size()) {
                    localeInfo = (LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(position);
                }
                if (localeInfo == null || localeInfo.pathToFile == null || LanguageSelectActivity.this.getParentActivity() == null || localeInfo.isRemote()) {
                    return false;
                }
                final LocaleInfo finalLocaleInfo = localeInfo;
                Builder builder = new Builder(LanguageSelectActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("DeleteLocalization", R.string.DeleteLocalization));
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (LocaleController.getInstance().deleteLanguage(finalLocaleInfo, LanguageSelectActivity.this.currentAccount)) {
                            LanguageSelectActivity.this.fillLanguages();
                            if (LanguageSelectActivity.this.searchResult != null) {
                                LanguageSelectActivity.this.searchResult.remove(finalLocaleInfo);
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
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                LanguageSelectActivity.this.showDialog(builder.create());
                return true;
            }
        });
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.suggestedLangpack && this.listAdapter != null) {
            fillLanguages();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void fillLanguages() {
        this.sortedLanguages = new ArrayList(LocaleController.getInstance().languages);
        final LocaleInfo currentLocale = LocaleController.getInstance().getCurrentLocaleInfo();
        Collections.sort(this.sortedLanguages, new Comparator<LocaleInfo>() {
            public int compare(LocaleInfo o, LocaleInfo o2) {
                if (o == currentLocale) {
                    return -1;
                }
                if (o2 == currentLocale) {
                    return 1;
                }
                return o.name.compareTo(o2.name);
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void search(final String query) {
        if (query == null) {
            this.searchResult = null;
            return;
        }
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask() {
            public void run() {
                try {
                    LanguageSelectActivity.this.searchTimer.cancel();
                    LanguageSelectActivity.this.searchTimer = null;
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                LanguageSelectActivity.this.processSearch(query);
            }
        }, 100, 300);
    }

    private void processSearch(final String query) {
        Utilities.searchQueue.postRunnable(new Runnable() {
            public void run() {
                if (query.trim().toLowerCase().length() == 0) {
                    LanguageSelectActivity.this.updateSearchResults(new ArrayList());
                    return;
                }
                long time = System.currentTimeMillis();
                ArrayList<LocaleInfo> resultArray = new ArrayList();
                for (int a = 0; a < LanguageSelectActivity.this.sortedLanguages.size(); a++) {
                    LocaleInfo c = (LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(a);
                    if (c.name.toLowerCase().startsWith(query) || c.nameEnglish.toLowerCase().startsWith(query)) {
                        resultArray.add(c);
                    }
                }
                LanguageSelectActivity.this.updateSearchResults(resultArray);
            }
        });
    }

    private void updateSearchResults(final ArrayList<LocaleInfo> arrCounties) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                LanguageSelectActivity.this.searchResult = arrCounties;
                LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[14];
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[12] = new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon);
        return r9;
    }
}
