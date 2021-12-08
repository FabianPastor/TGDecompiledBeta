package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class LanguageSelectActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public ArrayList<LocaleController.LocaleInfo> searchResult;
    /* access modifiers changed from: private */
    public Timer searchTimer;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public ArrayList<LocaleController.LocaleInfo> sortedLanguages;
    /* access modifiers changed from: private */
    public ArrayList<LocaleController.LocaleInfo> unofficialLanguages;

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
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Language", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    LanguageSelectActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = LanguageSelectActivity.this.searching = true;
            }

            public void onSearchCollapse() {
                LanguageSelectActivity.this.search((String) null);
                boolean unused = LanguageSelectActivity.this.searching = false;
                boolean unused2 = LanguageSelectActivity.this.searchWas = false;
                if (LanguageSelectActivity.this.listView != null) {
                    LanguageSelectActivity.this.emptyView.setVisibility(8);
                    LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.listAdapter);
                }
            }

            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                LanguageSelectActivity.this.search(text);
                if (text.length() != 0) {
                    boolean unused = LanguageSelectActivity.this.searchWas = true;
                    if (LanguageSelectActivity.this.listView != null) {
                        LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
                    }
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new LanguageSelectActivity$$ExternalSyntheticLambda4(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new LanguageSelectActivity$$ExternalSyntheticLambda5(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3058lambda$createView$0$orgtelegramuiLanguageSelectActivity(View view, int position) {
        if (getParentActivity() != null && this.parentLayout != null && (view instanceof LanguageCell)) {
            LocaleController.LocaleInfo localeInfo = ((LanguageCell) view).getCurrentLocale();
            if (localeInfo != null) {
                LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, this.currentAccount);
                this.parentLayout.rebuildAllFragmentViews(false, false);
            }
            finishFragment();
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ boolean m3060lambda$createView$2$orgtelegramuiLanguageSelectActivity(View view, int position) {
        LocaleController.LocaleInfo localeInfo;
        if (getParentActivity() == null || this.parentLayout == null || !(view instanceof LanguageCell) || (localeInfo = ((LanguageCell) view).getCurrentLocale()) == null || localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("DeleteLocalizationTitle", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DeleteLocalizationText", NUM, localeInfo.name)));
        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new LanguageSelectActivity$$ExternalSyntheticLambda0(this, localeInfo));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
        return true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3059lambda$createView$1$orgtelegramuiLanguageSelectActivity(LocaleController.LocaleInfo finalLocaleInfo, DialogInterface dialogInterface, int i) {
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
        Comparator<LocaleController.LocaleInfo> comparator = new LanguageSelectActivity$$ExternalSyntheticLambda3(LocaleController.getInstance().getCurrentLocaleInfo());
        this.sortedLanguages = new ArrayList<>();
        this.unofficialLanguages = new ArrayList<>(LocaleController.getInstance().unofficialLanguages);
        ArrayList<LocaleController.LocaleInfo> arrayList = LocaleController.getInstance().languages;
        int size = arrayList.size();
        for (int a = 0; a < size; a++) {
            LocaleController.LocaleInfo info = arrayList.get(a);
            if (info.serverIndex != Integer.MAX_VALUE) {
                this.sortedLanguages.add(info);
            } else {
                this.unofficialLanguages.add(info);
            }
        }
        Collections.sort(this.sortedLanguages, comparator);
        Collections.sort(this.unofficialLanguages, comparator);
    }

    static /* synthetic */ int lambda$fillLanguages$3(LocaleController.LocaleInfo currentLocale, LocaleController.LocaleInfo o, LocaleController.LocaleInfo o2) {
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
                    LanguageSelectActivity.this.searchTimer.cancel();
                    Timer unused = LanguageSelectActivity.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                LanguageSelectActivity.this.processSearch(query);
            }
        }, 100, 300);
    }

    /* access modifiers changed from: private */
    public void processSearch(String query) {
        Utilities.searchQueue.postRunnable(new LanguageSelectActivity$$ExternalSyntheticLambda1(this, query));
    }

    /* renamed from: lambda$processSearch$4$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3061lambda$processSearch$4$orgtelegramuiLanguageSelectActivity(String query) {
        if (query.trim().toLowerCase().length() == 0) {
            updateSearchResults(new ArrayList());
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList<LocaleController.LocaleInfo> resultArray = new ArrayList<>();
        int N = this.unofficialLanguages.size();
        for (int a = 0; a < N; a++) {
            LocaleController.LocaleInfo c = this.unofficialLanguages.get(a);
            if (c.name.toLowerCase().startsWith(query) || c.nameEnglish.toLowerCase().startsWith(query)) {
                resultArray.add(c);
            }
        }
        int N2 = this.sortedLanguages.size();
        for (int a2 = 0; a2 < N2; a2++) {
            LocaleController.LocaleInfo c2 = this.sortedLanguages.get(a2);
            if (c2.name.toLowerCase().startsWith(query) || c2.nameEnglish.toLowerCase().startsWith(query)) {
                resultArray.add(c2);
            }
        }
        updateSearchResults(resultArray);
    }

    private void updateSearchResults(ArrayList<LocaleController.LocaleInfo> arrCounties) {
        AndroidUtilities.runOnUIThread(new LanguageSelectActivity$$ExternalSyntheticLambda2(this, arrCounties));
    }

    /* renamed from: lambda$updateSearchResults$5$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3062xevar_c4(ArrayList arrCounties) {
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
                int count = LanguageSelectActivity.this.sortedLanguages.size();
                if (count != 0) {
                    count++;
                }
                if (!LanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                    return count + LanguageSelectActivity.this.unofficialLanguages.size() + 1;
                }
                return count;
            } else if (LanguageSelectActivity.this.searchResult == null) {
                return 0;
            } else {
                return LanguageSelectActivity.this.searchResult.size();
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new LanguageCell(this.mContext, false);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
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
                    LanguageCell textSettingsCell = (LanguageCell) holder.itemView;
                    boolean z = false;
                    if (this.search) {
                        localeInfo = (LocaleController.LocaleInfo) LanguageSelectActivity.this.searchResult.get(position);
                        last = position == LanguageSelectActivity.this.searchResult.size() - 1;
                    } else if (LanguageSelectActivity.this.unofficialLanguages.isEmpty() || position < 0 || position >= LanguageSelectActivity.this.unofficialLanguages.size()) {
                        if (!LanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                            position -= LanguageSelectActivity.this.unofficialLanguages.size() + 1;
                        }
                        localeInfo = (LocaleController.LocaleInfo) LanguageSelectActivity.this.sortedLanguages.get(position);
                        last = position == LanguageSelectActivity.this.sortedLanguages.size() - 1;
                    } else {
                        localeInfo = (LocaleController.LocaleInfo) LanguageSelectActivity.this.unofficialLanguages.get(position);
                        last = position == LanguageSelectActivity.this.unofficialLanguages.size() - 1;
                    }
                    if (localeInfo.isLocal()) {
                        textSettingsCell.setLanguage(localeInfo, String.format("%1$s (%2$s)", new Object[]{localeInfo.name, LocaleController.getString("LanguageCustom", NUM)}), !last);
                    } else {
                        textSettingsCell.setLanguage(localeInfo, (String) null, !last);
                    }
                    if (localeInfo == LocaleController.getInstance().getCurrentLocaleInfo()) {
                        z = true;
                    }
                    textSettingsCell.setLanguageSelected(z);
                    return;
                case 1:
                    ShadowSectionCell sectionCell = (ShadowSectionCell) holder.itemView;
                    if (LanguageSelectActivity.this.unofficialLanguages.isEmpty() || position != LanguageSelectActivity.this.unofficialLanguages.size()) {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        sectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (this.search) {
                return 0;
            }
            if ((LanguageSelectActivity.this.unofficialLanguages.isEmpty() || (i != LanguageSelectActivity.this.unofficialLanguages.size() && i != LanguageSelectActivity.this.unofficialLanguages.size() + LanguageSelectActivity.this.sortedLanguages.size() + 1)) && (!LanguageSelectActivity.this.unofficialLanguages.isEmpty() || i != LanguageSelectActivity.this.sortedLanguages.size())) {
                return 0;
            }
            return 1;
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
