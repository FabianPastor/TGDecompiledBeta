package org.telegram.p005ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.DividerCell;
import org.telegram.p005ui.Cells.LetterSectionCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SectionsAdapter;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;

/* renamed from: org.telegram.ui.CountrySelectActivity */
public class CountrySelectActivity extends BaseFragment {
    private CountrySelectActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private RecyclerListView listView;
    private CountryAdapter listViewAdapter;
    private boolean needPhoneCode;
    private CountrySearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;

    /* renamed from: org.telegram.ui.CountrySelectActivity$CountrySelectActivityDelegate */
    public interface CountrySelectActivityDelegate {
        void didSelectCountry(String str, String str2);
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                CountrySelectActivity.this.lambda$createView$1$PhotoAlbumPickerActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$2 */
    class CLASSNAME extends ActionBarMenuItemSearchListener {
        CLASSNAME() {
        }

        public void onSearchExpand() {
            CountrySelectActivity.this.searching = true;
        }

        public void onSearchCollapse() {
            CountrySelectActivity.this.searchListViewAdapter.search(null);
            CountrySelectActivity.this.searching = false;
            CountrySelectActivity.this.searchWas = false;
            CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.listViewAdapter);
            CountrySelectActivity.this.listView.setFastScrollVisible(true);
            CountrySelectActivity.this.emptyView.setText(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
        }

        public void onTextChanged(EditText editText) {
            String text = editText.getText().toString();
            CountrySelectActivity.this.searchListViewAdapter.search(text);
            if (text.length() != 0) {
                CountrySelectActivity.this.searchWas = true;
                if (CountrySelectActivity.this.listView != null) {
                    CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.searchListViewAdapter);
                    CountrySelectActivity.this.listView.setFastScrollVisible(false);
                }
                if (CountrySelectActivity.this.emptyView != null) {
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$3 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1 && CountrySelectActivity.this.searching && CountrySelectActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(CountrySelectActivity.this.getParentActivity().getCurrentFocus());
            }
        }
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$Country */
    public static class Country {
        public String code;
        public String name;
        public String shortname;
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$CountryAdapter */
    public class CountryAdapter extends SectionsAdapter {
        private HashMap<String, ArrayList<Country>> countries = new HashMap();
        private Context mContext;
        private ArrayList<String> sortedCountries = new ArrayList();

        public CountryAdapter(Context context) {
            ArrayList<Country> arr;
            this.mContext = context;
            try {
                InputStream stream = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] args = line.split(";");
                    Country c = new Country();
                    c.name = args[2];
                    c.code = args[0];
                    c.shortname = args[1];
                    String n = c.name.substring(0, 1).toUpperCase();
                    arr = (ArrayList) this.countries.get(n);
                    if (arr == null) {
                        arr = new ArrayList();
                        this.countries.put(n, arr);
                        this.sortedCountries.add(n);
                    }
                    arr.add(c);
                }
                reader.close();
                stream.close();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
            Collections.sort(this.sortedCountries, CountrySelectActivity$CountryAdapter$$Lambda$0.$instance);
            for (ArrayList<Country> arr2 : this.countries.values()) {
                Collections.sort(arr2, CountrySelectActivity$CountryAdapter$$Lambda$1.$instance);
            }
        }

        public HashMap<String, ArrayList<Country>> getCountries() {
            return this.countries;
        }

        public Country getItem(int section, int position) {
            if (section < 0 || section >= this.sortedCountries.size()) {
                return null;
            }
            ArrayList<Country> arr = (ArrayList) this.countries.get(this.sortedCountries.get(section));
            if (position < 0 || position >= arr.size()) {
                return null;
            }
            return (Country) arr.get(position);
        }

        public boolean isEnabled(int section, int row) {
            return row < ((ArrayList) this.countries.get(this.sortedCountries.get(section))).size();
        }

        public int getSectionCount() {
            return this.sortedCountries.size();
        }

        public int getCountForSection(int section) {
            int count = ((ArrayList) this.countries.get(this.sortedCountries.get(section))).size();
            if (section != this.sortedCountries.size() - 1) {
                return count + 1;
            }
            return count;
        }

