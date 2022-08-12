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
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
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
import org.telegram.messenger.R;
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

    public boolean hasForceLightStatusBar() {
        return true;
    }

    public CountrySelectActivity(boolean z) {
        this(z, (ArrayList<Country>) null);
    }

    public CountrySelectActivity(boolean z, ArrayList<Country> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            this.existingCountries = new ArrayList<>(arrayList);
        }
        this.needPhoneCode = z;
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setTitle(LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
        this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    CountrySelectActivity.this.finishFragment();
                }
            }
        });
        int i = 1;
        this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
                String obj = editText.getText().toString();
                if (TextUtils.isEmpty(obj)) {
                    CountrySelectActivity.this.searchListViewAdapter.search((String) null);
                    boolean unused = CountrySelectActivity.this.searchWas = false;
                    CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.listViewAdapter);
                    CountrySelectActivity.this.listView.setFastScrollVisible(true);
                    return;
                }
                CountrySelectActivity.this.searchListViewAdapter.search(obj);
                if (obj.length() != 0) {
                    boolean unused2 = CountrySelectActivity.this.searchWas = true;
                }
            }
        }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.actionBar.setSearchTextColor(Theme.getColor("windowBackgroundWhiteGrayText"), true);
        this.actionBar.setSearchTextColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setSearchCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.searching = false;
        this.searchWas = false;
        CountryAdapter countryAdapter = new CountryAdapter(context, this.existingCountries);
        this.listViewAdapter = countryAdapter;
        this.searchListViewAdapter = new CountrySearchAdapter(context, countryAdapter.getCountries());
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showTextView();
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
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
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new CountrySelectActivity$$ExternalSyntheticLambda0(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(CountrySelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view, int i) {
        Country country;
        CountrySelectActivityDelegate countrySelectActivityDelegate;
        if (!this.searching || !this.searchWas) {
            int sectionForPosition = this.listViewAdapter.getSectionForPosition(i);
            int positionInSectionForPosition = this.listViewAdapter.getPositionInSectionForPosition(i);
            if (positionInSectionForPosition >= 0 && sectionForPosition >= 0) {
                country = this.listViewAdapter.getItem(sectionForPosition, positionInSectionForPosition);
            } else {
                return;
            }
        } else {
            country = this.searchListViewAdapter.getItem(i);
        }
        if (i >= 0) {
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

    public void setCountrySelectActivityDelegate(CountrySelectActivityDelegate countrySelectActivityDelegate) {
        this.delegate = countrySelectActivityDelegate;
    }

    public static class Country {
        public String code;
        public String name;
        public String shortname;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || Country.class != obj.getClass()) {
                return false;
            }
            Country country = (Country) obj;
            if (!ObjectsCompat$$ExternalSyntheticBackport0.m(this.name, country.name) || !ObjectsCompat$$ExternalSyntheticBackport0.m(this.code, country.code)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Arrays.hashCode(new Object[]{this.name, this.code});
        }
    }

    public class CountryAdapter extends RecyclerListView.SectionsAdapter {
        private HashMap<String, ArrayList<Country>> countries = new HashMap<>();
        private Context mContext;
        private ArrayList<String> sortedCountries = new ArrayList<>();

        public View getSectionHeaderView(int i, View view) {
            return null;
        }

        public CountryAdapter(Context context, ArrayList<Country> arrayList) {
            this.mContext = context;
            if (arrayList != null) {
                for (int i = 0; i < arrayList.size(); i++) {
                    Country country = arrayList.get(i);
                    String upperCase = country.name.substring(0, 1).toUpperCase();
                    ArrayList arrayList2 = this.countries.get(upperCase);
                    if (arrayList2 == null) {
                        arrayList2 = new ArrayList();
                        this.countries.put(upperCase, arrayList2);
                        this.sortedCountries.add(upperCase);
                    }
                    arrayList2.add(country);
                }
            } else {
                try {
                    InputStream open = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(open));
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        String[] split = readLine.split(";");
                        Country country2 = new Country();
                        String str = split[2];
                        country2.name = str;
                        country2.code = split[0];
                        country2.shortname = split[1];
                        String upperCase2 = str.substring(0, 1).toUpperCase();
                        ArrayList arrayList3 = this.countries.get(upperCase2);
                        if (arrayList3 == null) {
                            arrayList3 = new ArrayList();
                            this.countries.put(upperCase2, arrayList3);
                            this.sortedCountries.add(upperCase2);
                        }
                        arrayList3.add(country2);
                    }
                    bufferedReader.close();
                    open.close();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            Collections.sort(this.sortedCountries, CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda0.INSTANCE);
            for (ArrayList<Country> sort : this.countries.values()) {
                Collections.sort(sort, CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda1.INSTANCE);
            }
        }

        public HashMap<String, ArrayList<Country>> getCountries() {
            return this.countries;
        }

        public Country getItem(int i, int i2) {
            if (i >= 0 && i < this.sortedCountries.size()) {
                ArrayList arrayList = this.countries.get(this.sortedCountries.get(i));
                if (i2 >= 0 && i2 < arrayList.size()) {
                    return (Country) arrayList.get(i2);
                }
            }
            return null;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
            return i2 < this.countries.get(this.sortedCountries.get(i)).size();
        }

        public int getSectionCount() {
            return this.sortedCountries.size();
        }

        public int getCountForSection(int i) {
            int size = this.countries.get(this.sortedCountries.get(i)).size();
            return i != this.sortedCountries.size() + -1 ? size + 1 : size;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new DividerCell(this.mContext);
                view.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f));
            } else {
                view = CountrySelectActivity.createSettingsCell(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
            String str;
            if (viewHolder.getItemViewType() == 0) {
                Country country = (Country) this.countries.get(this.sortedCountries.get(i)).get(i2);
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                CharSequence replaceEmoji = Emoji.replaceEmoji(CountrySelectActivity.getCountryNameWithFlag(country), textSettingsCell.getTextView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                if (CountrySelectActivity.this.needPhoneCode) {
                    str = "+" + country.code;
                } else {
                    str = null;
                }
                textSettingsCell.setTextAndValue(replaceEmoji, str, false);
            }
        }

        public int getItemViewType(int i, int i2) {
            return i2 < this.countries.get(this.sortedCountries.get(i)).size() ? 0 : 1;
        }

        public String getLetter(int i) {
            int sectionForPosition = getSectionForPosition(i);
            if (sectionForPosition == -1) {
                sectionForPosition = this.sortedCountries.size() - 1;
            }
            return this.sortedCountries.get(sectionForPosition);
        }

        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            iArr[0] = (int) (((float) getItemCount()) * f);
            iArr[1] = 0;
        }
    }

    public class CountrySearchAdapter extends RecyclerListView.SelectionAdapter {
        private List<Country> countryList = new ArrayList();
        private Map<Country, List<String>> countrySearchMap = new HashMap();
        private Context mContext;
        private ArrayList<Country> searchResult;
        /* access modifiers changed from: private */
        public Timer searchTimer;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public CountrySearchAdapter(Context context, HashMap<String, ArrayList<Country>> hashMap) {
            this.mContext = context;
            for (ArrayList<Country> it : hashMap.values()) {
                for (Country country : it) {
                    this.countryList.add(country);
                    this.countrySearchMap.put(country, Arrays.asList(country.name.split(" ")));
                }
            }
        }

        public void search(final String str) {
            if (str == null) {
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
                    CountrySearchAdapter.this.processSearch(str);
                }
            }, 100, 300);
        }

        /* access modifiers changed from: private */
        public void processSearch(String str) {
            Utilities.searchQueue.postRunnable(new CountrySelectActivity$CountrySearchAdapter$$ExternalSyntheticLambda0(this, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$0(String str) {
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList());
                return;
            }
            ArrayList arrayList = new ArrayList();
            for (Country next : this.countryList) {
                Iterator it = this.countrySearchMap.get(next).iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (((String) it.next()).toLowerCase().startsWith(lowerCase)) {
                            arrayList.add(next);
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            updateSearchResults(arrayList);
        }

        private void updateSearchResults(ArrayList<Country> arrayList) {
            AndroidUtilities.runOnUIThread(new CountrySelectActivity$CountrySearchAdapter$$ExternalSyntheticLambda1(this, arrayList));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$1(ArrayList arrayList) {
            if (CountrySelectActivity.this.searching) {
                this.searchResult = arrayList;
                if (!(!CountrySelectActivity.this.searchWas || CountrySelectActivity.this.listView == null || CountrySelectActivity.this.listView.getAdapter() == CountrySelectActivity.this.searchListViewAdapter)) {
                    CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.searchListViewAdapter);
                    CountrySelectActivity.this.listView.setFastScrollVisible(false);
                }
                notifyDataSetChanged();
            }
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

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(CountrySelectActivity.createSettingsCell(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            Country country = this.searchResult.get(i);
            TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
            CharSequence replaceEmoji = Emoji.replaceEmoji(CountrySelectActivity.getCountryNameWithFlag(country), textSettingsCell.getTextView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (CountrySelectActivity.this.needPhoneCode) {
                str = "+" + country.code;
            } else {
                str = null;
            }
            textSettingsCell.setTextAndValue(replaceEmoji, str, false);
        }
    }

    /* access modifiers changed from: private */
    public static TextSettingsCell createSettingsCell(Context context) {
        TextSettingsCell textSettingsCell = new TextSettingsCell(context);
        float f = 16.0f;
        int dp = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 12.0f);
        if (LocaleController.isRTL) {
            f = 12.0f;
        }
        textSettingsCell.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
        textSettingsCell.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            private NotificationCenter.NotificationCenterDelegate listener;

            {
                this.listener = new CountrySelectActivity$4$$ExternalSyntheticLambda0(TextSettingsCell.this);
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$$0(TextSettingsCell textSettingsCell, int i, int i2, Object[] objArr) {
                if (i == NotificationCenter.emojiLoaded) {
                    textSettingsCell.getTextView().invalidate();
                }
            }

            public void onViewAttachedToWindow(View view) {
                NotificationCenter.getGlobalInstance().addObserver(this.listener, NotificationCenter.emojiLoaded);
            }

            public void onViewDetachedFromWindow(View view) {
                NotificationCenter.getGlobalInstance().removeObserver(this.listener, NotificationCenter.emojiLoaded);
            }
        });
        return textSettingsCell;
    }

    /* access modifiers changed from: private */
    public static CharSequence getCountryNameWithFlag(Country country) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String languageFlag = LocaleController.getLanguageFlag(country.shortname);
        if (languageFlag != null) {
            spannableStringBuilder.append(languageFlag).append(" ");
            spannableStringBuilder.setSpan(new ReplacementSpan() {
                public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
                }

                public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                    return AndroidUtilities.dp(16.0f);
                }
            }, languageFlag.length(), languageFlag.length() + 1, 0);
        }
        spannableStringBuilder.append(country.name);
        return spannableStringBuilder;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollActive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollInactive"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollText"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        return arrayList;
    }
}
