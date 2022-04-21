package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class CountrySelectActivity extends BaseFragment {
    private CountrySelectActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private ArrayList<Country> existingCountries;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public CountryAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean needPhoneCode;
    /* access modifiers changed from: private */
    public CountrySearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;

    public interface CountrySelectActivityDelegate {
        void didSelectCountry(Country country);
    }

    public CountrySelectActivity(boolean phoneCode) {
        this(phoneCode, (ArrayList<Country>) null);
    }

    public CountrySelectActivity(boolean phoneCode, ArrayList<Country> existingCountries2) {
        if (existingCountries2 != null && !existingCountries2.isEmpty()) {
            this.existingCountries = new ArrayList<>(existingCountries2);
        }
        this.needPhoneCode = phoneCode;
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public boolean hasForceLightStatusBar() {
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setTitle(LocaleController.getString("ChooseCountry", NUM));
        this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CountrySelectActivity.this.finishFragment();
                }
            }
        });
        int i = 1;
        this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = CountrySelectActivity.this.searching = true;
            }

            public void onSearchCollapse() {
                CountrySelectActivity.this.searchListViewAdapter.search((String) null);
                boolean unused = CountrySelectActivity.this.searching = false;
                boolean unused2 = CountrySelectActivity.this.searchWas = false;
                CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.listViewAdapter);
                CountrySelectActivity.this.listView.setFastScrollVisible(true);
            }

            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    CountrySelectActivity.this.searchListViewAdapter.search((String) null);
                    boolean unused = CountrySelectActivity.this.searchWas = false;
                    CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.listViewAdapter);
                    CountrySelectActivity.this.listView.setFastScrollVisible(true);
                    return;
                }
                CountrySelectActivity.this.searchListViewAdapter.search(text);
                if (text.length() != 0) {
                    boolean unused2 = CountrySelectActivity.this.searchWas = true;
                }
            }
        }).setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.actionBar.setSearchTextColor(Theme.getColor("windowBackgroundWhiteGrayText"), true);
        this.actionBar.setSearchTextColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setSearchCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.searching = false;
        this.searchWas = false;
        this.listViewAdapter = new CountryAdapter(context, this.existingCountries);
        this.searchListViewAdapter = new CountrySearchAdapter(context, this.listViewAdapter.getCountries());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoResult", NUM));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setSectionsType(3);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled(0);
        this.listView.setFastScrollVisible(true);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setAdapter(this.listViewAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new CountrySelectActivity$$ExternalSyntheticLambda0(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(CountrySelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-CountrySelectActivity  reason: not valid java name */
    public /* synthetic */ void m2049lambda$createView$0$orgtelegramuiCountrySelectActivity(View view, int position) {
        Country country;
        CountrySelectActivityDelegate countrySelectActivityDelegate;
        if (!this.searching || !this.searchWas) {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                country = this.listViewAdapter.getItem(section, row);
            } else {
                return;
            }
        } else {
            country = this.searchListViewAdapter.getItem(position);
        }
        if (position >= 0) {
            finishFragment();
            if (country != null && (countrySelectActivityDelegate = this.delegate) != null) {
                countrySelectActivityDelegate.didSelectCountry(country);
            }
        }
    }

    public void onResume() {
        super.onResume();
        CountryAdapter countryAdapter = this.listViewAdapter;
        if (countryAdapter != null) {
            countryAdapter.notifyDataSetChanged();
        }
    }

    public void setCountrySelectActivityDelegate(CountrySelectActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public static class Country {
        public String code;
        public String name;
        public String shortname;

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Country that = (Country) o;
            if (!ColorUtils$$ExternalSyntheticBackport0.m(this.name, that.name) || !ColorUtils$$ExternalSyntheticBackport0.m(this.code, that.code)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Arrays.hashCode(new Object[]{this.name, this.code});
        }
    }

    public class CountryAdapter extends RecyclerListView.SectionsAdapter {
        private static final int TYPE_COUNTRY = 0;
        private static final int TYPE_DIVIDER = 1;
        private HashMap<String, ArrayList<Country>> countries = new HashMap<>();
        private Context mContext;
        private ArrayList<String> sortedCountries = new ArrayList<>();

        public CountryAdapter(Context context, ArrayList<Country> exisitingCountries) {
            this.mContext = context;
            if (exisitingCountries != null) {
                for (int i = 0; i < exisitingCountries.size(); i++) {
                    Country c = exisitingCountries.get(i);
                    String n = c.name.substring(0, 1).toUpperCase();
                    ArrayList<Country> arr = this.countries.get(n);
                    if (arr == null) {
                        arr = new ArrayList<>();
                        this.countries.put(n, arr);
                        this.sortedCountries.add(n);
                    }
                    arr.add(c);
                }
            } else {
                try {
                    InputStream stream = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    while (true) {
                        String readLine = reader.readLine();
                        String line = readLine;
                        if (readLine == null) {
                            break;
                        }
                        String[] args = line.split(";");
                        Country c2 = new Country();
                        c2.name = args[2];
                        c2.code = args[0];
                        c2.shortname = args[1];
                        String n2 = c2.name.substring(0, 1).toUpperCase();
                        ArrayList<Country> arr2 = this.countries.get(n2);
                        if (arr2 == null) {
                            arr2 = new ArrayList<>();
                            this.countries.put(n2, arr2);
                            this.sortedCountries.add(n2);
                        }
                        arr2.add(c2);
                    }
                    reader.close();
                    stream.close();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            Collections.sort(this.sortedCountries, ChangePhoneActivity$PhoneView$$ExternalSyntheticLambda6.INSTANCE);
            for (ArrayList<Country> arr3 : this.countries.values()) {
                Collections.sort(arr3, CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0.INSTANCE);
            }
        }

        public HashMap<String, ArrayList<Country>> getCountries() {
            return this.countries;
        }

        public Country getItem(int section, int position) {
            if (section < 0 || section >= this.sortedCountries.size()) {
                return null;
            }
            ArrayList<Country> arr = this.countries.get(this.sortedCountries.get(section));
            if (position < 0 || position >= arr.size()) {
                return null;
            }
            return arr.get(position);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
            return row < this.countries.get(this.sortedCountries.get(section)).size();
        }

        public int getSectionCount() {
            return this.sortedCountries.size();
        }

        public int getCountForSection(int section) {
            int count = this.countries.get(this.sortedCountries.get(section)).size();
            if (section != this.sortedCountries.size() - 1) {
                return count + 1;
            }
            return count;
        }

        public View getSectionHeaderView(int section, View view) {
            return null;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = CountrySelectActivity.createSettingsCell(this.mContext);
                    break;
                default:
                    view = new DividerCell(this.mContext);
                    view.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
            String str;
            if (holder.getItemViewType() == 0) {
                Country c = this.countries.get(this.sortedCountries.get(section)).get(position);
                TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
                CharSequence replaceEmoji = Emoji.replaceEmoji(CountrySelectActivity.getCountryNameWithFlag(c), settingsCell.getTextView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                if (CountrySelectActivity.this.needPhoneCode) {
                    str = "+" + c.code;
                } else {
                    str = null;
                }
                settingsCell.setTextAndValue(replaceEmoji, str, false);
            }
        }

        public int getItemViewType(int section, int position) {
            return position < this.countries.get(this.sortedCountries.get(section)).size() ? 0 : 1;
        }

        public String getLetter(int position) {
            int section = getSectionForPosition(position);
            if (section == -1) {
                section = this.sortedCountries.size() - 1;
            }
            return this.sortedCountries.get(section);
        }

        public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
            position[0] = (int) (((float) getItemCount()) * progress);
            position[1] = 0;
        }
    }

    public class CountrySearchAdapter extends RecyclerListView.SelectionAdapter {
        private List<Country> countryList = new ArrayList();
        private Map<Country, List<String>> countrySearchMap = new HashMap();
        private Context mContext;
        private ArrayList<Country> searchResult;
        /* access modifiers changed from: private */
        public Timer searchTimer;

        public CountrySearchAdapter(Context context, HashMap<String, ArrayList<Country>> countries) {
            this.mContext = context;
            for (List<Country> list : countries.values()) {
                for (Country country : list) {
                    this.countryList.add(country);
                    this.countrySearchMap.put(country, Arrays.asList(country.name.split(" ")));
                }
            }
        }

        public void search(final String query) {
            if (query == null) {
                this.searchResult = null;
                return;
            }
            try {
                Timer timer = this.searchTimer;
                if (timer != null) {
                    timer.cancel();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            Timer timer2 = new Timer();
            this.searchTimer = timer2;
            timer2.schedule(new TimerTask() {
                public void run() {
                    try {
                        CountrySearchAdapter.this.searchTimer.cancel();
                        Timer unused = CountrySearchAdapter.this.searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    CountrySearchAdapter.this.processSearch(query);
                }
            }, 100, 300);
        }

        /* access modifiers changed from: private */
        public void processSearch(String query) {
            Utilities.searchQueue.postRunnable(new CountrySelectActivity$CountrySearchAdapter$$ExternalSyntheticLambda0(this, query));
        }

        /* renamed from: lambda$processSearch$0$org-telegram-ui-CountrySelectActivity$CountrySearchAdapter  reason: not valid java name */
        public /* synthetic */ void m2050xf9a8c8c8(String query) {
            String q = query.trim().toLowerCase();
            if (q.length() == 0) {
                updateSearchResults(new ArrayList());
                return;
            }
            ArrayList<Country> resultArray = new ArrayList<>();
            for (Country country : this.countryList) {
                Iterator it = this.countrySearchMap.get(country).iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (((String) it.next()).toLowerCase().startsWith(q)) {
                            resultArray.add(country);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            updateSearchResults(resultArray);
        }

        private void updateSearchResults(ArrayList<Country> arrCounties) {
            AndroidUtilities.runOnUIThread(new CountrySelectActivity$CountrySearchAdapter$$ExternalSyntheticLambda1(this, arrCounties));
        }

        /* renamed from: lambda$updateSearchResults$1$org-telegram-ui-CountrySelectActivity$CountrySearchAdapter  reason: not valid java name */
        public /* synthetic */ void m2051x856d2339(ArrayList arrCounties) {
            if (CountrySelectActivity.this.searching) {
                this.searchResult = arrCounties;
                if (!(!CountrySelectActivity.this.searchWas || CountrySelectActivity.this.listView == null || CountrySelectActivity.this.listView.getAdapter() == CountrySelectActivity.this.searchListViewAdapter)) {
                    CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.searchListViewAdapter);
                    CountrySelectActivity.this.listView.setFastScrollVisible(false);
                }
                notifyDataSetChanged();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            ArrayList<Country> arrayList = this.searchResult;
            if (arrayList == null) {
                return 0;
            }
            return arrayList.size();
        }

        public Country getItem(int i) {
            if (i < 0 || i >= this.searchResult.size()) {
                return null;
            }
            return this.searchResult.get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(CountrySelectActivity.createSettingsCell(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String str;
            Country c = this.searchResult.get(position);
            TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
            CharSequence replaceEmoji = Emoji.replaceEmoji(CountrySelectActivity.getCountryNameWithFlag(c), settingsCell.getTextView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (CountrySelectActivity.this.needPhoneCode) {
                str = "+" + c.code;
            } else {
                str = null;
            }
            settingsCell.setTextAndValue(replaceEmoji, str, false);
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public static TextSettingsCell createSettingsCell(Context context) {
        TextSettingsCell view = new TextSettingsCell(context);
        float f = 16.0f;
        int dp = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 12.0f);
        if (LocaleController.isRTL) {
            f = 12.0f;
        }
        view.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            private NotificationCenter.NotificationCenterDelegate listener;

            {
                this.listener = new CountrySelectActivity$4$$ExternalSyntheticLambda0(TextSettingsCell.this);
            }

            static /* synthetic */ void lambda$$0(TextSettingsCell view, int id, int account, Object[] args) {
                if (id == NotificationCenter.emojiLoaded) {
                    view.getTextView().invalidate();
                }
            }

            public void onViewAttachedToWindow(View v) {
                NotificationCenter.getGlobalInstance().addObserver(this.listener, NotificationCenter.emojiLoaded);
            }

            public void onViewDetachedFromWindow(View v) {
                NotificationCenter.getGlobalInstance().removeObserver(this.listener, NotificationCenter.emojiLoaded);
            }
        });
        return view;
    }

    /* access modifiers changed from: private */
    public static CharSequence getCountryNameWithFlag(Country c) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        String flag = LocaleController.getLanguageFlag(c.shortname);
        if (flag != null) {
            sb.append(flag).append(" ");
            sb.setSpan(new ReplacementSpan() {
                public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
                    return AndroidUtilities.dp(16.0f);
                }

                public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                }
            }, flag.length(), flag.length() + 1, 0);
        }
        sb.append(c.name);
        return sb;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollActive"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollInactive"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollText"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        return themeDescriptions;
    }
}
