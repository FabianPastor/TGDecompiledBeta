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
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
        addItem.setIsSearchField(true);
        addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
        });
        addItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                LanguageSelectActivity.this.lambda$createView$0$LanguageSelectActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return LanguageSelectActivity.this.lambda$createView$2$LanguageSelectActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$LanguageSelectActivity(View view, int i) {
        if (getParentActivity() != null && this.parentLayout != null && (view instanceof LanguageCell)) {
            LocaleController.LocaleInfo currentLocale = ((LanguageCell) view).getCurrentLocale();
            if (currentLocale != null) {
                LocaleController.getInstance().applyLanguage(currentLocale, true, false, false, true, this.currentAccount);
                this.parentLayout.rebuildAllFragmentViews(false, false);
            }
            finishFragment();
        }
    }

    public /* synthetic */ boolean lambda$createView$2$LanguageSelectActivity(View view, int i) {
        LocaleController.LocaleInfo currentLocale;
        if (getParentActivity() == null || this.parentLayout == null || !(view instanceof LanguageCell) || (currentLocale = ((LanguageCell) view).getCurrentLocale()) == null || currentLocale.pathToFile == null || (currentLocale.isRemote() && currentLocale.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("DeleteLocalizationTitle", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DeleteLocalizationText", NUM, currentLocale.name)));
        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener(currentLocale) {
            private final /* synthetic */ LocaleController.LocaleInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                LanguageSelectActivity.this.lambda$null$1$LanguageSelectActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
        return true;
    }

    public /* synthetic */ void lambda$null$1$LanguageSelectActivity(LocaleController.LocaleInfo localeInfo, DialogInterface dialogInterface, int i) {
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
        $$Lambda$LanguageSelectActivity$bKvkTCAKOIGl0v4A4OGKsEAtQUU r1 = new Comparator() {
            public final int compare(Object obj, Object obj2) {
                return LanguageSelectActivity.lambda$fillLanguages$3(LocaleController.LocaleInfo.this, (LocaleController.LocaleInfo) obj, (LocaleController.LocaleInfo) obj2);
            }
        };
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
        Collections.sort(this.sortedLanguages, r1);
        Collections.sort(this.unofficialLanguages, r1);
    }

    static /* synthetic */ int lambda$fillLanguages$3(LocaleController.LocaleInfo localeInfo, LocaleController.LocaleInfo localeInfo2, LocaleController.LocaleInfo localeInfo3) {
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
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Timer timer = new Timer();
        this.searchTimer = timer;
        timer.schedule(new TimerTask() {
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
        Utilities.searchQueue.postRunnable(new Runnable(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LanguageSelectActivity.this.lambda$processSearch$4$LanguageSelectActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$processSearch$4$LanguageSelectActivity(String str) {
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
        AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LanguageSelectActivity.this.lambda$updateSearchResults$5$LanguageSelectActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$updateSearchResults$5$LanguageSelectActivity(ArrayList arrayList) {
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

        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0066, code lost:
            if (r8 == (org.telegram.ui.LanguageSelectActivity.access$800(r6.this$0).size() - 1)) goto L_0x0068;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x006a, code lost:
            r8 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x009d, code lost:
            if (r8 == (org.telegram.ui.LanguageSelectActivity.access$1000(r6.this$0).size() - 1)) goto L_0x0068;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x00cf, code lost:
            if (r8 == (org.telegram.ui.LanguageSelectActivity.access$900(r6.this$0).size() - 1)) goto L_0x0068;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r7, int r8) {
            /*
                r6 = this;
                int r0 = r7.getItemViewType()
                r1 = 1
                if (r0 == 0) goto L_0x0046
                if (r0 == r1) goto L_0x000b
                goto L_0x0109
            L_0x000b:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.ShadowSectionCell r7 = (org.telegram.ui.Cells.ShadowSectionCell) r7
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                boolean r0 = r0.isEmpty()
                java.lang.String r1 = "windowBackgroundGrayShadow"
                if (r0 != 0) goto L_0x0038
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                int r0 = r0.size()
                if (r8 != r0) goto L_0x0038
                android.content.Context r8 = r6.mContext
                r0 = 2131165417(0x7var_e9, float:1.794505E38)
                android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r0, (java.lang.String) r1)
                r7.setBackgroundDrawable(r8)
                goto L_0x0109
            L_0x0038:
                android.content.Context r8 = r6.mContext
                r0 = 2131165418(0x7var_ea, float:1.7945053E38)
                android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r8, (int) r0, (java.lang.String) r1)
                r7.setBackgroundDrawable(r8)
                goto L_0x0109
            L_0x0046:
                android.view.View r7 = r7.itemView
                org.telegram.ui.Cells.LanguageCell r7 = (org.telegram.ui.Cells.LanguageCell) r7
                boolean r0 = r6.search
                r2 = 0
                if (r0 == 0) goto L_0x006c
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.searchResult
                java.lang.Object r0 = r0.get(r8)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.LanguageSelectActivity r3 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r3 = r3.searchResult
                int r3 = r3.size()
                int r3 = r3 - r1
                if (r8 != r3) goto L_0x006a
            L_0x0068:
                r8 = 1
                goto L_0x00d2
            L_0x006a:
                r8 = 0
                goto L_0x00d2
            L_0x006c:
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00a0
                if (r8 < 0) goto L_0x00a0
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                int r0 = r0.size()
                if (r8 >= r0) goto L_0x00a0
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                java.lang.Object r0 = r0.get(r8)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.LanguageSelectActivity r3 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r3 = r3.unofficialLanguages
                int r3 = r3.size()
                int r3 = r3 - r1
                if (r8 != r3) goto L_0x006a
                goto L_0x0068
            L_0x00a0:
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00b8
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                int r0 = r0.size()
                int r0 = r0 + r1
                int r8 = r8 - r0
            L_0x00b8:
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.sortedLanguages
                java.lang.Object r0 = r0.get(r8)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.LanguageSelectActivity r3 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r3 = r3.sortedLanguages
                int r3 = r3.size()
                int r3 = r3 - r1
                if (r8 != r3) goto L_0x006a
                goto L_0x0068
            L_0x00d2:
                boolean r3 = r0.isLocal()
                if (r3 == 0) goto L_0x00f5
                r3 = 2
                java.lang.Object[] r3 = new java.lang.Object[r3]
                java.lang.String r4 = r0.name
                r3[r2] = r4
                r4 = 2131625511(0x7f0e0627, float:1.8878232E38)
                java.lang.String r5 = "LanguageCustom"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3[r1] = r4
                java.lang.String r4 = "%1$s (%2$s)"
                java.lang.String r3 = java.lang.String.format(r4, r3)
                r8 = r8 ^ r1
                r7.setLanguage(r0, r3, r8)
                goto L_0x00fa
            L_0x00f5:
                r3 = 0
                r8 = r8 ^ r1
                r7.setLanguage(r0, r3, r8)
            L_0x00fa:
                org.telegram.messenger.LocaleController r8 = org.telegram.messenger.LocaleController.getInstance()
                org.telegram.messenger.LocaleController$LocaleInfo r8 = r8.getCurrentLocaleInfo()
                if (r0 != r8) goto L_0x0105
                goto L_0x0106
            L_0x0105:
                r1 = 0
            L_0x0106:
                r7.setLanguageSelected(r1)
            L_0x0109:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LanguageSelectActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            return ((LanguageSelectActivity.this.unofficialLanguages.isEmpty() || !(i == LanguageSelectActivity.this.unofficialLanguages.size() || i == (LanguageSelectActivity.this.unofficialLanguages.size() + LanguageSelectActivity.this.sortedLanguages.size()) + 1)) && (!LanguageSelectActivity.this.unofficialLanguages.isEmpty() || i != LanguageSelectActivity.this.sortedLanguages.size())) ? 0 : 1;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LanguageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"), new ThemeDescription((View) this.listView, 0, new Class[]{LanguageCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon")};
    }
}
