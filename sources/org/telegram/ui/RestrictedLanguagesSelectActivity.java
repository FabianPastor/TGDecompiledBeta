package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckbox2Cell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class RestrictedLanguagesSelectActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    /* access modifiers changed from: private */
    public SharedPreferences preferences;
    /* access modifiers changed from: private */
    public ListAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public ArrayList<LocaleController.LocaleInfo> searchResult;
    private Timer searchTimer;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public HashSet<String> selectedLanguages = null;
    /* access modifiers changed from: private */
    public ArrayList<LocaleController.LocaleInfo> sortedLanguages;

    public static HashSet<String> getRestrictedLanguages() {
        return new HashSet<>(MessagesController.getGlobalMainSettings().getStringSet("translate_button_restricted_languages", new HashSet()));
    }

    public boolean onFragmentCreate() {
        this.preferences = MessagesController.getGlobalMainSettings();
        this.selectedLanguages = getRestrictedLanguages();
        SharedPreferences sharedPreferences = this.preferences;
        AnonymousClass1 r1 = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public int langPos(String lng) {
                if (lng == null) {
                    return -1;
                }
                ArrayList<LocaleController.LocaleInfo> arr = RestrictedLanguagesSelectActivity.this.searching ? RestrictedLanguagesSelectActivity.this.searchResult : RestrictedLanguagesSelectActivity.this.sortedLanguages;
                if (arr == null) {
                    return -1;
                }
                for (int i = 0; i < arr.size(); i++) {
                    if (lng.equals(arr.get(i).pluralLangCode)) {
                        return i;
                    }
                }
                return -1;
            }

            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                SharedPreferences unused = RestrictedLanguagesSelectActivity.this.preferences = sharedPreferences;
                HashSet<String> newSelectedLanguages = RestrictedLanguagesSelectActivity.getRestrictedLanguages();
                if (!(RestrictedLanguagesSelectActivity.this.listView == null || RestrictedLanguagesSelectActivity.this.listView.getAdapter() == null)) {
                    RecyclerView.Adapter adapter = RestrictedLanguagesSelectActivity.this.listView.getAdapter();
                    int offset = RestrictedLanguagesSelectActivity.this.searching ^ 1;
                    Iterator it = RestrictedLanguagesSelectActivity.this.selectedLanguages.iterator();
                    while (it.hasNext()) {
                        String lng = (String) it.next();
                        if (!newSelectedLanguages.contains(lng)) {
                            adapter.notifyItemChanged(langPos(lng) + offset);
                        }
                    }
                    Iterator<String> it2 = newSelectedLanguages.iterator();
                    while (it2.hasNext()) {
                        String lng2 = it2.next();
                        if (!RestrictedLanguagesSelectActivity.this.selectedLanguages.contains(lng2)) {
                            adapter.notifyItemChanged(langPos(lng2) + ((int) offset));
                        }
                    }
                }
                HashSet unused2 = RestrictedLanguagesSelectActivity.this.selectedLanguages = newSelectedLanguages;
            }
        };
        this.listener = r1;
        sharedPreferences.registerOnSharedPreferenceChangeListener(r1);
        fillLanguages();
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.preferences.unregisterOnSharedPreferenceChangeListener(this.listener);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("DoNotTranslate", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    RestrictedLanguagesSelectActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = RestrictedLanguagesSelectActivity.this.searching = true;
            }

            public void onSearchCollapse() {
                RestrictedLanguagesSelectActivity.this.search((String) null);
                boolean unused = RestrictedLanguagesSelectActivity.this.searching = false;
                boolean unused2 = RestrictedLanguagesSelectActivity.this.searchWas = false;
                if (RestrictedLanguagesSelectActivity.this.listView != null) {
                    RestrictedLanguagesSelectActivity.this.emptyView.setVisibility(8);
                    RestrictedLanguagesSelectActivity.this.listView.setAdapter(RestrictedLanguagesSelectActivity.this.listAdapter);
                }
            }

            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                RestrictedLanguagesSelectActivity.this.search(text);
                if (text.length() != 0) {
                    boolean unused = RestrictedLanguagesSelectActivity.this.searchWas = true;
                    if (RestrictedLanguagesSelectActivity.this.listView != null) {
                        RestrictedLanguagesSelectActivity.this.listView.setAdapter(RestrictedLanguagesSelectActivity.this.searchListViewAdapter);
                        return;
                    }
                    return;
                }
                boolean unused2 = RestrictedLanguagesSelectActivity.this.searching = false;
                boolean unused3 = RestrictedLanguagesSelectActivity.this.searchWas = false;
                if (RestrictedLanguagesSelectActivity.this.listView != null) {
                    RestrictedLanguagesSelectActivity.this.emptyView.setVisibility(8);
                    RestrictedLanguagesSelectActivity.this.listView.setAdapter(RestrictedLanguagesSelectActivity.this.listAdapter);
                }
            }
        }).setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.listAdapter = new ListAdapter(context, false);
        this.searchListViewAdapter = new ListAdapter(context, true);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.showTextView();
        this.emptyView.setShowAtCenter(true);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda4(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda5(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(RestrictedLanguagesSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-RestrictedLanguagesSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3210xb6e1babc(View view, int position) {
        LocaleController.LocaleInfo localeInfo;
        if (getParentActivity() != null && this.parentLayout != null && (view instanceof TextCheckbox2Cell)) {
            boolean search = this.listView.getAdapter() == this.searchListViewAdapter;
            if (!search) {
                position--;
            }
            if (search) {
                localeInfo = this.searchResult.get(position);
            } else {
                localeInfo = this.sortedLanguages.get(position);
            }
            if (localeInfo != null) {
                LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
                String langCode = localeInfo.pluralLangCode;
                if (langCode == null || !langCode.equals(currentLocaleInfo.pluralLangCode)) {
                    boolean value = this.selectedLanguages.contains(langCode);
                    HashSet<String> newSelectedLanguages = new HashSet<>(this.selectedLanguages);
                    if (value) {
                        Collection.EL.removeIf(newSelectedLanguages, new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda3(langCode));
                    } else {
                        newSelectedLanguages.add(langCode);
                    }
                    if (newSelectedLanguages.size() != 1 || !newSelectedLanguages.contains(currentLocaleInfo.pluralLangCode)) {
                        this.preferences.edit().putStringSet("translate_button_restricted_languages", newSelectedLanguages).apply();
                    } else {
                        this.preferences.edit().remove("translate_button_restricted_languages").apply();
                    }
                } else {
                    AndroidUtilities.shakeView(((TextCheckbox2Cell) view).checkbox, 2.0f, 0);
                }
            }
        }
    }

    static /* synthetic */ boolean lambda$createView$0(String langCode, String s) {
        return s != null && s.equals(langCode);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-RestrictedLanguagesSelectActivity  reason: not valid java name */
    public /* synthetic */ boolean m3212x3df7var_e(View view, int position) {
        LocaleController.LocaleInfo localeInfo;
        if (getParentActivity() == null || this.parentLayout == null || !(view instanceof TextCheckbox2Cell)) {
            return false;
        }
        boolean search = this.listView.getAdapter() == this.searchListViewAdapter;
        if (!search) {
            position--;
        }
        if (search) {
            localeInfo = this.searchResult.get(position);
        } else {
            localeInfo = this.sortedLanguages.get(position);
        }
        if (localeInfo == null || localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("DeleteLocalizationTitle", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DeleteLocalizationText", NUM, localeInfo.name)));
        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda0(this, localeInfo));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
        return true;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-RestrictedLanguagesSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3211xfa6cd87d(LocaleController.LocaleInfo finalLocaleInfo, DialogInterface dialogInterface, int i) {
        if (LocaleController.getInstance().deleteLanguage(finalLocaleInfo, this.currentAccount)) {
            fillLanguages();
            ArrayList<LocaleController.LocaleInfo> arrayList = this.searchResult;
            if (arrayList != null) {
                arrayList.remove(finalLocaleInfo);
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
            ListAdapter listAdapter3 = this.searchListViewAdapter;
            if (listAdapter3 != null) {
                listAdapter3.notifyDataSetChanged();
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.suggestedLangpack && this.listAdapter != null) {
            fillLanguages();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void fillLanguages() {
        Comparator<LocaleController.LocaleInfo> comparator = new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda2(LocaleController.getInstance().getCurrentLocaleInfo());
        this.sortedLanguages = new ArrayList<>();
        ArrayList<LocaleController.LocaleInfo> arrayList = LocaleController.getInstance().languages;
        int size = arrayList.size();
        for (int a = 0; a < size; a++) {
            LocaleController.LocaleInfo info = arrayList.get(a);
            if (!(info == null || info.serverIndex == Integer.MAX_VALUE)) {
                this.sortedLanguages.add(info);
            }
        }
        Collections.sort(this.sortedLanguages, comparator);
    }

    static /* synthetic */ int lambda$fillLanguages$4(LocaleController.LocaleInfo currentLocale, LocaleController.LocaleInfo o, LocaleController.LocaleInfo o2) {
        if (o == currentLocale) {
            return -1;
        }
        if (o2 == currentLocale) {
            return 1;
        }
        if (o.serverIndex == o2.serverIndex) {
            return o.name.compareTo(o2.name);
        }
        if (o.serverIndex > o2.serverIndex) {
            return 1;
        }
        if (o.serverIndex < o2.serverIndex) {
            return -1;
        }
        return 0;
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void search(String query) {
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
        processSearch(query);
    }

    private void processSearch(String query) {
        if (query.trim().toLowerCase().length() == 0) {
            updateSearchResults(new ArrayList());
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList<LocaleController.LocaleInfo> resultArray = new ArrayList<>();
        int N = this.sortedLanguages.size();
        for (int a = 0; a < N; a++) {
            LocaleController.LocaleInfo c = this.sortedLanguages.get(a);
            if (c.name.toLowerCase().startsWith(query) || c.nameEnglish.toLowerCase().startsWith(query)) {
                resultArray.add(c);
            }
        }
        updateSearchResults(resultArray);
    }

    private void updateSearchResults(ArrayList<LocaleController.LocaleInfo> arrCounties) {
        AndroidUtilities.runOnUIThread(new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda1(this, arrCounties));
    }

    /* renamed from: lambda$updateSearchResults$5$org-telegram-ui-RestrictedLanguagesSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3213x7d99288a(ArrayList arrCounties) {
        this.searchResult = arrCounties;
        this.searchListViewAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private boolean search;

        public ListAdapter(Context context, boolean isSearch) {
            this.mContext = context;
            this.search = isSearch;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            if (!this.search) {
                return RestrictedLanguagesSelectActivity.this.sortedLanguages.size() + 1;
            }
            if (RestrictedLanguagesSelectActivity.this.searchResult == null) {
                return 0;
            }
            return RestrictedLanguagesSelectActivity.this.searchResult.size();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new TextCheckbox2Cell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 2:
                    HeaderCell header = new HeaderCell(this.mContext);
                    header.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    header.setText(LocaleController.getString("ChooseLanguages", NUM));
                    view = header;
                    break;
                default:
                    view = new ShadowSectionCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean last;
            LocaleController.LocaleInfo localeInfo;
            switch (holder.getItemViewType()) {
                case 0:
                    if (!this.search) {
                        position--;
                    }
                    TextCheckbox2Cell textSettingsCell = (TextCheckbox2Cell) holder.itemView;
                    boolean z = false;
                    if (this.search) {
                        localeInfo = (LocaleController.LocaleInfo) RestrictedLanguagesSelectActivity.this.searchResult.get(position);
                        last = position == RestrictedLanguagesSelectActivity.this.searchResult.size() - 1;
                    } else {
                        localeInfo = (LocaleController.LocaleInfo) RestrictedLanguagesSelectActivity.this.sortedLanguages.get(position);
                        last = position == RestrictedLanguagesSelectActivity.this.sortedLanguages.size() - 1;
                    }
                    String langCode = localeInfo.pluralLangCode;
                    boolean value = RestrictedLanguagesSelectActivity.this.selectedLanguages.contains(langCode);
                    if (localeInfo.isLocal()) {
                        textSettingsCell.setTextAndValue(String.format("%1$s (%2$s)", new Object[]{localeInfo.name, LocaleController.getString("LanguageCustom", NUM)}), localeInfo.nameEnglish, false, !last);
                    } else {
                        textSettingsCell.setTextAndValue(localeInfo.name, localeInfo.nameEnglish, false, !last);
                    }
                    boolean isCurrent = langCode != null && langCode.equals(LocaleController.getInstance().getCurrentLocaleInfo().pluralLangCode);
                    if (value || isCurrent) {
                        z = true;
                    }
                    textSettingsCell.setChecked(z);
                    return;
                case 1:
                    if (!this.search) {
                        int position2 = position - 1;
                    }
                    ((ShadowSectionCell) holder.itemView).setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (!this.search) {
                i--;
            }
            if (i == -1) {
                return 2;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        return themeDescriptions;
    }
}
