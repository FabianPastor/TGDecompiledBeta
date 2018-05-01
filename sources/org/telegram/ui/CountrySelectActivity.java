package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class CountrySelectActivity extends BaseFragment {
    private CountrySelectActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private RecyclerListView listView;
    private CountryAdapter listViewAdapter;
    private boolean needPhoneCode;
    private CountrySearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;

    public static class Country {
        public String code;
        public String name;
        public String shortname;
    }

    public interface CountrySelectActivityDelegate {
        void didSelectCountry(String str, String str2);
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$1 */
    class C21161 extends ActionBarMenuOnItemClick {
        C21161() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                CountrySelectActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$2 */
    class C21172 extends ActionBarMenuItemSearchListener {
        C21172() {
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
            CountrySelectActivity.this.emptyView.setText(LocaleController.getString("ChooseCountry", C0446R.string.ChooseCountry));
        }

        public void onTextChanged(EditText editText) {
            editText = editText.getText().toString();
            CountrySelectActivity.this.searchListViewAdapter.search(editText);
            if (editText.length() != null) {
                CountrySelectActivity.this.searchWas = true;
                if (CountrySelectActivity.this.listView != null) {
                    CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.searchListViewAdapter);
                    CountrySelectActivity.this.listView.setFastScrollVisible(false);
                }
                CountrySelectActivity.this.emptyView;
            }
        }
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$3 */
    class C21183 implements OnItemClickListener {
        C21183() {
        }

        public void onItemClick(View view, int i) {
            if (CountrySelectActivity.this.searching == null || CountrySelectActivity.this.searchWas == null) {
                int sectionForPosition = CountrySelectActivity.this.listViewAdapter.getSectionForPosition(i);
                int positionInSectionForPosition = CountrySelectActivity.this.listViewAdapter.getPositionInSectionForPosition(i);
                if (positionInSectionForPosition >= 0) {
                    if (sectionForPosition >= null) {
                        view = CountrySelectActivity.this.listViewAdapter.getItem(sectionForPosition, positionInSectionForPosition);
                    }
                }
                return;
            }
            view = CountrySelectActivity.this.searchListViewAdapter.getItem(i);
            if (i >= 0) {
                CountrySelectActivity.this.finishFragment();
                if (!(view == null || CountrySelectActivity.this.delegate == 0)) {
                    CountrySelectActivity.this.delegate.didSelectCountry(view.name, view.shortname);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.CountrySelectActivity$4 */
    class C21194 extends OnScrollListener {
        C21194() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1 && CountrySelectActivity.this.searching != null && CountrySelectActivity.this.searchWas != null) {
                AndroidUtilities.hideKeyboard(CountrySelectActivity.this.getParentActivity().getCurrentFocus());
            }
        }
    }

    public class CountrySearchAdapter extends SelectionAdapter {
        private HashMap<String, ArrayList<Country>> countries;
        private Context mContext;
        private ArrayList<Country> searchResult;
        private Timer searchTimer;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public CountrySearchAdapter(Context context, HashMap<String, ArrayList<Country>> hashMap) {
            this.mContext = context;
            this.countries = hashMap;
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
                        CountrySearchAdapter.this.searchTimer.cancel();
                        CountrySearchAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    CountrySearchAdapter.this.processSearch(str);
                }
            }, 100, 300);
        }

        private void processSearch(final String str) {
            Utilities.searchQueue.postRunnable(new Runnable() {
                public void run() {
                    if (str.trim().toLowerCase().length() == 0) {
                        CountrySearchAdapter.this.updateSearchResults(new ArrayList());
                        return;
                    }
                    ArrayList arrayList = new ArrayList();
                    ArrayList arrayList2 = (ArrayList) CountrySearchAdapter.this.countries.get(str.substring(0, 1).toUpperCase());
                    if (arrayList2 != null) {
                        Iterator it = arrayList2.iterator();
                        while (it.hasNext()) {
                            Country country = (Country) it.next();
                            if (country.name.toLowerCase().startsWith(str)) {
                                arrayList.add(country);
                            }
                        }
                    }
                    CountrySearchAdapter.this.updateSearchResults(arrayList);
                }
            });
        }

        private void updateSearchResults(final ArrayList<Country> arrayList) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    CountrySearchAdapter.this.searchResult = arrayList;
                    CountrySearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public int getItemCount() {
            if (this.searchResult == null) {
                return 0;
            }
            return this.searchResult.size();
        }

        public Country getItem(int i) {
            if (i >= 0) {
                if (i < this.searchResult.size()) {
                    return (Country) this.searchResult.get(i);
                }
            }
            return 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new TextSettingsCell(this.mContext));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            String stringBuilder;
            Country country = (Country) this.searchResult.get(i);
            TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
            String str = country.name;
            if (CountrySelectActivity.this.needPhoneCode) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("+");
                stringBuilder2.append(country.code);
                stringBuilder = stringBuilder2.toString();
            } else {
                stringBuilder = null;
            }
            boolean z = true;
            if (i == this.searchResult.size() - 1) {
                z = false;
            }
            textSettingsCell.setTextAndValue(str, stringBuilder, z);
        }
    }

    public class CountryAdapter extends SectionsAdapter {
        private HashMap<String, ArrayList<Country>> countries = new HashMap();
        private Context mContext;
        private ArrayList<String> sortedCountries = new ArrayList();

        public CountryAdapter(Context context) {
            this.mContext = context;
            try {
                context = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    String[] split = readLine.split(";");
                    Country country = new Country();
                    country.name = split[2];
                    country.code = split[0];
                    country.shortname = split[1];
                    readLine = country.name.substring(0, 1).toUpperCase();
                    ArrayList arrayList = (ArrayList) this.countries.get(readLine);
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                        this.countries.put(readLine, arrayList);
                        this.sortedCountries.add(readLine);
                    }
                    arrayList.add(country);
                }
                bufferedReader.close();
                context.close();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            Collections.sort(this.sortedCountries, new Comparator<String>(CountrySelectActivity.this) {
                public int compare(String str, String str2) {
                    return str.compareTo(str2);
                }
            });
            for (ArrayList sort : this.countries.values()) {
                Collections.sort(sort, new Comparator<Country>(CountrySelectActivity.this) {
                    public int compare(Country country, Country country2) {
                        return country.name.compareTo(country2.name);
                    }
                });
            }
        }

        public HashMap<String, ArrayList<Country>> getCountries() {
            return this.countries;
        }

        public Country getItem(int i, int i2) {
            if (i >= 0) {
                if (i < this.sortedCountries.size()) {
                    ArrayList arrayList = (ArrayList) this.countries.get(this.sortedCountries.get(i));
                    if (i2 >= 0) {
                        if (i2 < arrayList.size()) {
                            return (Country) arrayList.get(i2);
                        }
                    }
                    return null;
                }
            }
            return null;
        }

        public boolean isEnabled(int i, int i2) {
            return i2 < ((ArrayList) this.countries.get(this.sortedCountries.get(i))).size();
        }

        public int getSectionCount() {
            return this.sortedCountries.size();
        }

        public int getCountForSection(int i) {
            int size = ((ArrayList) this.countries.get(this.sortedCountries.get(i))).size();
            return i != this.sortedCountries.size() + -1 ? size + 1 : size;
        }

        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new LetterSectionCell(this.mContext);
                ((LetterSectionCell) view).setCellHeight(AndroidUtilities.dp(48.0f));
            }
            ((LetterSectionCell) view).setLetter(((String) this.sortedCountries.get(i)).toUpperCase());
            return view;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            float f;
            int dp;
            if (i != 0) {
                i = new DividerCell(this.mContext);
                f = 72.0f;
                dp = AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : 72.0f);
                if (!LocaleController.isRTL) {
                    f = 24.0f;
                }
                i.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
            } else {
                i = new TextSettingsCell(this.mContext);
                f = 54.0f;
                dp = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 54.0f);
                if (!LocaleController.isRTL) {
                    f = 16.0f;
                }
                i.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
            }
            return new Holder(i);
        }

        public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 0) {
                Country country = (Country) ((ArrayList) this.countries.get(this.sortedCountries.get(i))).get(i2);
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                viewHolder = country.name;
                if (CountrySelectActivity.this.needPhoneCode) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(country.code);
                    i = stringBuilder.toString();
                } else {
                    i = 0;
                }
                textSettingsCell.setTextAndValue(viewHolder, i, false);
            }
        }

        public int getItemViewType(int i, int i2) {
            return i2 < ((ArrayList) this.countries.get(this.sortedCountries.get(i))).size() ? 0 : 1;
        }

        public String getLetter(int i) {
            i = getSectionForPosition(i);
            if (i == -1) {
                i = this.sortedCountries.size() - 1;
            }
            return (String) this.sortedCountries.get(i);
        }

        public int getPositionForScrollProgress(float f) {
            return (int) (((float) getItemCount()) * f);
        }
    }

    public CountrySelectActivity(boolean z) {
        this.needPhoneCode = z;
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ChooseCountry", C0446R.string.ChooseCountry));
        this.actionBar.setActionBarMenuOnItemClick(new C21161());
        this.actionBar.createMenu().addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C21172()).getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        this.searching = false;
        this.searchWas = false;
        this.listViewAdapter = new CountryAdapter(context);
        this.searchListViewAdapter = new CountrySearchAdapter(context, this.listViewAdapter.getCountries());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showTextView();
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setSectionsType(1);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled();
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setAdapter(this.listViewAdapter);
        context = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        context.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C21183());
        this.listView.setOnScrollListener(new C21194());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void setCountrySelectActivityDelegate(CountrySelectActivityDelegate countrySelectActivityDelegate) {
        this.delegate = countrySelectActivityDelegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[17];
        r1[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive);
        r1[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive);
        r1[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText);
        r1[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r1[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return r1;
    }
}
