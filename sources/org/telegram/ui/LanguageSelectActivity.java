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
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Language", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
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
                String obj = editText.getText().toString();
                LanguageSelectActivity.this.search(obj);
                if (obj.length() != 0) {
                    boolean unused = LanguageSelectActivity.this.searchWas = true;
                    if (LanguageSelectActivity.this.listView != null) {
                        LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
                    }
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new LanguageSelectActivity$$ExternalSyntheticLambda4(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new LanguageSelectActivity$$ExternalSyntheticLambda5(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view, int i) {
        if (getParentActivity() != null && this.parentLayout != null && (view instanceof LanguageCell)) {
            LocaleController.LocaleInfo currentLocale = ((LanguageCell) view).getCurrentLocale();
            if (currentLocale != null) {
                LocaleController.getInstance().applyLanguage(currentLocale, true, false, false, true, this.currentAccount);
                this.parentLayout.rebuildAllFragmentViews(false, false);
            }
            finishFragment();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$2(View view, int i) {
        LocaleController.LocaleInfo currentLocale;
        if (getParentActivity() == null || this.parentLayout == null || !(view instanceof LanguageCell) || (currentLocale = ((LanguageCell) view).getCurrentLocale()) == null || currentLocale.pathToFile == null || (currentLocale.isRemote() && currentLocale.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("DeleteLocalizationTitle", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DeleteLocalizationText", NUM, currentLocale.name)));
        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new LanguageSelectActivity$$ExternalSyntheticLambda0(this, currentLocale));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(LocaleController.LocaleInfo localeInfo, DialogInterface dialogInterface, int i) {
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
        LanguageSelectActivity$$ExternalSyntheticLambda3 languageSelectActivity$$ExternalSyntheticLambda3 = new LanguageSelectActivity$$ExternalSyntheticLambda3(LocaleController.getInstance().getCurrentLocaleInfo());
        this.sortedLanguages = new ArrayList<>();
        this.unofficialLanguages = new ArrayList<>(LocaleController.getInstance().unofficialLanguages);
        ArrayList<LocaleController.LocaleInfo> arrayList = LocaleController.getInstance().languages;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            LocaleController.LocaleInfo localeInfo = arrayList.get(i);
            if (localeInfo.serverIndex != Integer.MAX_VALUE) {
                this.sortedLanguages.add(localeInfo);
            } else {
                this.unofficialLanguages.add(localeInfo);
            }
        }
        Collections.sort(this.sortedLanguages, languageSelectActivity$$ExternalSyntheticLambda3);
        Collections.sort(this.unofficialLanguages, languageSelectActivity$$ExternalSyntheticLambda3);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$fillLanguages$3(LocaleController.LocaleInfo localeInfo, LocaleController.LocaleInfo localeInfo2, LocaleController.LocaleInfo localeInfo3) {
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
                    LanguageSelectActivity.this.searchTimer.cancel();
                    Timer unused = LanguageSelectActivity.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                LanguageSelectActivity.this.processSearch(str);
            }
        }, 100, 300);
    }

    /* access modifiers changed from: private */
    public void processSearch(String str) {
        Utilities.searchQueue.postRunnable(new LanguageSelectActivity$$ExternalSyntheticLambda1(this, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processSearch$4(String str) {
        if (str.trim().toLowerCase().length() == 0) {
            updateSearchResults(new ArrayList());
            return;
        }
        System.currentTimeMillis();
        ArrayList arrayList = new ArrayList();
        int size = this.unofficialLanguages.size();
        for (int i = 0; i < size; i++) {
            LocaleController.LocaleInfo localeInfo = this.unofficialLanguages.get(i);
            if (localeInfo.name.toLowerCase().startsWith(str) || localeInfo.nameEnglish.toLowerCase().startsWith(str)) {
                arrayList.add(localeInfo);
            }
        }
        int size2 = this.sortedLanguages.size();
        for (int i2 = 0; i2 < size2; i2++) {
            LocaleController.LocaleInfo localeInfo2 = this.sortedLanguages.get(i2);
            if (localeInfo2.name.toLowerCase().startsWith(str) || localeInfo2.nameEnglish.toLowerCase().startsWith(str)) {
                arrayList.add(localeInfo2);
            }
        }
        updateSearchResults(arrayList);
    }

    private void updateSearchResults(ArrayList<LocaleController.LocaleInfo> arrayList) {
        AndroidUtilities.runOnUIThread(new LanguageSelectActivity$$ExternalSyntheticLambda2(this, arrayList));
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
                int size = LanguageSelectActivity.this.sortedLanguages.size();
                if (size != 0) {
                    size++;
                }
                return !LanguageSelectActivity.this.unofficialLanguages.isEmpty() ? size + LanguageSelectActivity.this.unofficialLanguages.size() + 1 : size;
            } else if (LanguageSelectActivity.this.searchResult == null) {
                return 0;
            } else {
                return LanguageSelectActivity.this.searchResult.size();
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new ShadowSectionCell(this.mContext);
            } else {
                view = new LanguageCell(this.mContext, false);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0065, code lost:
            if (r8 == (org.telegram.ui.LanguageSelectActivity.access$800(r6.this$0).size() - 1)) goto L_0x0067;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0069, code lost:
            r8 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x009c, code lost:
            if (r8 == (org.telegram.ui.LanguageSelectActivity.access$1000(r6.this$0).size() - 1)) goto L_0x0067;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x00ce, code lost:
            if (r8 == (org.telegram.ui.LanguageSelectActivity.access$900(r6.this$0).size() - 1)) goto L_0x0067;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r7, int r8) {
            /*
                r6 = this;
                int r0 = r7.getItemViewType()
                r1 = 1
                if (r0 == 0) goto L_0x0045
                if (r0 == r1) goto L_0x000b
                goto L_0x0108
            L_0x000b:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.ShadowSectionCell r7 = (org.telegram.ui.Cells.ShadowSectionCell) r7
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                boolean r0 = r0.isEmpty()
                java.lang.String r1 = "windowBackgroundGrayShadow"
                if (r0 != 0) goto L_0x0037
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                int r0 = r0.size()
                if (r8 != r0) goto L_0x0037
                android.content.Context r8 = r6.mContext
                r0 = 2131165448(0x7var_, float:1.7945113E38)
                android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r0, (java.lang.String) r1)
                r7.setBackgroundDrawable(r8)
                goto L_0x0108
            L_0x0037:
                android.content.Context r8 = r6.mContext
                r0 = 2131165449(0x7var_, float:1.7945115E38)
                android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r0, (java.lang.String) r1)
                r7.setBackgroundDrawable(r8)
                goto L_0x0108
            L_0x0045:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.LanguageCell r7 = (org.telegram.ui.Cells.LanguageCell) r7
                boolean r0 = r6.search
                r2 = 0
                if (r0 == 0) goto L_0x006b
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.searchResult
                java.lang.Object r0 = r0.get(r8)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.LanguageSelectActivity r3 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r3 = r3.searchResult
                int r3 = r3.size()
                int r3 = r3 - r1
                if (r8 != r3) goto L_0x0069
            L_0x0067:
                r8 = 1
                goto L_0x00d1
            L_0x0069:
                r8 = 0
                goto L_0x00d1
            L_0x006b:
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x009f
                if (r8 < 0) goto L_0x009f
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                int r0 = r0.size()
                if (r8 >= r0) goto L_0x009f
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                java.lang.Object r0 = r0.get(r8)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.LanguageSelectActivity r3 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r3 = r3.unofficialLanguages
                int r3 = r3.size()
                int r3 = r3 - r1
                if (r8 != r3) goto L_0x0069
                goto L_0x0067
            L_0x009f:
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00b7
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                int r0 = r0.size()
                int r0 = r0 + r1
                int r8 = r8 - r0
            L_0x00b7:
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.sortedLanguages
                java.lang.Object r0 = r0.get(r8)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.LanguageSelectActivity r3 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r3 = r3.sortedLanguages
                int r3 = r3.size()
                int r3 = r3 - r1
                if (r8 != r3) goto L_0x0069
                goto L_0x0067
            L_0x00d1:
                boolean r3 = r0.isLocal()
                if (r3 == 0) goto L_0x00f4
                r3 = 2
                java.lang.Object[] r3 = new java.lang.Object[r3]
                java.lang.String r4 = r0.name
                r3[r2] = r4
                r4 = 2131625988(0x7f0e0804, float:1.88792E38)
                java.lang.String r5 = "LanguageCustom"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3[r1] = r4
                java.lang.String r4 = "%1$s (%2$s)"
                java.lang.String r3 = java.lang.String.format(r4, r3)
                r8 = r8 ^ r1
                r7.setLanguage(r0, r3, r8)
                goto L_0x00f9
            L_0x00f4:
                r3 = 0
                r8 = r8 ^ r1
                r7.setLanguage(r0, r3, r8)
            L_0x00f9:
                org.telegram.messenger.LocaleController r8 = org.telegram.messenger.LocaleController.getInstance()
                org.telegram.messenger.LocaleController$LocaleInfo r8 = r8.getCurrentLocaleInfo()
                if (r0 != r8) goto L_0x0104
                goto L_0x0105
            L_0x0104:
                r1 = 0
            L_0x0105:
                r7.setLanguageSelected(r1)
            L_0x0108:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LanguageSelectActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
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
