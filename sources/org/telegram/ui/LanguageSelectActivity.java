package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Collection$EL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextRadioCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
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
                        return;
                    }
                    return;
                }
                boolean unused2 = LanguageSelectActivity.this.searching = false;
                boolean unused3 = LanguageSelectActivity.this.searchWas = false;
                if (LanguageSelectActivity.this.listView != null) {
                    LanguageSelectActivity.this.emptyView.setVisibility(8);
                    LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.listAdapter);
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new LanguageSelectActivity$$ExternalSyntheticLambda6(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new LanguageSelectActivity$$ExternalSyntheticLambda7(this));
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
    public /* synthetic */ void lambda$createView$1(View view, int i) {
        LocaleController.LocaleInfo localeInfo;
        try {
            if (getParentActivity() != null && this.parentLayout != null) {
                if (view instanceof TextRadioCell) {
                    boolean z = this.listView.getAdapter() == this.searchListViewAdapter;
                    if (!z) {
                        i -= 2;
                    }
                    if (z) {
                        localeInfo = this.searchResult.get(i);
                    } else if (this.unofficialLanguages.isEmpty() || i < 0 || i >= this.unofficialLanguages.size()) {
                        if (!this.unofficialLanguages.isEmpty()) {
                            i -= this.unofficialLanguages.size() + 1;
                        }
                        localeInfo = this.sortedLanguages.get(i);
                    } else {
                        localeInfo = this.unofficialLanguages.get(i);
                    }
                    if (localeInfo != null) {
                        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
                        LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, this.currentAccount);
                        this.parentLayout.rebuildAllFragmentViews(false, false);
                        String str = localeInfo.pluralLangCode;
                        String str2 = currentLocaleInfo.pluralLangCode;
                        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                        HashSet<String> restrictedLanguages = RestrictedLanguagesSelectActivity.getRestrictedLanguages();
                        HashSet hashSet = new HashSet(restrictedLanguages);
                        if (restrictedLanguages.contains(str)) {
                            Collection$EL.removeIf(hashSet, new LanguageSelectActivity$$ExternalSyntheticLambda5(str));
                            if (!restrictedLanguages.contains(str2)) {
                                hashSet.add(str2);
                            }
                        }
                        globalMainSettings.edit().putStringSet("translate_button_restricted_languages", hashSet).apply();
                        finishFragment();
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(String str, String str2) {
        return str2 != null && str2.equals(str);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$3(View view, int i) {
        LocaleController.LocaleInfo localeInfo;
        try {
            if (!(getParentActivity() == null || this.parentLayout == null)) {
                if (view instanceof TextRadioCell) {
                    boolean z = this.listView.getAdapter() == this.searchListViewAdapter;
                    if (!z) {
                        i -= 2;
                    }
                    if (z) {
                        localeInfo = this.searchResult.get(i);
                    } else if (this.unofficialLanguages.isEmpty() || i < 0 || i >= this.unofficialLanguages.size()) {
                        if (!this.unofficialLanguages.isEmpty()) {
                            i -= this.unofficialLanguages.size() + 1;
                        }
                        localeInfo = this.sortedLanguages.get(i);
                    } else {
                        localeInfo = this.unofficialLanguages.get(i);
                    }
                    if (!(localeInfo == null || localeInfo.pathToFile == null)) {
                        if (!localeInfo.isRemote() || localeInfo.serverIndex == Integer.MAX_VALUE) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                            builder.setTitle(LocaleController.getString("DeleteLocalizationTitle", NUM));
                            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DeleteLocalizationText", NUM, localeInfo.name)));
                            builder.setPositiveButton(LocaleController.getString("Delete", NUM), new LanguageSelectActivity$$ExternalSyntheticLambda0(this, localeInfo));
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
                }
            }
            return false;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
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
            AndroidUtilities.runOnUIThread(new LanguageSelectActivity$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$4() {
        this.listAdapter.notifyDataSetChanged();
    }

    private void fillLanguages() {
        LanguageSelectActivity$$ExternalSyntheticLambda4 languageSelectActivity$$ExternalSyntheticLambda4 = new LanguageSelectActivity$$ExternalSyntheticLambda4(LocaleController.getInstance().getCurrentLocaleInfo());
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
        Collections.sort(this.sortedLanguages, languageSelectActivity$$ExternalSyntheticLambda4);
        Collections.sort(this.unofficialLanguages, languageSelectActivity$$ExternalSyntheticLambda4);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$fillLanguages$5(LocaleController.LocaleInfo localeInfo, LocaleController.LocaleInfo localeInfo2, LocaleController.LocaleInfo localeInfo3) {
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
            this.searching = false;
            this.searchResult = null;
            if (this.listView != null) {
                this.emptyView.setVisibility(8);
                this.listView.setAdapter(this.listAdapter);
                return;
            }
            return;
        }
        processSearch(str);
    }

    private void processSearch(String str) {
        Utilities.searchQueue.postRunnable(new LanguageSelectActivity$$ExternalSyntheticLambda2(this, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processSearch$6(String str) {
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
        AndroidUtilities.runOnUIThread(new LanguageSelectActivity$$ExternalSyntheticLambda3(this, arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSearchResults$7(ArrayList arrayList) {
        this.searchResult = arrayList;
        this.searchListViewAdapter.notifyDataSetChanged();
    }

    private class TranslateSettings extends LinearLayout {
        private TextSettingsCell doNotTranslateCell;
        private ValueAnimator doNotTranslateCellAnimation = null;
        private HeaderCell header;
        private TextInfoPrivacyCell info;
        private TextInfoPrivacyCell info2;
        private SharedPreferences.OnSharedPreferenceChangeListener listener;
        /* access modifiers changed from: private */
        public SharedPreferences preferences;
        private TextCheckCell showButtonCheck;

        public TranslateSettings(Context context) {
            super(context);
            boolean z = true;
            setOrientation(1);
            this.preferences = MessagesController.getGlobalMainSettings();
            HeaderCell headerCell = new HeaderCell(context);
            this.header = headerCell;
            headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.header.setText(LocaleController.getString("TranslateMessages", NUM));
            addView(this.header, LayoutHelper.createLinear(-1, -2));
            boolean value = getValue();
            TextCheckCell textCheckCell = new TextCheckCell(context);
            this.showButtonCheck = textCheckCell;
            textCheckCell.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("windowBackgroundWhite"), Theme.getColor("listSelectorSDK21")));
            this.showButtonCheck.setTextAndCheck(LocaleController.getString("ShowTranslateButton", NUM), value, value);
            this.showButtonCheck.setOnClickListener(new LanguageSelectActivity$TranslateSettings$$ExternalSyntheticLambda1(this));
            addView(this.showButtonCheck, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            this.doNotTranslateCell = textSettingsCell;
            textSettingsCell.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("windowBackgroundWhite"), Theme.getColor("listSelectorSDK21")));
            this.doNotTranslateCell.setOnClickListener(new LanguageSelectActivity$TranslateSettings$$ExternalSyntheticLambda2(this));
            this.doNotTranslateCell.setClickable((!value || !LanguageDetector.hasSupport()) ? false : z);
            float f = 1.0f;
            this.doNotTranslateCell.setAlpha((!value || !LanguageDetector.hasSupport()) ? 0.0f : 1.0f);
            addView(this.doNotTranslateCell, LayoutHelper.createLinear(-1, -2));
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
            this.info = textInfoPrivacyCell;
            textInfoPrivacyCell.setTopPadding(11);
            this.info.setBottomPadding(16);
            this.info.setText(LocaleController.getString("TranslateMessagesInfo1", NUM));
            addView(this.info, LayoutHelper.createLinear(-1, -2));
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
            this.info2 = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setTopPadding(0);
            this.info2.setBottomPadding(16);
            this.info2.setText(LocaleController.getString("TranslateMessagesInfo2", NUM));
            this.info2.setAlpha(value ? 0.0f : f);
            addView(this.info2, LayoutHelper.createLinear(-1, -2));
            updateHeight();
            update();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            this.preferences.edit().putBoolean("translate_button", !getValue()).apply();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            LanguageSelectActivity.this.presentFragment(new RestrictedLanguagesSelectActivity());
            update();
        }

        private boolean getValue() {
            return this.preferences.getBoolean("translate_button", false);
        }

        private ArrayList<String> getRestrictedLanguages() {
            String str = LocaleController.getInstance().getCurrentLocaleInfo().pluralLangCode;
            ArrayList<String> arrayList = new ArrayList<>(RestrictedLanguagesSelectActivity.getRestrictedLanguages());
            if (!arrayList.contains(str)) {
                arrayList.add(str);
            }
            return arrayList;
        }

        public void update() {
            boolean z = getValue() && LanguageDetector.hasSupport();
            this.showButtonCheck.setChecked(getValue());
            ValueAnimator valueAnimator = this.doNotTranslateCellAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.showButtonCheck.setDivider(z);
            ArrayList<String> restrictedLanguages = getRestrictedLanguages();
            String str = null;
            if (restrictedLanguages.size() == 1) {
                try {
                    str = LocaleController.getInstance().getLanguageFromDict(restrictedLanguages.get(0)).name;
                } catch (Exception unused) {
                }
            }
            if (str == null) {
                str = String.format(LocaleController.getPluralString("Languages", getRestrictedLanguages().size()), new Object[]{Integer.valueOf(getRestrictedLanguages().size())});
            }
            this.doNotTranslateCell.setTextAndValue(LocaleController.getString("DoNotTranslate", NUM), str, false);
            this.doNotTranslateCell.setClickable(z);
            float[] fArr = new float[2];
            fArr[0] = this.doNotTranslateCell.getAlpha();
            float f = 1.0f;
            fArr[1] = z ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.doNotTranslateCellAnimation = ofFloat;
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.doNotTranslateCellAnimation.addUpdateListener(new LanguageSelectActivity$TranslateSettings$$ExternalSyntheticLambda0(this));
            ValueAnimator valueAnimator2 = this.doNotTranslateCellAnimation;
            float alpha = this.doNotTranslateCell.getAlpha();
            if (!z) {
                f = 0.0f;
            }
            valueAnimator2.setDuration((long) (Math.abs(alpha - f) * 200.0f));
            this.doNotTranslateCellAnimation.start();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$update$2(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.doNotTranslateCell.setAlpha(floatValue);
            float f = 1.0f - floatValue;
            this.doNotTranslateCell.setTranslationY(((float) (-AndroidUtilities.dp(8.0f))) * f);
            this.info.setTranslationY(((float) (-this.doNotTranslateCell.getHeight())) * f);
            this.info2.setAlpha(f);
            this.info2.setTranslationY(((float) (-this.doNotTranslateCell.getHeight())) * f);
        }

        /* access modifiers changed from: protected */
        public void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            updateHeight();
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            updateHeight();
            super.onLayout(z, i, i2, i3, i4);
        }

        /* access modifiers changed from: package-private */
        public void updateHeight() {
            int i = 0;
            this.header.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, NUM), 0);
            this.showButtonCheck.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, NUM), 0);
            this.doNotTranslateCell.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, NUM), 0);
            this.info.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, NUM), 0);
            this.info2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, NUM), 0);
            if (!LanguageSelectActivity.this.searching) {
                i = height();
            }
            if (getLayoutParams() == null) {
                setLayoutParams(new RecyclerView.LayoutParams(-1, i));
            } else if (getLayoutParams().height != i) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) getLayoutParams();
                layoutParams.height = i;
                setLayoutParams(layoutParams);
            }
        }

        /* access modifiers changed from: package-private */
        public int height() {
            return Math.max(AndroidUtilities.dp(40.0f), this.header.getMeasuredHeight()) + Math.max(AndroidUtilities.dp(50.0f), this.showButtonCheck.getMeasuredHeight()) + Math.max(Math.max(AndroidUtilities.dp(50.0f), this.doNotTranslateCell.getMeasuredHeight()), this.info2.getMeasuredHeight() <= 0 ? AndroidUtilities.dp(51.0f) : this.info2.getMeasuredHeight()) + (this.info.getMeasuredHeight() <= 0 ? AndroidUtilities.dp(62.0f) : this.info.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            updateHeight();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            update();
            SharedPreferences sharedPreferences = this.preferences;
            AnonymousClass1 r1 = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
                    SharedPreferences unused = TranslateSettings.this.preferences = sharedPreferences;
                    TranslateSettings.this.update();
                }
            };
            this.listener = r1;
            sharedPreferences.registerOnSharedPreferenceChangeListener(r1);
            updateHeight();
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.preferences.unregisterOnSharedPreferenceChangeListener(this.listener);
        }
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
                if (!LanguageSelectActivity.this.unofficialLanguages.isEmpty()) {
                    size += LanguageSelectActivity.this.unofficialLanguages.size() + 1;
                }
                return size + 2;
            } else if (LanguageSelectActivity.this.searchResult == null) {
                return 0;
            } else {
                return LanguageSelectActivity.this.searchResult.size();
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.ui.Cells.TextRadioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: org.telegram.ui.Cells.TextRadioCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: org.telegram.ui.Cells.TextRadioCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r2, int r3) {
            /*
                r1 = this;
                java.lang.String r2 = "windowBackgroundWhite"
                if (r3 == 0) goto L_0x0037
                r0 = 2
                if (r3 == r0) goto L_0x002d
                r0 = 3
                if (r3 == r0) goto L_0x0012
                org.telegram.ui.Cells.ShadowSectionCell r2 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r3 = r1.mContext
                r2.<init>(r3)
                goto L_0x0046
            L_0x0012:
                org.telegram.ui.Cells.HeaderCell r3 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r0 = r1.mContext
                r3.<init>(r0)
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r3.setBackgroundColor(r2)
                r2 = 2131626215(0x7f0e08e7, float:1.887966E38)
                java.lang.String r0 = "Language"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r0, r2)
                r3.setText(r2)
                goto L_0x0045
            L_0x002d:
                org.telegram.ui.LanguageSelectActivity$TranslateSettings r2 = new org.telegram.ui.LanguageSelectActivity$TranslateSettings
                org.telegram.ui.LanguageSelectActivity r3 = org.telegram.ui.LanguageSelectActivity.this
                android.content.Context r0 = r1.mContext
                r2.<init>(r0)
                goto L_0x0046
            L_0x0037:
                org.telegram.ui.Cells.TextRadioCell r3 = new org.telegram.ui.Cells.TextRadioCell
                android.content.Context r0 = r1.mContext
                r3.<init>(r0)
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r3.setBackgroundColor(r2)
            L_0x0045:
                r2 = r3
            L_0x0046:
                org.telegram.ui.Components.RecyclerListView$Holder r3 = new org.telegram.ui.Components.RecyclerListView$Holder
                r3.<init>(r2)
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LanguageSelectActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: int} */
        /* JADX WARNING: type inference failed for: r2v0 */
        /* JADX WARNING: type inference failed for: r2v2 */
        /* JADX WARNING: type inference failed for: r2v4 */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0088, code lost:
            if (r12 == (org.telegram.ui.LanguageSelectActivity.access$700(r10.this$0).size() - 1)) goto L_0x008a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x008c, code lost:
            r12 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x00bf, code lost:
            if (r12 == (org.telegram.ui.LanguageSelectActivity.access$900(r10.this$0).size() - 1)) goto L_0x008a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x00f1, code lost:
            if (r12 == (org.telegram.ui.LanguageSelectActivity.access$800(r10.this$0).size() - 1)) goto L_0x008a;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
                r10 = this;
                int r0 = r11.getItemViewType()
                r1 = 2
                r2 = 0
                r3 = 1
                if (r0 == 0) goto L_0x0065
                if (r0 == r3) goto L_0x0025
                if (r0 == r1) goto L_0x000f
                goto L_0x0136
            L_0x000f:
                android.view.View r11 = r11.itemView
                org.telegram.ui.LanguageSelectActivity$TranslateSettings r11 = (org.telegram.ui.LanguageSelectActivity.TranslateSettings) r11
                org.telegram.ui.LanguageSelectActivity r12 = org.telegram.ui.LanguageSelectActivity.this
                boolean r12 = r12.searching
                if (r12 == 0) goto L_0x001d
                r2 = 8
            L_0x001d:
                r11.setVisibility(r2)
                r11.updateHeight()
                goto L_0x0136
            L_0x0025:
                boolean r0 = r10.search
                if (r0 != 0) goto L_0x002b
                int r12 = r12 + -1
            L_0x002b:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.ShadowSectionCell r11 = (org.telegram.ui.Cells.ShadowSectionCell) r11
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                boolean r0 = r0.isEmpty()
                java.lang.String r1 = "windowBackgroundGrayShadow"
                if (r0 != 0) goto L_0x0057
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                int r0 = r0.size()
                if (r12 != r0) goto L_0x0057
                android.content.Context r12 = r10.mContext
                r0 = 2131165477(0x7var_, float:1.7945172E38)
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r0, (java.lang.String) r1)
                r11.setBackgroundDrawable(r12)
                goto L_0x0136
            L_0x0057:
                android.content.Context r12 = r10.mContext
                r0 = 2131165478(0x7var_, float:1.7945174E38)
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r0, (java.lang.String) r1)
                r11.setBackgroundDrawable(r12)
                goto L_0x0136
            L_0x0065:
                boolean r0 = r10.search
                if (r0 != 0) goto L_0x006b
                int r12 = r12 + -2
            L_0x006b:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextRadioCell r11 = (org.telegram.ui.Cells.TextRadioCell) r11
                if (r0 == 0) goto L_0x008e
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.searchResult
                java.lang.Object r0 = r0.get(r12)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.LanguageSelectActivity r4 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r4 = r4.searchResult
                int r4 = r4.size()
                int r4 = r4 - r3
                if (r12 != r4) goto L_0x008c
            L_0x008a:
                r12 = 1
                goto L_0x00f4
            L_0x008c:
                r12 = 0
                goto L_0x00f4
            L_0x008e:
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00c2
                if (r12 < 0) goto L_0x00c2
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                int r0 = r0.size()
                if (r12 >= r0) goto L_0x00c2
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                java.lang.Object r0 = r0.get(r12)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.LanguageSelectActivity r4 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r4 = r4.unofficialLanguages
                int r4 = r4.size()
                int r4 = r4 - r3
                if (r12 != r4) goto L_0x008c
                goto L_0x008a
            L_0x00c2:
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x00da
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.unofficialLanguages
                int r0 = r0.size()
                int r0 = r0 + r3
                int r12 = r12 - r0
            L_0x00da:
                org.telegram.ui.LanguageSelectActivity r0 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r0 = r0.sortedLanguages
                java.lang.Object r0 = r0.get(r12)
                org.telegram.messenger.LocaleController$LocaleInfo r0 = (org.telegram.messenger.LocaleController.LocaleInfo) r0
                org.telegram.ui.LanguageSelectActivity r4 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r4 = r4.sortedLanguages
                int r4 = r4.size()
                int r4 = r4 - r3
                if (r12 != r4) goto L_0x008c
                goto L_0x008a
            L_0x00f4:
                boolean r4 = r0.isLocal()
                if (r4 == 0) goto L_0x011c
                java.lang.Object[] r1 = new java.lang.Object[r1]
                java.lang.String r4 = r0.name
                r1[r2] = r4
                r4 = 2131626218(0x7f0e08ea, float:1.8879666E38)
                java.lang.String r5 = "LanguageCustom"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r1[r3] = r4
                java.lang.String r4 = "%1$s (%2$s)"
                java.lang.String r5 = java.lang.String.format(r4, r1)
                java.lang.String r6 = r0.nameEnglish
                r7 = 0
                r8 = 0
                r9 = r12 ^ 1
                r4 = r11
                r4.setTextAndValueAndCheck(r5, r6, r7, r8, r9)
                goto L_0x0128
            L_0x011c:
                java.lang.String r5 = r0.name
                java.lang.String r6 = r0.nameEnglish
                r7 = 0
                r8 = 0
                r9 = r12 ^ 1
                r4 = r11
                r4.setTextAndValueAndCheck(r5, r6, r7, r8, r9)
            L_0x0128:
                org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
                org.telegram.messenger.LocaleController$LocaleInfo r12 = r12.getCurrentLocaleInfo()
                if (r0 != r12) goto L_0x0133
                r2 = 1
            L_0x0133:
                r11.setChecked(r2)
            L_0x0136:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LanguageSelectActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            boolean z = this.search;
            if (!z) {
                i -= 2;
            }
            if (i == -2) {
                return 2;
            }
            if (i == -1) {
                return 3;
            }
            if (z) {
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
