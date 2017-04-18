package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
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
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class LanguageSelectActivity extends BaseFragment {
    private EmptyTextProgressView emptyView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ListAdapter searchListViewAdapter;
    public ArrayList<LocaleInfo> searchResult;
    private Timer searchTimer;
    private boolean searchWas;
    private boolean searching;

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
            if (this.search) {
                if (LanguageSelectActivity.this.searchResult == null) {
                    return 0;
                }
                return LanguageSelectActivity.this.searchResult.size();
            } else if (LocaleController.getInstance().sortedLanguages != null) {
                return LocaleController.getInstance().sortedLanguages.size();
            } else {
                return 0;
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(new TextSettingsCell(this.mContext));
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            LocaleInfo localeInfo;
            boolean last;
            boolean z = true;
            TextSettingsCell textSettingsCell = holder.itemView;
            if (this.search) {
                localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(position);
                if (position == LanguageSelectActivity.this.searchResult.size() - 1) {
                    last = true;
                } else {
                    last = false;
                }
            } else {
                localeInfo = (LocaleInfo) LocaleController.getInstance().sortedLanguages.get(position);
                last = position == LocaleController.getInstance().sortedLanguages.size() + -1;
            }
            if (localeInfo.isRemote()) {
                String str = localeInfo.name + " server side";
                if (last) {
                    z = false;
                }
                textSettingsCell.setText(str, z);
                return;
            }
            str = localeInfo.name;
            if (last) {
                z = false;
            }
            textSettingsCell.setText(str, z);
        }

        public int getItemViewType(int i) {
            return 0;
        }
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
                LocaleInfo localeInfo = null;
                if (LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                    if (position >= 0 && position < LanguageSelectActivity.this.searchResult.size()) {
                        localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(position);
                    }
                } else if (position >= 0 && position < LocaleController.getInstance().sortedLanguages.size()) {
                    localeInfo = (LocaleInfo) LocaleController.getInstance().sortedLanguages.get(position);
                }
                if (localeInfo != null) {
                    LocaleController.getInstance().applyLanguage(localeInfo, true);
                    LanguageSelectActivity.this.parentLayout.rebuildAllFragmentViews(false);
                }
                LanguageSelectActivity.this.finishFragment();
            }
        });
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemClick(View view, int position) {
                LocaleInfo localeInfo = null;
                if (LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                    if (position >= 0 && position < LanguageSelectActivity.this.searchResult.size()) {
                        localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(position);
                    }
                } else if (position >= 0 && position < LocaleController.getInstance().sortedLanguages.size()) {
                    localeInfo = (LocaleInfo) LocaleController.getInstance().sortedLanguages.get(position);
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
                        if (LocaleController.getInstance().deleteLanguage(finalLocaleInfo)) {
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
                Iterator it = LocaleController.getInstance().sortedLanguages.iterator();
                while (it.hasNext()) {
                    LocaleInfo c = (LocaleInfo) it.next();
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
        r13 = new ThemeDescription[12];
        r13[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r13[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r13[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r13[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r13[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r13[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r13[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        r13[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        r13[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r13[9] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        int i = 0;
        r13[10] = new ThemeDescription(this.listView, i, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        i = 0;
        r13[11] = new ThemeDescription(this.listView, i, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        return r13;
    }
}
