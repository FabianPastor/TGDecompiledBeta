package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Collection$EL;
import java.util.ArrayList;
import java.util.Collections;
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
            public int langPos(String str) {
                if (str == null) {
                    return -1;
                }
                ArrayList access$100 = RestrictedLanguagesSelectActivity.this.searching ? RestrictedLanguagesSelectActivity.this.searchResult : RestrictedLanguagesSelectActivity.this.sortedLanguages;
                if (access$100 == null) {
                    return -1;
                }
                for (int i = 0; i < access$100.size(); i++) {
                    if (str.equals(((LocaleController.LocaleInfo) access$100.get(i)).pluralLangCode)) {
                        return i;
                    }
                }
                return -1;
            }

            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
                SharedPreferences unused = RestrictedLanguagesSelectActivity.this.preferences = sharedPreferences;
                HashSet<String> restrictedLanguages = RestrictedLanguagesSelectActivity.getRestrictedLanguages();
                if (!(RestrictedLanguagesSelectActivity.this.listView == null || RestrictedLanguagesSelectActivity.this.listView.getAdapter() == null)) {
                    RecyclerView.Adapter adapter = RestrictedLanguagesSelectActivity.this.listView.getAdapter();
                    boolean z = !RestrictedLanguagesSelectActivity.this.searching;
                    Iterator it = RestrictedLanguagesSelectActivity.this.selectedLanguages.iterator();
                    while (it.hasNext()) {
                        String str2 = (String) it.next();
                        if (!restrictedLanguages.contains(str2)) {
                            adapter.notifyItemChanged(langPos(str2) + z);
                        }
                    }
                    Iterator<String> it2 = restrictedLanguages.iterator();
                    while (it2.hasNext()) {
                        String next = it2.next();
                        if (!RestrictedLanguagesSelectActivity.this.selectedLanguages.contains(next)) {
                            adapter.notifyItemChanged(langPos(next) + (z ? 1 : 0));
                        }
                    }
                }
                HashSet unused2 = RestrictedLanguagesSelectActivity.this.selectedLanguages = restrictedLanguages;
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
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("DoNotTranslate", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
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
                String obj = editText.getText().toString();
                RestrictedLanguagesSelectActivity.this.search(obj);
                if (obj.length() != 0) {
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
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoResult", NUM));
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda4(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda5(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(RestrictedLanguagesSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view, int i) {
        LocaleController.LocaleInfo localeInfo;
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
            if (localeInfo != null) {
                LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
                String str = localeInfo.pluralLangCode;
                if (str == null || !str.equals(currentLocaleInfo.pluralLangCode)) {
                    boolean contains = this.selectedLanguages.contains(str);
                    HashSet hashSet = new HashSet(this.selectedLanguages);
                    if (contains) {
                        Collection$EL.removeIf(hashSet, new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda3(str));
                    } else {
                        hashSet.add(str);
                    }
                    if (hashSet.size() != 1 || !hashSet.contains(currentLocaleInfo.pluralLangCode)) {
                        this.preferences.edit().putStringSet("translate_button_restricted_languages", hashSet).apply();
                    } else {
                        this.preferences.edit().remove("translate_button_restricted_languages").apply();
                    }
                } else {
                    AndroidUtilities.shakeView(((TextCheckbox2Cell) view).checkbox, 2.0f, 0);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(String str, String str2) {
        return str2 != null && str2.equals(str);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$3(View view, int i) {
        LocaleController.LocaleInfo localeInfo;
        if (!(getParentActivity() == null || this.parentLayout == null || !(view instanceof TextCheckbox2Cell))) {
            boolean z = this.listView.getAdapter() == this.searchListViewAdapter;
            if (!z) {
                i--;
            }
            if (z) {
                localeInfo = this.searchResult.get(i);
            } else {
                localeInfo = this.sortedLanguages.get(i);
            }
            if (!(localeInfo == null || localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE))) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("DeleteLocalizationTitle", NUM));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DeleteLocalizationText", NUM, localeInfo.name)));
                builder.setPositiveButton(LocaleController.getString("Delete", NUM), new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda0(this, localeInfo));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(LocaleController.LocaleInfo localeInfo, DialogInterface dialogInterface, int i) {
        if (LocaleController.getInstance().deleteLanguage(localeInfo, this.currentAccount)) {
            fillLanguages();
            ArrayList<LocaleController.LocaleInfo> arrayList = this.searchResult;
            if (arrayList != null) {
                arrayList.remove(localeInfo);
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.suggestedLangpack && this.listAdapter != null) {
            fillLanguages();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void fillLanguages() {
        RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda2 restrictedLanguagesSelectActivity$$ExternalSyntheticLambda2 = new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda2(LocaleController.getInstance().getCurrentLocaleInfo());
        this.sortedLanguages = new ArrayList<>();
        ArrayList<LocaleController.LocaleInfo> arrayList = LocaleController.getInstance().languages;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            LocaleController.LocaleInfo localeInfo = arrayList.get(i);
            if (!(localeInfo == null || localeInfo.serverIndex == Integer.MAX_VALUE)) {
                this.sortedLanguages.add(localeInfo);
            }
        }
        Collections.sort(this.sortedLanguages, restrictedLanguagesSelectActivity$$ExternalSyntheticLambda2);
    }

    /* access modifiers changed from: private */
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
        if (i < i2) {
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
            FileLog.e((Throwable) e);
        }
        processSearch(str);
    }

    private void processSearch(String str) {
        if (str.trim().toLowerCase().length() == 0) {
            updateSearchResults(new ArrayList());
            return;
        }
        System.currentTimeMillis();
        ArrayList arrayList = new ArrayList();
        int size = this.sortedLanguages.size();
        for (int i = 0; i < size; i++) {
            LocaleController.LocaleInfo localeInfo = this.sortedLanguages.get(i);
            if (localeInfo.name.toLowerCase().startsWith(str) || localeInfo.nameEnglish.toLowerCase().startsWith(str)) {
                arrayList.add(localeInfo);
            }
        }
        updateSearchResults(arrayList);
    }

    private void updateSearchResults(ArrayList<LocaleController.LocaleInfo> arrayList) {
        AndroidUtilities.runOnUIThread(new RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda1(this, arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSearchResults$5(ArrayList arrayList) {
        this.searchResult = arrayList;
        this.searchListViewAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private boolean search;

        public ListAdapter(Context context, boolean z) {
            this.mContext = context;
            this.search = z;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.ui.Cells.TextCheckbox2Cell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: org.telegram.ui.Cells.TextCheckbox2Cell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: org.telegram.ui.Cells.TextCheckbox2Cell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r2, int r3) {
            /*
                r1 = this;
                java.lang.String r2 = "windowBackgroundWhite"
                if (r3 == 0) goto L_0x002a
                r0 = 2
                if (r3 == r0) goto L_0x000f
                org.telegram.ui.Cells.ShadowSectionCell r2 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r3 = r1.mContext
                r2.<init>(r3)
                goto L_0x0039
            L_0x000f:
                org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r0 = r1.mContext
                r3.<init>(r0)
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r3.setBackgroundColor(r2)
                r2 = 2131624982(0x7f0e0416, float:1.887716E38)
                java.lang.String r0 = "ChooseLanguages"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r0, r2)
                r3.setText(r2)
                goto L_0x0038
            L_0x002a:
                org.telegram.ui.Cells.TextCheckbox2Cell r3 = new org.telegram.ui.Cells.TextCheckbox2Cell
                android.content.Context r0 = r1.mContext
                r3.<init>(r0)
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r3.setBackgroundColor(r2)
            L_0x0038:
                r2 = r3
            L_0x0039:
                org.telegram.ui.Components.RecyclerListView$Holder r3 = new org.telegram.ui.Components.RecyclerListView$Holder
                r3.<init>(r2)
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.RestrictedLanguagesSelectActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0043, code lost:
            if (r10 == (org.telegram.ui.RestrictedLanguagesSelectActivity.access$100(r8.this$0).size() - 1)) goto L_0x0045;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0047, code lost:
            r10 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0060, code lost:
            if (r10 == (org.telegram.ui.RestrictedLanguagesSelectActivity.access$200(r8.this$0).size() - 1)) goto L_0x0045;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            /*
                r8 = this;
                int r0 = r9.getItemViewType()
                r1 = 1
                if (r0 == 0) goto L_0x001f
                if (r0 == r1) goto L_0x000b
                goto L_0x00ba
            L_0x000b:
                android.view.View r9 = r9.itemView
                org.telegram.ui.Cells.ShadowSectionCell r9 = (org.telegram.ui.Cells.ShadowSectionCell) r9
                android.content.Context r10 = r8.mContext
                r0 = 2131165472(0x7var_, float:1.7945162E38)
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r10, (int) r0, (java.lang.String) r1)
                r9.setBackgroundDrawable(r10)
                goto L_0x00ba
            L_0x001f:
                boolean r0 = r8.search
                if (r0 != 0) goto L_0x0025
                int r10 = r10 + -1
            L_0x0025:
                android.view.View r9 = r9.itemView
                org.telegram.ui.Cells.TextCheckbox2Cell r9 = (org.telegram.ui.Cells.TextCheckbox2Cell) r9
                r2 = 0
                if (r0 == 0) goto L_0x0049
                org.telegram.ui.RestrictedLanguagesSelectActivity r0 = org.telegram.ui.RestrictedLanguagesSelectActivity.this
                java.util.ArrayList r0 = r0.searchResult
                java.lang.Object r0 = r0.get(r10)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.RestrictedLanguagesSelectActivity r3 = org.telegram.ui.RestrictedLanguagesSelectActivity.this
                java.util.ArrayList r3 = r3.searchResult
                int r3 = r3.size()
                int r3 = r3 - r1
                if (r10 != r3) goto L_0x0047
            L_0x0045:
                r10 = 1
                goto L_0x0063
            L_0x0047:
                r10 = 0
                goto L_0x0063
            L_0x0049:
                org.telegram.ui.RestrictedLanguagesSelectActivity r0 = org.telegram.ui.RestrictedLanguagesSelectActivity.this
                java.util.ArrayList r0 = r0.sortedLanguages
                java.lang.Object r0 = r0.get(r10)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.RestrictedLanguagesSelectActivity r3 = org.telegram.ui.RestrictedLanguagesSelectActivity.this
                java.util.ArrayList r3 = r3.sortedLanguages
                int r3 = r3.size()
                int r3 = r3 - r1
                if (r10 != r3) goto L_0x0047
                goto L_0x0045
            L_0x0063:
                java.lang.String r3 = r0.pluralLangCode
                org.telegram.ui.RestrictedLanguagesSelectActivity r4 = org.telegram.ui.RestrictedLanguagesSelectActivity.this
                java.util.HashSet r4 = r4.selectedLanguages
                boolean r4 = r4.contains(r3)
                boolean r5 = r0.isLocal()
                if (r5 == 0) goto L_0x0094
                r5 = 2
                java.lang.Object[] r5 = new java.lang.Object[r5]
                java.lang.String r6 = r0.name
                r5[r2] = r6
                r6 = 2131626155(0x7f0e08ab, float:1.8879538E38)
                java.lang.String r7 = "LanguageCustom"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
                r5[r1] = r6
                java.lang.String r6 = "%1$s (%2$s)"
                java.lang.String r5 = java.lang.String.format(r6, r5)
                java.lang.String r0 = r0.nameEnglish
                r10 = r10 ^ r1
                r9.setTextAndValue(r5, r0, r2, r10)
                goto L_0x009c
            L_0x0094:
                java.lang.String r5 = r0.name
                java.lang.String r0 = r0.nameEnglish
                r10 = r10 ^ r1
                r9.setTextAndValue(r5, r0, r2, r10)
            L_0x009c:
                if (r3 == 0) goto L_0x00b0
                org.telegram.messenger.LocaleController r10 = org.telegram.messenger.LocaleController.getInstance()
                org.telegram.messenger.LocaleController$LocaleInfo r10 = r10.getCurrentLocaleInfo()
                java.lang.String r10 = r10.pluralLangCode
                boolean r10 = r3.equals(r10)
                if (r10 == 0) goto L_0x00b0
                r10 = 1
                goto L_0x00b1
            L_0x00b0:
                r10 = 0
            L_0x00b1:
                if (r4 != 0) goto L_0x00b7
                if (r10 == 0) goto L_0x00b6
                goto L_0x00b7
            L_0x00b6:
                r1 = 0
            L_0x00b7:
                r9.setChecked(r1)
            L_0x00ba:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.RestrictedLanguagesSelectActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (!this.search) {
                i--;
            }
            return i == -1 ? 2 : 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        return arrayList;
    }
}
