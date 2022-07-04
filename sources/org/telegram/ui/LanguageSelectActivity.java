package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Timer;
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
    private Timer searchTimer;
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
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount, false);
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new LanguageSelectActivity$$ExternalSyntheticLambda6(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new LanguageSelectActivity$$ExternalSyntheticLambda7(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3618lambda$createView$1$orgtelegramuiLanguageSelectActivity(View view, int position) {
        LocaleController.LocaleInfo localeInfo;
        try {
            if (getParentActivity() != null && this.parentLayout != null) {
                if (view instanceof TextRadioCell) {
                    boolean search = this.listView.getAdapter() == this.searchListViewAdapter;
                    if (!search) {
                        position -= 2;
                    }
                    if (search) {
                        localeInfo = this.searchResult.get(position);
                    } else if (this.unofficialLanguages.isEmpty() || position < 0 || position >= this.unofficialLanguages.size()) {
                        if (!this.unofficialLanguages.isEmpty()) {
                            position -= this.unofficialLanguages.size() + 1;
                        }
                        localeInfo = this.sortedLanguages.get(position);
                    } else {
                        localeInfo = this.unofficialLanguages.get(position);
                    }
                    if (localeInfo != null) {
                        LocaleController.LocaleInfo prevLocale = LocaleController.getInstance().getCurrentLocaleInfo();
                        LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, this.currentAccount);
                        this.parentLayout.rebuildAllFragmentViews(false, false);
                        String langCode = localeInfo.pluralLangCode;
                        String prevLangCode = prevLocale.pluralLangCode;
                        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                        HashSet<String> selectedLanguages = RestrictedLanguagesSelectActivity.getRestrictedLanguages();
                        HashSet<String> newSelectedLanguages = new HashSet<>(selectedLanguages);
                        if (selectedLanguages.contains(langCode)) {
                            Collection.EL.removeIf(newSelectedLanguages, new LanguageSelectActivity$$ExternalSyntheticLambda5(langCode));
                            if (!selectedLanguages.contains(prevLangCode)) {
                                newSelectedLanguages.add(prevLangCode);
                            }
                        }
                        preferences.edit().putStringSet("translate_button_restricted_languages", newSelectedLanguages).apply();
                        finishFragment();
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    static /* synthetic */ boolean lambda$createView$0(String langCode, String s) {
        return s != null && s.equals(langCode);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ boolean m3620lambda$createView$3$orgtelegramuiLanguageSelectActivity(View view, int position) {
        LocaleController.LocaleInfo localeInfo;
        try {
            if (!(getParentActivity() == null || this.parentLayout == null)) {
                if (view instanceof TextRadioCell) {
                    boolean search = this.listView.getAdapter() == this.searchListViewAdapter;
                    if (!search) {
                        position -= 2;
                    }
                    if (search) {
                        localeInfo = this.searchResult.get(position);
                    } else if (this.unofficialLanguages.isEmpty() || position < 0 || position >= this.unofficialLanguages.size()) {
                        if (!this.unofficialLanguages.isEmpty()) {
                            position -= this.unofficialLanguages.size() + 1;
                        }
                        localeInfo = this.sortedLanguages.get(position);
                    } else {
                        localeInfo = this.unofficialLanguages.get(position);
                    }
                    if (!(localeInfo == null || localeInfo.pathToFile == null)) {
                        if (!localeInfo.isRemote() || localeInfo.serverIndex == Integer.MAX_VALUE) {
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
                    }
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3619lambda$createView$2$orgtelegramuiLanguageSelectActivity(LocaleController.LocaleInfo finalLocaleInfo, DialogInterface dialogInterface, int i) {
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
            AndroidUtilities.runOnUIThread(new LanguageSelectActivity$$ExternalSyntheticLambda1(this));
        }
    }

    /* renamed from: lambda$didReceivedNotification$4$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3621x8b9d827f() {
        this.listAdapter.notifyDataSetChanged();
    }

    private void fillLanguages() {
        Comparator<LocaleController.LocaleInfo> comparator = new LanguageSelectActivity$$ExternalSyntheticLambda4(LocaleController.getInstance().getCurrentLocaleInfo());
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

    static /* synthetic */ int lambda$fillLanguages$5(LocaleController.LocaleInfo currentLocale, LocaleController.LocaleInfo o, LocaleController.LocaleInfo o2) {
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
            this.searching = false;
            this.searchResult = null;
            if (this.listView != null) {
                this.emptyView.setVisibility(8);
                this.listView.setAdapter(this.listAdapter);
                return;
            }
            return;
        }
        processSearch(query);
    }

    private void processSearch(String query) {
        Utilities.searchQueue.postRunnable(new LanguageSelectActivity$$ExternalSyntheticLambda2(this, query));
    }

    /* renamed from: lambda$processSearch$6$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3622lambda$processSearch$6$orgtelegramuiLanguageSelectActivity(String query) {
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
        AndroidUtilities.runOnUIThread(new LanguageSelectActivity$$ExternalSyntheticLambda3(this, arrCounties));
    }

    /* renamed from: lambda$updateSearchResults$7$org-telegram-ui-LanguageSelectActivity  reason: not valid java name */
    public /* synthetic */ void m3623x9dcfe282(ArrayList arrCounties) {
        this.searchResult = arrCounties;
        this.searchListViewAdapter.notifyDataSetChanged();
    }

    private class TranslateSettings extends LinearLayout {
        /* access modifiers changed from: private */
        public TextSettingsCell doNotTranslateCell;
        private ValueAnimator doNotTranslateCellAnimation = null;
        private HeaderCell header;
        private TextInfoPrivacyCell info;
        /* access modifiers changed from: private */
        public TextInfoPrivacyCell info2;
        private SharedPreferences.OnSharedPreferenceChangeListener listener;
        /* access modifiers changed from: private */
        public SharedPreferences preferences;
        private TextCheckCell showButtonCheck;

        public TranslateSettings(Context context) {
            super(context);
            setFocusable(false);
            setOrientation(1);
            this.preferences = MessagesController.getGlobalMainSettings();
            HeaderCell headerCell = new HeaderCell(context);
            this.header = headerCell;
            headerCell.setFocusable(true);
            this.header.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.header.setText(LocaleController.getString("TranslateMessages", NUM));
            this.header.setContentDescription(LocaleController.getString("TranslateMessages", NUM));
            addView(this.header, LayoutHelper.createLinear(-1, -2));
            boolean value = getValue();
            TextCheckCell textCheckCell = new TextCheckCell(context);
            this.showButtonCheck = textCheckCell;
            textCheckCell.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor("windowBackgroundWhite")));
            this.showButtonCheck.setTextAndCheck(LocaleController.getString("ShowTranslateButton", NUM), value, value);
            this.showButtonCheck.setOnClickListener(new LanguageSelectActivity$TranslateSettings$$ExternalSyntheticLambda1(this));
            addView(this.showButtonCheck, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            this.doNotTranslateCell = textSettingsCell;
            textSettingsCell.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor("windowBackgroundWhite")));
            this.doNotTranslateCell.setOnClickListener(new LanguageSelectActivity$TranslateSettings$$ExternalSyntheticLambda2(this));
            this.doNotTranslateCell.setClickable(value && LanguageDetector.hasSupport());
            float f = 1.0f;
            this.doNotTranslateCell.setAlpha((!value || !LanguageDetector.hasSupport()) ? 0.0f : 1.0f);
            addView(this.doNotTranslateCell, LayoutHelper.createLinear(-1, -2));
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
            this.info = textInfoPrivacyCell;
            textInfoPrivacyCell.setTopPadding(11);
            this.info.setBottomPadding(16);
            this.info.setFocusable(true);
            this.info.setText(LocaleController.getString("TranslateMessagesInfo1", NUM));
            this.info.setContentDescription(LocaleController.getString("TranslateMessagesInfo1", NUM));
            addView(this.info, LayoutHelper.createLinear(-1, -2));
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
            this.info2 = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setTopPadding(0);
            this.info2.setBottomPadding(16);
            this.info2.setFocusable(true);
            this.info2.setText(LocaleController.getString("TranslateMessagesInfo2", NUM));
            this.info2.setContentDescription(LocaleController.getString("TranslateMessagesInfo2", NUM));
            this.info2.setAlpha(value ? 0.0f : f);
            addView(this.info2, LayoutHelper.createLinear(-1, -2));
            updateHeight();
            update();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LanguageSelectActivity$TranslateSettings  reason: not valid java name */
        public /* synthetic */ void m3624x92ea1a5b(View e) {
            this.preferences.edit().putBoolean("translate_button", !getValue()).apply();
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LanguageSelectActivity$TranslateSettings  reason: not valid java name */
        public /* synthetic */ void m3625x1fd7317a(View e) {
            LanguageSelectActivity.this.presentFragment(new RestrictedLanguagesSelectActivity());
            update();
        }

        private boolean getValue() {
            return this.preferences.getBoolean("translate_button", false);
        }

        private ArrayList<String> getRestrictedLanguages() {
            String currentLang = LocaleController.getInstance().getCurrentLocaleInfo().pluralLangCode;
            ArrayList<String> langCodes = new ArrayList<>(RestrictedLanguagesSelectActivity.getRestrictedLanguages());
            if (!langCodes.contains(currentLang)) {
                langCodes.add(currentLang);
            }
            return langCodes;
        }

        public void update() {
            boolean value = getValue() && LanguageDetector.hasSupport();
            this.showButtonCheck.setChecked(getValue());
            ValueAnimator valueAnimator = this.doNotTranslateCellAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.showButtonCheck.setDivider(value);
            ArrayList<String> langCodes = getRestrictedLanguages();
            String doNotTranslateCellValue = null;
            if (langCodes.size() == 1) {
                try {
                    doNotTranslateCellValue = LocaleController.getInstance().getLanguageFromDict(langCodes.get(0)).name;
                } catch (Exception e) {
                }
            }
            if (doNotTranslateCellValue == null) {
                doNotTranslateCellValue = String.format(LocaleController.getPluralString("Languages", getRestrictedLanguages().size()), new Object[]{Integer.valueOf(getRestrictedLanguages().size())});
            }
            this.doNotTranslateCell.setTextAndValue(LocaleController.getString("DoNotTranslate", NUM), doNotTranslateCellValue, false);
            this.doNotTranslateCell.setClickable(value);
            this.info2.setVisibility(0);
            float[] fArr = new float[2];
            fArr[0] = this.doNotTranslateCell.getAlpha();
            float f = 1.0f;
            fArr[1] = value ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.doNotTranslateCellAnimation = ofFloat;
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.doNotTranslateCellAnimation.addUpdateListener(new LanguageSelectActivity$TranslateSettings$$ExternalSyntheticLambda0(this));
            this.doNotTranslateCellAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (((double) TranslateSettings.this.doNotTranslateCell.getAlpha()) > 0.5d) {
                        TranslateSettings.this.info2.setVisibility(8);
                    } else {
                        TranslateSettings.this.info2.setVisibility(0);
                    }
                }
            });
            ValueAnimator valueAnimator2 = this.doNotTranslateCellAnimation;
            float alpha = this.doNotTranslateCell.getAlpha();
            if (!value) {
                f = 0.0f;
            }
            valueAnimator2.setDuration((long) (Math.abs(alpha - f) * 200.0f));
            this.doNotTranslateCellAnimation.start();
        }

        /* renamed from: lambda$update$2$org-telegram-ui-LanguageSelectActivity$TranslateSettings  reason: not valid java name */
        public /* synthetic */ void m3626xc1a1edb6(ValueAnimator a) {
            float t = ((Float) a.getAnimatedValue()).floatValue();
            this.doNotTranslateCell.setAlpha(t);
            this.doNotTranslateCell.setTranslationY(((float) (-AndroidUtilities.dp(8.0f))) * (1.0f - t));
            this.info.setTranslationY(((float) (-this.doNotTranslateCell.getHeight())) * (1.0f - t));
            this.info2.setAlpha(1.0f - t);
            this.info2.setTranslationY(((float) (-this.doNotTranslateCell.getHeight())) * (1.0f - t));
        }

        /* access modifiers changed from: protected */
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            updateHeight();
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            updateHeight();
            super.onLayout(changed, l, t, r, b);
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
            int newHeight = i;
            if (getLayoutParams() == null) {
                setLayoutParams(new RecyclerView.LayoutParams(-1, newHeight));
            } else if (getLayoutParams().height != newHeight) {
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) getLayoutParams();
                lp.height = newHeight;
                setLayoutParams(lp);
            }
        }

        /* access modifiers changed from: package-private */
        public int height() {
            return Math.max(AndroidUtilities.dp(40.0f), this.header.getMeasuredHeight()) + Math.max(AndroidUtilities.dp(50.0f), this.showButtonCheck.getMeasuredHeight()) + Math.max(Math.max(AndroidUtilities.dp(50.0f), this.doNotTranslateCell.getMeasuredHeight()), this.info2.getMeasuredHeight() <= 0 ? AndroidUtilities.dp(51.0f) : this.info2.getMeasuredHeight()) + (this.info.getMeasuredHeight() <= 0 ? AndroidUtilities.dp(62.0f) : this.info.getMeasuredHeight());
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            updateHeight();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            update();
            SharedPreferences sharedPreferences = this.preferences;
            AnonymousClass2 r1 = new SharedPreferences.OnSharedPreferenceChangeListener() {
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
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
                    count += LanguageSelectActivity.this.unofficialLanguages.size() + 1;
                }
                return count + 2;
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
                    View view2 = new TextRadioCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 2:
                    view = new TranslateSettings(this.mContext);
                    View view3 = view;
                    break;
                case 3:
                    HeaderCell header = new HeaderCell(this.mContext);
                    header.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    header.setText(LocaleController.getString("Language", NUM));
                    view = header;
                    break;
                default:
                    view = new ShadowSectionCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: int} */
        /* JADX WARNING: type inference failed for: r1v0 */
        /* JADX WARNING: type inference failed for: r1v2 */
        /* JADX WARNING: type inference failed for: r1v14 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r12, int r13) {
            /*
                r11 = this;
                int r0 = r12.getItemViewType()
                r1 = 0
                switch(r0) {
                    case 0: goto L_0x0060;
                    case 1: goto L_0x0020;
                    case 2: goto L_0x000a;
                    default: goto L_0x0008;
                }
            L_0x0008:
                goto L_0x0148
            L_0x000a:
                android.view.View r0 = r12.itemView
                org.telegram.ui.LanguageSelectActivity$TranslateSettings r0 = (org.telegram.ui.LanguageSelectActivity.TranslateSettings) r0
                org.telegram.ui.LanguageSelectActivity r2 = org.telegram.ui.LanguageSelectActivity.this
                boolean r2 = r2.searching
                if (r2 == 0) goto L_0x0018
                r1 = 8
            L_0x0018:
                r0.setVisibility(r1)
                r0.updateHeight()
                goto L_0x0148
            L_0x0020:
                boolean r0 = r11.search
                if (r0 != 0) goto L_0x0026
                int r13 = r13 + -1
            L_0x0026:
                android.view.View r0 = r12.itemView
                org.telegram.ui.Cells.ShadowSectionCell r0 = (org.telegram.ui.Cells.ShadowSectionCell) r0
                org.telegram.ui.LanguageSelectActivity r1 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r1 = r1.unofficialLanguages
                boolean r1 = r1.isEmpty()
                java.lang.String r2 = "windowBackgroundGrayShadow"
                if (r1 != 0) goto L_0x0052
                org.telegram.ui.LanguageSelectActivity r1 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r1 = r1.unofficialLanguages
                int r1 = r1.size()
                if (r13 != r1) goto L_0x0052
                android.content.Context r1 = r11.mContext
                r3 = 2131165435(0x7var_fb, float:1.7945087E38)
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r3, (java.lang.String) r2)
                r0.setBackgroundDrawable(r1)
                goto L_0x0148
            L_0x0052:
                android.content.Context r1 = r11.mContext
                r3 = 2131165436(0x7var_fc, float:1.794509E38)
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r3, (java.lang.String) r2)
                r0.setBackgroundDrawable(r1)
                goto L_0x0148
            L_0x0060:
                boolean r0 = r11.search
                if (r0 != 0) goto L_0x0066
                int r13 = r13 + -2
            L_0x0066:
                android.view.View r0 = r12.itemView
                org.telegram.ui.Cells.TextRadioCell r0 = (org.telegram.ui.Cells.TextRadioCell) r0
                boolean r2 = r11.search
                r8 = 1
                if (r2 == 0) goto L_0x008f
                org.telegram.ui.LanguageSelectActivity r2 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r2 = r2.searchResult
                java.lang.Object r2 = r2.get(r13)
                org.telegram.messenger.LocaleController$LocaleInfo r2 = (org.telegram.messenger.LocaleController.LocaleInfo) r2
                org.telegram.ui.LanguageSelectActivity r3 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r3 = r3.searchResult
                int r3 = r3.size()
                int r3 = r3 - r8
                if (r13 != r3) goto L_0x008a
                r3 = 1
                goto L_0x008b
            L_0x008a:
                r3 = 0
            L_0x008b:
                r9 = r2
                r10 = r3
                goto L_0x00fe
            L_0x008f:
                org.telegram.ui.LanguageSelectActivity r2 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r2 = r2.unofficialLanguages
                boolean r2 = r2.isEmpty()
                if (r2 != 0) goto L_0x00c8
                if (r13 < 0) goto L_0x00c8
                org.telegram.ui.LanguageSelectActivity r2 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r2 = r2.unofficialLanguages
                int r2 = r2.size()
                if (r13 >= r2) goto L_0x00c8
                org.telegram.ui.LanguageSelectActivity r2 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r2 = r2.unofficialLanguages
                java.lang.Object r2 = r2.get(r13)
                org.telegram.messenger.LocaleController$LocaleInfo r2 = (org.telegram.messenger.LocaleController.LocaleInfo) r2
                org.telegram.ui.LanguageSelectActivity r3 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r3 = r3.unofficialLanguages
                int r3 = r3.size()
                int r3 = r3 - r8
                if (r13 != r3) goto L_0x00c4
                r3 = 1
                goto L_0x00c5
            L_0x00c4:
                r3 = 0
            L_0x00c5:
                r9 = r2
                r10 = r3
                goto L_0x00fe
            L_0x00c8:
                org.telegram.ui.LanguageSelectActivity r2 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r2 = r2.unofficialLanguages
                boolean r2 = r2.isEmpty()
                if (r2 != 0) goto L_0x00e0
                org.telegram.ui.LanguageSelectActivity r2 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r2 = r2.unofficialLanguages
                int r2 = r2.size()
                int r2 = r2 + r8
                int r13 = r13 - r2
            L_0x00e0:
                org.telegram.ui.LanguageSelectActivity r2 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r2 = r2.sortedLanguages
                java.lang.Object r2 = r2.get(r13)
                org.telegram.messenger.LocaleController$LocaleInfo r2 = (org.telegram.messenger.LocaleController.LocaleInfo) r2
                org.telegram.ui.LanguageSelectActivity r3 = org.telegram.ui.LanguageSelectActivity.this
                java.util.ArrayList r3 = r3.sortedLanguages
                int r3 = r3.size()
                int r3 = r3 - r8
                if (r13 != r3) goto L_0x00fb
                r3 = 1
                goto L_0x00fc
            L_0x00fb:
                r3 = 0
            L_0x00fc:
                r9 = r2
                r10 = r3
            L_0x00fe:
                boolean r2 = r9.isLocal()
                if (r2 == 0) goto L_0x012a
                r2 = 2
                java.lang.Object[] r2 = new java.lang.Object[r2]
                java.lang.String r3 = r9.name
                r2[r1] = r3
                r3 = 2131626349(0x7f0e096d, float:1.8879932E38)
                java.lang.String r4 = "LanguageCustom"
                java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                r2[r8] = r3
                java.lang.String r3 = "%1$s (%2$s)"
                java.lang.String r3 = java.lang.String.format(r3, r2)
                java.lang.String r4 = r9.nameEnglish
                r5 = 0
                r6 = 0
                if (r10 != 0) goto L_0x0124
                r7 = 1
                goto L_0x0125
            L_0x0124:
                r7 = 0
            L_0x0125:
                r2 = r0
                r2.setTextAndValueAndCheck(r3, r4, r5, r6, r7)
                goto L_0x0139
            L_0x012a:
                java.lang.String r3 = r9.name
                java.lang.String r4 = r9.nameEnglish
                r5 = 0
                r6 = 0
                if (r10 != 0) goto L_0x0134
                r7 = 1
                goto L_0x0135
            L_0x0134:
                r7 = 0
            L_0x0135:
                r2 = r0
                r2.setTextAndValueAndCheck(r3, r4, r5, r6, r7)
            L_0x0139:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
                org.telegram.messenger.LocaleController$LocaleInfo r2 = r2.getCurrentLocaleInfo()
                if (r9 != r2) goto L_0x0144
                r1 = 1
            L_0x0144:
                r0.setChecked(r1)
            L_0x0148:
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
