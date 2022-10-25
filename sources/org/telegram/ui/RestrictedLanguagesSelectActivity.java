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
import j$.util.Collection$EL;
import j$.util.function.Predicate;
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
import org.telegram.messenger.R;
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
/* loaded from: classes3.dex */
public class RestrictedLanguagesSelectActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private EmptyTextProgressView emptyView;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences preferences;
    private ListAdapter searchListViewAdapter;
    private ArrayList<LocaleController.LocaleInfo> searchResult;
    private Timer searchTimer;
    private boolean searchWas;
    private boolean searching;
    private HashSet<String> selectedLanguages = null;
    private ArrayList<LocaleController.LocaleInfo> sortedLanguages;

    public static HashSet<String> getRestrictedLanguages() {
        return new HashSet<>(MessagesController.getGlobalMainSettings().getStringSet("translate_button_restricted_languages", new HashSet()));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        this.preferences = MessagesController.getGlobalMainSettings();
        this.selectedLanguages = getRestrictedLanguages();
        SharedPreferences sharedPreferences = this.preferences;
        SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity.1
            public int langPos(String str) {
                if (str == null) {
                    return -1;
                }
                ArrayList arrayList = RestrictedLanguagesSelectActivity.this.searching ? RestrictedLanguagesSelectActivity.this.searchResult : RestrictedLanguagesSelectActivity.this.sortedLanguages;
                if (arrayList == null) {
                    return -1;
                }
                for (int i = 0; i < arrayList.size(); i++) {
                    if (str.equals(((LocaleController.LocaleInfo) arrayList.get(i)).pluralLangCode)) {
                        return i;
                    }
                }
                return -1;
            }

            @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences2, String str) {
                RestrictedLanguagesSelectActivity.this.preferences = sharedPreferences2;
                HashSet<String> restrictedLanguages = RestrictedLanguagesSelectActivity.getRestrictedLanguages();
                if (RestrictedLanguagesSelectActivity.this.listView != null && RestrictedLanguagesSelectActivity.this.listView.getAdapter() != null) {
                    RecyclerView.Adapter adapter = RestrictedLanguagesSelectActivity.this.listView.getAdapter();
                    int i = !RestrictedLanguagesSelectActivity.this.searching ? 1 : 0;
                    Iterator it = RestrictedLanguagesSelectActivity.this.selectedLanguages.iterator();
                    while (it.hasNext()) {
                        String str2 = (String) it.next();
                        if (!restrictedLanguages.contains(str2)) {
                            adapter.notifyItemChanged(langPos(str2) + i);
                        }
                    }
                    Iterator<String> it2 = restrictedLanguages.iterator();
                    while (it2.hasNext()) {
                        String next = it2.next();
                        if (!RestrictedLanguagesSelectActivity.this.selectedLanguages.contains(next)) {
                            adapter.notifyItemChanged(langPos(next) + i);
                        }
                    }
                }
                RestrictedLanguagesSelectActivity.this.selectedLanguages = restrictedLanguages;
            }
        };
        this.listener = onSharedPreferenceChangeListener;
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        fillLanguages();
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.preferences.unregisterOnSharedPreferenceChangeListener(this.listener);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.searching = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("DoNotTranslate", R.string.DoNotTranslate));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    RestrictedLanguagesSelectActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity.3
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                RestrictedLanguagesSelectActivity.this.searching = true;
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                RestrictedLanguagesSelectActivity.this.search(null);
                RestrictedLanguagesSelectActivity.this.searching = false;
                RestrictedLanguagesSelectActivity.this.searchWas = false;
                if (RestrictedLanguagesSelectActivity.this.listView != null) {
                    RestrictedLanguagesSelectActivity.this.emptyView.setVisibility(8);
                    RestrictedLanguagesSelectActivity.this.listView.setAdapter(RestrictedLanguagesSelectActivity.this.listAdapter);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                String obj = editText.getText().toString();
                RestrictedLanguagesSelectActivity.this.search(obj);
                if (obj.length() != 0) {
                    RestrictedLanguagesSelectActivity.this.searchWas = true;
                    if (RestrictedLanguagesSelectActivity.this.listView == null) {
                        return;
                    }
                    RestrictedLanguagesSelectActivity.this.listView.setAdapter(RestrictedLanguagesSelectActivity.this.searchListViewAdapter);
                    return;
                }
                RestrictedLanguagesSelectActivity.this.searching = false;
                RestrictedLanguagesSelectActivity.this.searchWas = false;
                if (RestrictedLanguagesSelectActivity.this.listView == null) {
                    return;
                }
                RestrictedLanguagesSelectActivity.this.emptyView.setVisibility(8);
                RestrictedLanguagesSelectActivity.this.listView.setAdapter(RestrictedLanguagesSelectActivity.this.listAdapter);
            }
        }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.listAdapter = new ListAdapter(context, false);
        this.searchListViewAdapter = new ListAdapter(context, true);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.showTextView();
        this.emptyView.setShowAtCenter(true);
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                RestrictedLanguagesSelectActivity.this.lambda$createView$1(view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                boolean lambda$createView$3;
                lambda$createView$3 = RestrictedLanguagesSelectActivity.this.lambda$createView$3(view, i);
                return lambda$createView$3;
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(RestrictedLanguagesSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view, int i) {
        LocaleController.LocaleInfo localeInfo;
        if (getParentActivity() == null || this.parentLayout == null || !(view instanceof TextCheckbox2Cell)) {
            return;
        }
        boolean z = this.listView.getAdapter() == this.searchListViewAdapter;
        if (!z) {
            i--;
        }
        if (z) {
            localeInfo = this.searchResult.get(i);
        } else {
            localeInfo = this.sortedLanguages.get(i);
        }
        if (localeInfo == null) {
            return;
        }
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        final String str = localeInfo.pluralLangCode;
        if (str != null && str.equals(currentLocaleInfo.pluralLangCode)) {
            AndroidUtilities.shakeView(((TextCheckbox2Cell) view).checkbox);
            return;
        }
        boolean contains = this.selectedLanguages.contains(str);
        HashSet hashSet = new HashSet(this.selectedLanguages);
        if (contains) {
            Collection$EL.removeIf(hashSet, new Predicate() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda3
                @Override // j$.util.function.Predicate
                public /* synthetic */ Predicate and(Predicate predicate) {
                    return predicate.getClass();
                }

                @Override // j$.util.function.Predicate
                public /* synthetic */ Predicate negate() {
                    return Predicate.CC.$default$negate(this);
                }

                @Override // j$.util.function.Predicate
                public /* synthetic */ Predicate or(Predicate predicate) {
                    return predicate.getClass();
                }

                @Override // j$.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$createView$0;
                    lambda$createView$0 = RestrictedLanguagesSelectActivity.lambda$createView$0(str, (String) obj);
                    return lambda$createView$0;
                }
            });
        } else {
            hashSet.add(str);
        }
        if (hashSet.size() == 1 && hashSet.contains(currentLocaleInfo.pluralLangCode)) {
            this.preferences.edit().remove("translate_button_restricted_languages").apply();
        } else {
            this.preferences.edit().putStringSet("translate_button_restricted_languages", hashSet).apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(String str, String str2) {
        return str2 != null && str2.equals(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$3(View view, int i) {
        final LocaleController.LocaleInfo localeInfo;
        if (getParentActivity() != null && this.parentLayout != null && (view instanceof TextCheckbox2Cell)) {
            boolean z = this.listView.getAdapter() == this.searchListViewAdapter;
            if (!z) {
                i--;
            }
            if (z) {
                localeInfo = this.searchResult.get(i);
            } else {
                localeInfo = this.sortedLanguages.get(i);
            }
            if (localeInfo != null && localeInfo.pathToFile != null && (!localeInfo.isRemote() || localeInfo.serverIndex == Integer.MAX_VALUE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("DeleteLocalizationTitle", R.string.DeleteLocalizationTitle));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DeleteLocalizationText", R.string.DeleteLocalizationText, localeInfo.name)));
                builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        RestrictedLanguagesSelectActivity.this.lambda$createView$2(localeInfo, dialogInterface, i2);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog create = builder.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(LocaleController.LocaleInfo localeInfo, DialogInterface dialogInterface, int i) {
        if (LocaleController.getInstance().deleteLanguage(localeInfo, this.currentAccount)) {
            fillLanguages();
            ArrayList<LocaleController.LocaleInfo> arrayList = this.searchResult;
            if (arrayList != null) {
                arrayList.remove(localeInfo);
            }
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            ListAdapter listAdapter2 = this.searchListViewAdapter;
            if (listAdapter2 == null) {
                return;
            }
            listAdapter2.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.suggestedLangpack || this.listAdapter == null) {
            return;
        }
        fillLanguages();
        this.listAdapter.notifyDataSetChanged();
    }

    private void fillLanguages() {
        final LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        Comparator comparator = new Comparator() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda2
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$fillLanguages$4;
                lambda$fillLanguages$4 = RestrictedLanguagesSelectActivity.lambda$fillLanguages$4(LocaleController.LocaleInfo.this, (LocaleController.LocaleInfo) obj, (LocaleController.LocaleInfo) obj2);
                return lambda$fillLanguages$4;
            }
        };
        this.sortedLanguages = new ArrayList<>();
        ArrayList<LocaleController.LocaleInfo> arrayList = LocaleController.getInstance().languages;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            LocaleController.LocaleInfo localeInfo = arrayList.get(i);
            if (localeInfo != null && localeInfo.serverIndex != Integer.MAX_VALUE) {
                this.sortedLanguages.add(localeInfo);
            }
        }
        Collections.sort(this.sortedLanguages, comparator);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$fillLanguages$4(LocaleController.LocaleInfo localeInfo, LocaleController.LocaleInfo localeInfo2, LocaleController.LocaleInfo localeInfo3) {
        if (localeInfo2 == localeInfo) {
            return -1;
        }
        if (localeInfo3 == localeInfo) {
            return 1;
        }
        int i = localeInfo2.serverIndex;
        int i2 = localeInfo3.serverIndex;
        if (i == i2) {
            return localeInfo2.name.compareTo(localeInfo3.name);
        }
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void search(String str) {
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
            FileLog.e(e);
        }
        processSearch(str);
    }

    private void processSearch(String str) {
        if (str.trim().toLowerCase().length() == 0) {
            updateSearchResults(new ArrayList<>());
            return;
        }
        System.currentTimeMillis();
        ArrayList<LocaleController.LocaleInfo> arrayList = new ArrayList<>();
        int size = this.sortedLanguages.size();
        for (int i = 0; i < size; i++) {
            LocaleController.LocaleInfo localeInfo = this.sortedLanguages.get(i);
            if (localeInfo.name.toLowerCase().startsWith(str) || localeInfo.nameEnglish.toLowerCase().startsWith(str)) {
                arrayList.add(localeInfo);
            }
        }
        updateSearchResults(arrayList);
    }

    private void updateSearchResults(final ArrayList<LocaleController.LocaleInfo> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RestrictedLanguagesSelectActivity.this.lambda$updateSearchResults$5(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSearchResults$5(ArrayList arrayList) {
        this.searchResult = arrayList;
        this.searchListViewAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private boolean search;

        public ListAdapter(Context context, boolean z) {
            this.mContext = context;
            this.search = z;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (this.search) {
                if (RestrictedLanguagesSelectActivity.this.searchResult != null) {
                    return RestrictedLanguagesSelectActivity.this.searchResult.size();
                }
                return 0;
            }
            return RestrictedLanguagesSelectActivity.this.sortedLanguages.size() + 1;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1786onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextCheckbox2Cell textCheckbox2Cell;
            View view;
            if (i == 0) {
                TextCheckbox2Cell textCheckbox2Cell2 = new TextCheckbox2Cell(this.mContext);
                textCheckbox2Cell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textCheckbox2Cell = textCheckbox2Cell2;
            } else if (i == 2) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                headerCell.setText(LocaleController.getString("ChooseLanguages", R.string.ChooseLanguages));
                textCheckbox2Cell = headerCell;
            } else {
                view = new ShadowSectionCell(this.mContext);
                return new RecyclerListView.Holder(view);
            }
            view = textCheckbox2Cell;
            return new RecyclerListView.Holder(view);
        }

        /* JADX WARN: Code restructure failed: missing block: B:13:0x0042, code lost:
            if (r10 == (r8.this$0.searchResult.size() - 1)) goto L15;
         */
        /* JADX WARN: Code restructure failed: missing block: B:14:0x0044, code lost:
            r10 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:15:0x0046, code lost:
            r10 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:17:0x005f, code lost:
            if (r10 == (r8.this$0.sortedLanguages.size() - 1)) goto L15;
         */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            /*
                r8 = this;
                int r0 = r9.getItemViewType()
                r1 = 1
                if (r0 == 0) goto L1e
                if (r0 == r1) goto Lb
                goto Lb8
            Lb:
                android.view.View r9 = r9.itemView
                org.telegram.ui.Cells.ShadowSectionCell r9 = (org.telegram.ui.Cells.ShadowSectionCell) r9
                android.content.Context r10 = r8.mContext
                int r0 = org.telegram.messenger.R.drawable.greydivider_bottom
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r10, r0, r1)
                r9.setBackgroundDrawable(r10)
                goto Lb8
            L1e:
                boolean r0 = r8.search
                if (r0 != 0) goto L24
                int r10 = r10 + (-1)
            L24:
                android.view.View r9 = r9.itemView
                org.telegram.ui.Cells.TextCheckbox2Cell r9 = (org.telegram.ui.Cells.TextCheckbox2Cell) r9
                r2 = 0
                if (r0 == 0) goto L48
                org.telegram.ui.RestrictedLanguagesSelectActivity r0 = org.telegram.ui.RestrictedLanguagesSelectActivity.this
                java.util.ArrayList r0 = org.telegram.ui.RestrictedLanguagesSelectActivity.access$100(r0)
                java.lang.Object r0 = r0.get(r10)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.RestrictedLanguagesSelectActivity r3 = org.telegram.ui.RestrictedLanguagesSelectActivity.this
                java.util.ArrayList r3 = org.telegram.ui.RestrictedLanguagesSelectActivity.access$100(r3)
                int r3 = r3.size()
                int r3 = r3 - r1
                if (r10 != r3) goto L46
            L44:
                r10 = 1
                goto L62
            L46:
                r10 = 0
                goto L62
            L48:
                org.telegram.ui.RestrictedLanguagesSelectActivity r0 = org.telegram.ui.RestrictedLanguagesSelectActivity.this
                java.util.ArrayList r0 = org.telegram.ui.RestrictedLanguagesSelectActivity.access$200(r0)
                java.lang.Object r0 = r0.get(r10)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.RestrictedLanguagesSelectActivity r3 = org.telegram.ui.RestrictedLanguagesSelectActivity.this
                java.util.ArrayList r3 = org.telegram.ui.RestrictedLanguagesSelectActivity.access$200(r3)
                int r3 = r3.size()
                int r3 = r3 - r1
                if (r10 != r3) goto L46
                goto L44
            L62:
                java.lang.String r3 = r0.pluralLangCode
                org.telegram.ui.RestrictedLanguagesSelectActivity r4 = org.telegram.ui.RestrictedLanguagesSelectActivity.this
                java.util.HashSet r4 = org.telegram.ui.RestrictedLanguagesSelectActivity.access$500(r4)
                boolean r4 = r4.contains(r3)
                boolean r5 = r0.isLocal()
                if (r5 == 0) goto L92
                r5 = 2
                java.lang.Object[] r5 = new java.lang.Object[r5]
                java.lang.String r6 = r0.name
                r5[r2] = r6
                int r6 = org.telegram.messenger.R.string.LanguageCustom
                java.lang.String r7 = "LanguageCustom"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
                r5[r1] = r6
                java.lang.String r6 = "%1$s (%2$s)"
                java.lang.String r5 = java.lang.String.format(r6, r5)
                java.lang.String r0 = r0.nameEnglish
                r10 = r10 ^ r1
                r9.setTextAndValue(r5, r0, r2, r10)
                goto L9a
            L92:
                java.lang.String r5 = r0.name
                java.lang.String r0 = r0.nameEnglish
                r10 = r10 ^ r1
                r9.setTextAndValue(r5, r0, r2, r10)
            L9a:
                if (r3 == 0) goto Lae
                org.telegram.messenger.LocaleController r10 = org.telegram.messenger.LocaleController.getInstance()
                org.telegram.messenger.LocaleController$LocaleInfo r10 = r10.getCurrentLocaleInfo()
                java.lang.String r10 = r10.pluralLangCode
                boolean r10 = r3.equals(r10)
                if (r10 == 0) goto Lae
                r10 = 1
                goto Laf
            Lae:
                r10 = 0
            Laf:
                if (r4 != 0) goto Lb5
                if (r10 == 0) goto Lb4
                goto Lb5
            Lb4:
                r1 = 0
            Lb5:
                r9.setChecked(r1)
            Lb8:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.RestrictedLanguagesSelectActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (!this.search) {
                i--;
            }
            return i == -1 ? 2 : 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        return arrayList;
    }
}
