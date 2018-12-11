package org.telegram.p005ui;

import android.content.Context;
import android.content.DialogInterface;
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
import org.telegram.messenger.CLASSNAMER;
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
import org.telegram.p005ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.LanguageCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.ConnectionsManager;

/* renamed from: org.telegram.ui.LanguageSelectActivity */
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
    private ArrayList<LocaleInfo> unofficialLanguages;

    /* renamed from: org.telegram.ui.LanguageSelectActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                LanguageSelectActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity$2 */
    class CLASSNAME extends ActionBarMenuItemSearchListener {
        CLASSNAME() {
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
            String text = editText.getText().toString();
            LanguageSelectActivity.this.search(text);
            if (text.length() != 0) {
                LanguageSelectActivity.this.searchWas = true;
                if (LanguageSelectActivity.this.listView != null) {
                    LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity$3 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1 && LanguageSelectActivity.this.searching && LanguageSelectActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
            }
        }
    }

    /* renamed from: org.telegram.ui.LanguageSelectActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;
        private boolean search;

        public ListAdapter(Context context, boolean isSearch) {
            this.mContext = context;
            this.search = isSearch;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            if (!this.search) {
                int count = LanguageSelectActivity.this.sortedLanguages.size();
                if (count != 0) {
                    count++;
                }
                if (LanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                    return count;
                }
                return count + (LanguageSelectActivity.this.unofficialLanguages.size() + 1);
            } else if (LanguageSelectActivity.this.searchResult == null) {
                return 0;
            } else {
                return LanguageSelectActivity.this.searchResult.size();
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new LanguageCell(this.mContext, false);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new ShadowSectionCell(this.mContext);
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    LocaleInfo localeInfo;
                    boolean last;
                    LanguageCell textSettingsCell = holder.itemView;
                    if (this.search) {
                        localeInfo = (LocaleInfo) LanguageSelectActivity.this.searchResult.get(position);
                        last = position == LanguageSelectActivity.this.searchResult.size() + -1;
                    } else if (LanguageSelectActivity.this.unofficialLanguages.isEmpty() || position < 0 || position >= LanguageSelectActivity.this.unofficialLanguages.size()) {
                        if (!LanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                            position -= LanguageSelectActivity.this.unofficialLanguages.size() + 1;
                        }
                        localeInfo = (LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(position);
                        last = position == LanguageSelectActivity.this.sortedLanguages.size() + -1;
                    } else {
                        localeInfo = (LocaleInfo) LanguageSelectActivity.this.unofficialLanguages.get(position);
                        last = position == LanguageSelectActivity.this.unofficialLanguages.size() + -1;
                    }
                    if (localeInfo.isLocal()) {
                        boolean z2;
                        String format = String.format("%1$s (%2$s)", new Object[]{localeInfo.name, LocaleController.getString("LanguageCustom", CLASSNAMER.string.LanguageCustom)});
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
                    return;
                case 1:
                    ShadowSectionCell sectionCell = holder.itemView;
                    if (LanguageSelectActivity.this.unofficialLanguages.isEmpty() || position != LanguageSelectActivity.this.unofficialLanguages.size()) {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) CLASSNAMER.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if ((LanguageSelectActivity.this.unofficialLanguages.isEmpty() || (i != LanguageSelectActivity.this.unofficialLanguages.size() && i != (LanguageSelectActivity.this.unofficialLanguages.size() + LanguageSelectActivity.this.sortedLanguages.size()) + 1)) && (!LanguageSelectActivity.this.unofficialLanguages.isEmpty() || i != LanguageSelectActivity.this.sortedLanguages.size())) {
                return 0;
            }
            return 1;
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
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Language", CLASSNAMER.string.Language));
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.actionBar.createMenu().addItem(0, (int) CLASSNAMER.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new CLASSNAME()).setSearchFieldHint(LocaleController.getString("Search", CLASSNAMER.string.Search));
        this.listAdapter = new ListAdapter(context, false);
        this.searchListViewAdapter = new ListAdapter(context, true);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoResult", CLASSNAMER.string.NoResult));
        this.emptyView.showTextView();
        this.emptyView.setShowAtCenter(true);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new LanguageSelectActivity$$Lambda$0(this));
        this.listView.setOnItemLongClickListener(new LanguageSelectActivity$$Lambda$1(this));
        this.listView.setOnScrollListener(new CLASSNAME());
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$LanguageSelectActivity(View view, int position) {
        if (getParentActivity() != null && this.parentLayout != null && (view instanceof LanguageCell)) {
            LocaleInfo localeInfo = ((LanguageCell) view).getCurrentLocale();
            if (localeInfo != null) {
                LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, this.currentAccount);
                this.parentLayout.rebuildAllFragmentViews(false, false);
            }
            finishFragment();
        }
    }

    final /* synthetic */ boolean lambda$createView$2$LanguageSelectActivity(View view, int position) {
        if (getParentActivity() == null || this.parentLayout == null || !(view instanceof LanguageCell)) {
            return false;
        }
        LocaleInfo localeInfo = ((LanguageCell) view).getCurrentLocale();
        if (localeInfo == null || localeInfo.pathToFile == null) {
            return false;
        }
        if (localeInfo.isRemote() && localeInfo.serverIndex != ConnectionsManager.DEFAULT_DATACENTER_ID) {
            return false;
        }
        LocaleInfo finalLocaleInfo = localeInfo;
        Builder builder = new Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("DeleteLocalization", CLASSNAMER.string.DeleteLocalization));
        builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
        builder.setPositiveButton(LocaleController.getString("Delete", CLASSNAMER.string.Delete), new LanguageSelectActivity$$Lambda$5(this, finalLocaleInfo));
        builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
        showDialog(builder.create());
        return true;
    }

    final /* synthetic */ void lambda$null$1$LanguageSelectActivity(LocaleInfo finalLocaleInfo, DialogInterface dialogInterface, int i) {
        if (LocaleController.getInstance().deleteLanguage(finalLocaleInfo, this.currentAccount)) {
            fillLanguages();
            if (this.searchResult != null) {
                this.searchResult.remove(finalLocaleInfo);
            }
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
            if (this.searchListViewAdapter != null) {
                this.searchListViewAdapter.notifyDataSetChanged();
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
        Comparator<LocaleInfo> comparator = new LanguageSelectActivity$$Lambda$2(LocaleController.getInstance().getCurrentLocaleInfo());
        this.sortedLanguages = new ArrayList();
        this.unofficialLanguages = new ArrayList(LocaleController.getInstance().unofficialLanguages);
        ArrayList<LocaleInfo> arrayList = LocaleController.getInstance().languages;
        int size = arrayList.size();
        for (int a = 0; a < size; a++) {
            LocaleInfo info = (LocaleInfo) arrayList.get(a);
            if (info.serverIndex != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                this.sortedLanguages.add(info);
            } else {
                this.unofficialLanguages.add(info);
            }
        }
        Collections.sort(this.sortedLanguages, comparator);
        Collections.sort(this.unofficialLanguages, comparator);
    }

    static final /* synthetic */ int lambda$fillLanguages$3$LanguageSelectActivity(LocaleInfo currentLocale, LocaleInfo o, LocaleInfo o2) {
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
        if (o.serverIndex >= o2.serverIndex) {
            return 0;
        }
        return -1;
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
            FileLog.m13e(e);
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask() {
            public void run() {
                try {
                    LanguageSelectActivity.this.searchTimer.cancel();
                    LanguageSelectActivity.this.searchTimer = null;
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                LanguageSelectActivity.this.processSearch(query);
            }
        }, 100, 300);
    }

    private void processSearch(String query) {
        Utilities.searchQueue.postRunnable(new LanguageSelectActivity$$Lambda$3(this, query));
    }

    final /* synthetic */ void lambda$processSearch$4$LanguageSelectActivity(String query) {
        if (query.trim().toLowerCase().length() == 0) {
            updateSearchResults(new ArrayList());
            return;
        }
        int a;
        LocaleInfo c;
        long time = System.currentTimeMillis();
        ArrayList<LocaleInfo> resultArray = new ArrayList();
        int N = this.unofficialLanguages.size();
        for (a = 0; a < N; a++) {
            c = (LocaleInfo) this.unofficialLanguages.get(a);
            if (c.name.toLowerCase().startsWith(query) || c.nameEnglish.toLowerCase().startsWith(query)) {
                resultArray.add(c);
            }
        }
        N = this.sortedLanguages.size();
        for (a = 0; a < N; a++) {
            c = (LocaleInfo) this.sortedLanguages.get(a);
            if (c.name.toLowerCase().startsWith(query) || c.nameEnglish.toLowerCase().startsWith(query)) {
                resultArray.add(c);
            }
        }
        updateSearchResults(resultArray);
    }

    private void updateSearchResults(ArrayList<LocaleInfo> arrCounties) {
        AndroidUtilities.runOnUIThread(new LanguageSelectActivity$$Lambda$4(this, arrCounties));
    }

    final /* synthetic */ void lambda$updateSearchResults$5$LanguageSelectActivity(ArrayList arrCounties) {
        this.searchResult = arrCounties;
        this.searchListViewAdapter.notifyDataSetChanged();
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[16];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        themeDescriptionArr[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[10] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon);
        return themeDescriptionArr;
    }
}