        public View getSectionHeaderView(int section, View view) {
            if (view == null) {
                view = new LetterSectionCell(this.mContext);
                ((LetterSectionCell) view).setCellHeight(AndroidUtilities.m9dp(48.0f));
            }
            ((LetterSectionCell) view).setLetter(((String) this.sortedCountries.get(section)).toUpperCase());
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            float f = 72.0f;
            float f2 = 54.0f;
            int dp;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    dp = AndroidUtilities.m9dp(LocaleController.isRTL ? 16.0f : 54.0f);
                    if (!LocaleController.isRTL) {
                        f2 = 16.0f;
                    }
                    view.setPadding(dp, 0, AndroidUtilities.m9dp(f2), 0);
                    break;
                default:
                    float f3;
                    view = new DividerCell(this.mContext);
                    if (LocaleController.isRTL) {
                        f3 = 24.0f;
                    } else {
                        f3 = 72.0f;
                    }
                    dp = AndroidUtilities.m9dp(f3);
                    int dp2 = AndroidUtilities.m9dp(8.0f);
                    if (!LocaleController.isRTL) {
                        f = 24.0f;
                    }
                    view.setPadding(dp, dp2, AndroidUtilities.m9dp(f), AndroidUtilities.m9dp(8.0f));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(int section, int position, ViewHolder holder) {
            if (holder.getItemViewType() == 0) {
                Country c = (Country) ((ArrayList) this.countries.get(this.sortedCountries.get(section))).get(position);
                ((TextSettingsCell) holder.itemView).setTextAndValue(c.name, CountrySelectActivity.this.needPhoneCode ? "+" + c.code : null, false);
            }
        }

        public int getItemViewType(int section, int position) {
            return position < ((ArrayList) this.countries.get(this.sortedCountries.get(section))).size() ? 0 : 1;
        }

        public String getLetter(int position) {
            int section = getSectionForPosition(position);
            if (section == -1) {
                section = this.sortedCountries.size() - 1;
            }
            return (String) this.sortedCountries.get(section);
        }

        public int getPositionForScrollProgress(float progress) {
            return (int) (((float) getItemCount()) * progress);
        }
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$CountrySearchAdapter */
    public class CountrySearchAdapter extends SelectionAdapter {
        private HashMap<String, ArrayList<Country>> countries;
        private Context mContext;
        private ArrayList<Country> searchResult;
        private Timer searchTimer;

        public CountrySearchAdapter(Context context, HashMap<String, ArrayList<Country>> countries) {
            this.mContext = context;
            this.countries = countries;
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
                FileLog.m13e(e);
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        CountrySearchAdapter.this.searchTimer.cancel();
                        CountrySearchAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.m13e(e);
                    }
                    CountrySearchAdapter.this.processSearch(query);
                }
            }, 100, 300);
        }

        private void processSearch(String query) {
            Utilities.searchQueue.postRunnable(new CountrySelectActivity$CountrySearchAdapter$$Lambda$0(this, query));
        }

        /* renamed from: lambda$processSearch$0$CountrySelectActivity$CountrySearchAdapter */
        final /* synthetic */ void mo18273xa1825eb2(String query) {
            if (query.trim().toLowerCase().length() == 0) {
                updateSearchResults(new ArrayList());
                return;
            }
            ArrayList<Country> resultArray = new ArrayList();
            ArrayList<Country> arr = (ArrayList) this.countries.get(query.substring(0, 1).toUpperCase());
            if (arr != null) {
                Iterator it = arr.iterator();
                while (it.hasNext()) {
                    Country c = (Country) it.next();
                    if (c.name.toLowerCase().startsWith(query)) {
                        resultArray.add(c);
                    }
                }
            }
            updateSearchResults(resultArray);
        }

        private void updateSearchResults(ArrayList<Country> arrCounties) {
            AndroidUtilities.runOnUIThread(new CountrySelectActivity$CountrySearchAdapter$$Lambda$1(this, arrCounties));
        }

        /* renamed from: lambda$updateSearchResults$1$CountrySelectActivity$CountrySearchAdapter */
        final /* synthetic */ void mo18274xa883bb23(ArrayList arrCounties) {
            this.searchResult = arrCounties;
            notifyDataSetChanged();
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            if (this.searchResult == null) {
                return 0;
            }
            return this.searchResult.size();
        }

        public Country getItem(int i) {
            if (i < 0 || i >= this.searchResult.size()) {
                return null;
            }
            return (Country) this.searchResult.get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(new TextSettingsCell(this.mContext));
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            Country c = (Country) this.searchResult.get(position);
            ((TextSettingsCell) holder.itemView).setTextAndValue(c.name, CountrySelectActivity.this.needPhoneCode ? "+" + c.code : null, position != this.searchResult.size() + -1);
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public CountrySelectActivity(boolean phoneCode) {
        this.needPhoneCode = phoneCode;
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        int i = 1;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new CLASSNAME()).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.searching = false;
        this.searchWas = false;
        this.listViewAdapter = new CountryAdapter(context);
        this.searchListViewAdapter = new CountrySearchAdapter(context, this.listViewAdapter.getCountries());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showTextView();
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setSectionsType(1);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled();
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setAdapter(this.listViewAdapter);
        RecyclerListView recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new CountrySelectActivity$$Lambda$0(this));
        this.listView.setOnScrollListener(new CLASSNAME());
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$CountrySelectActivity(View view, int position) {
        Country country;
        if (this.searching && this.searchWas) {
            country = this.searchListViewAdapter.getItem(position);
        } else {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                country = this.listViewAdapter.getItem(section, row);
            } else {
                return;
            }
        }
        if (position >= 0) {
            lambda$createView$1$PhotoAlbumPickerActivity();
            if (country != null && this.delegate != null) {
                this.delegate.didSelectCountry(country.name, country.shortname);
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void setCountrySelectActivityDelegate(CountrySelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[17];
        r9[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive);
        r9[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive);
        r9[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText);
        r9[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r9[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return r9;
    }
}
