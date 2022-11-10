package org.telegram.ui.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$MessagesFilter;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterRoundVoice;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class FiltersView extends RecyclerListView {
    DiffUtil.Callback diffUtilsCallback;
    LinearLayoutManager layoutManager;
    private ArrayList<MediaFilterData> oldItems;
    private ArrayList<MediaFilterData> usersFilters;
    public static final MediaFilterData[] filters = {new MediaFilterData(R.drawable.search_media_filled, LocaleController.getString("SharedMediaTab2", R.string.SharedMediaTab2), new TLRPC$TL_inputMessagesFilterPhotoVideo(), 0), new MediaFilterData(R.drawable.search_links_filled, LocaleController.getString("SharedLinksTab2", R.string.SharedLinksTab2), new TLRPC$TL_inputMessagesFilterUrl(), 2), new MediaFilterData(R.drawable.search_files_filled, LocaleController.getString("SharedFilesTab2", R.string.SharedFilesTab2), new TLRPC$TL_inputMessagesFilterDocument(), 1), new MediaFilterData(R.drawable.search_music_filled, LocaleController.getString("SharedMusicTab2", R.string.SharedMusicTab2), new TLRPC$TL_inputMessagesFilterMusic(), 3), new MediaFilterData(R.drawable.search_voice_filled, LocaleController.getString("SharedVoiceTab2", R.string.SharedVoiceTab2), new TLRPC$TL_inputMessagesFilterRoundVoice(), 5)};
    private static final Pattern yearPatter = Pattern.compile("20[0-9]{1,2}");
    private static final Pattern monthYearOrDayPatter = Pattern.compile("(\\w{3,}) ([0-9]{0,4})");
    private static final Pattern yearOrDayAndMonthPatter = Pattern.compile("([0-9]{0,4}) (\\w{2,})");
    private static final Pattern shortDate = Pattern.compile("^([0-9]{1,4})(\\.| |/|\\-)([0-9]{1,4})$");
    private static final Pattern longDate = Pattern.compile("^([0-9]{1,2})(\\.| |/|\\-)([0-9]{1,2})(\\.| |/|\\-)([0-9]{1,4})$");
    private static final int[] numberOfDaysEachMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public FiltersView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        this.usersFilters = new ArrayList<>();
        this.oldItems = new ArrayList<>();
        this.diffUtilsCallback = new DiffUtil.Callback() { // from class: org.telegram.ui.Adapters.FiltersView.4
            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public boolean areContentsTheSame(int i, int i2) {
                return true;
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public int getOldListSize() {
                return FiltersView.this.oldItems.size();
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public int getNewListSize() {
                return FiltersView.this.usersFilters.size();
            }

            @Override // androidx.recyclerview.widget.DiffUtil.Callback
            public boolean areItemsTheSame(int i, int i2) {
                MediaFilterData mediaFilterData = (MediaFilterData) FiltersView.this.oldItems.get(i);
                MediaFilterData mediaFilterData2 = (MediaFilterData) FiltersView.this.usersFilters.get(i2);
                if (mediaFilterData.isSameType(mediaFilterData2)) {
                    int i3 = mediaFilterData.filterType;
                    if (i3 == 4) {
                        TLObject tLObject = mediaFilterData.chat;
                        if (tLObject instanceof TLRPC$User) {
                            TLObject tLObject2 = mediaFilterData2.chat;
                            if (tLObject2 instanceof TLRPC$User) {
                                return ((TLRPC$User) tLObject).id == ((TLRPC$User) tLObject2).id;
                            }
                        }
                        if (tLObject instanceof TLRPC$Chat) {
                            TLObject tLObject3 = mediaFilterData2.chat;
                            return (tLObject3 instanceof TLRPC$Chat) && ((TLRPC$Chat) tLObject).id == ((TLRPC$Chat) tLObject3).id;
                        }
                    } else if (i3 == 6) {
                        return mediaFilterData.title.equals(mediaFilterData2.title);
                    } else {
                        if (i3 == 7) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) { // from class: org.telegram.ui.Adapters.FiltersView.1
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(recycler, state, accessibilityNodeInfoCompat);
                if (!FiltersView.this.isEnabled()) {
                    accessibilityNodeInfoCompat.setVisibleToUser(false);
                }
            }
        };
        this.layoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(0);
        setLayoutManager(this.layoutManager);
        setAdapter(new Adapter());
        addItemDecoration(new RecyclerView.ItemDecoration(this) { // from class: org.telegram.ui.Adapters.FiltersView.2
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                super.getItemOffsets(rect, view, recyclerView, state);
                int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
                rect.left = AndroidUtilities.dp(8.0f);
                if (childAdapterPosition == state.getItemCount() - 1) {
                    rect.right = AndroidUtilities.dp(10.0f);
                }
                if (childAdapterPosition == 0) {
                    rect.left = AndroidUtilities.dp(10.0f);
                }
            }
        });
        setItemAnimator(new DefaultItemAnimator(this) { // from class: org.telegram.ui.Adapters.FiltersView.3
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected long getAddAnimationDelay(long j, long j2, long j3) {
                return 0L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getAddDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected long getMoveAnimationDelay() {
                return 0L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getMoveDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.SimpleItemAnimator
            public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
                boolean animateAdd = super.animateAdd(viewHolder);
                if (animateAdd) {
                    viewHolder.itemView.setScaleX(0.0f);
                    viewHolder.itemView.setScaleY(0.0f);
                }
                return animateAdd;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void animateAddImpl(final RecyclerView.ViewHolder viewHolder) {
                final View view = viewHolder.itemView;
                final ViewPropertyAnimator animate = view.animate();
                this.mAddAnimations.add(viewHolder);
                animate.alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(getAddDuration()).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Adapters.FiltersView.3.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        dispatchAddStarting(viewHolder);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        view.setAlpha(1.0f);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        animate.setListener(null);
                        dispatchAddFinished(viewHolder);
                        ((DefaultItemAnimator) AnonymousClass3.this).mAddAnimations.remove(viewHolder);
                        dispatchFinishedWhenDone();
                    }
                }).start();
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected void animateRemoveImpl(final RecyclerView.ViewHolder viewHolder) {
                final View view = viewHolder.itemView;
                final ViewPropertyAnimator animate = view.animate();
                this.mRemoveAnimations.add(viewHolder);
                animate.setDuration(getRemoveDuration()).alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Adapters.FiltersView.3.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animator) {
                        dispatchRemoveStarting(viewHolder);
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        animate.setListener(null);
                        view.setAlpha(1.0f);
                        view.setTranslationX(0.0f);
                        view.setTranslationY(0.0f);
                        view.setScaleX(1.0f);
                        view.setScaleY(1.0f);
                        dispatchRemoveFinished(viewHolder);
                        ((DefaultItemAnimator) AnonymousClass3.this).mRemoveAnimations.remove(viewHolder);
                        dispatchFinishedWhenDone();
                    }
                }).start();
            }
        });
        setWillNotDraw(false);
        setHideIfEmpty(false);
        setSelectorRadius(AndroidUtilities.dp(28.0f));
        setSelectorDrawableColor(getThemedColor("listSelectorSDK21"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
    }

    public MediaFilterData getFilterAt(int i) {
        if (this.usersFilters.isEmpty()) {
            return filters[i];
        }
        return this.usersFilters.get(i);
    }

    public void setUsersAndDates(ArrayList<Object> arrayList, ArrayList<DateData> arrayList2, boolean z) {
        String formatName;
        this.oldItems.clear();
        this.oldItems.addAll(this.usersFilters);
        this.usersFilters.clear();
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                Object obj = arrayList.get(i);
                if (obj instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) obj;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == tLRPC$User.id) {
                        formatName = LocaleController.getString("SavedMessages", R.string.SavedMessages);
                    } else {
                        formatName = ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name, 10);
                    }
                    MediaFilterData mediaFilterData = new MediaFilterData(R.drawable.search_users_filled, formatName, null, 4);
                    mediaFilterData.setUser(tLRPC$User);
                    this.usersFilters.add(mediaFilterData);
                } else if (obj instanceof TLRPC$Chat) {
                    TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) obj;
                    String str = tLRPC$Chat.title;
                    if (str.length() > 12) {
                        str = String.format("%s...", str.substring(0, 10));
                    }
                    MediaFilterData mediaFilterData2 = new MediaFilterData(R.drawable.search_users_filled, str, null, 4);
                    mediaFilterData2.setUser(tLRPC$Chat);
                    this.usersFilters.add(mediaFilterData2);
                }
            }
        }
        if (arrayList2 != null) {
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                DateData dateData = arrayList2.get(i2);
                MediaFilterData mediaFilterData3 = new MediaFilterData(R.drawable.search_date_filled, dateData.title, null, 6);
                mediaFilterData3.setDate(dateData);
                this.usersFilters.add(mediaFilterData3);
            }
        }
        if (z) {
            this.usersFilters.add(new MediaFilterData(R.drawable.chats_archive, LocaleController.getString("ArchiveSearchFilter", R.string.ArchiveSearchFilter), null, 7));
        }
        if (getAdapter() != null) {
            UpdateCallback updateCallback = new UpdateCallback(getAdapter());
            DiffUtil.calculateDiff(this.diffUtilsCallback).dispatchUpdatesTo(updateCallback);
            if (this.usersFilters.isEmpty() || !updateCallback.changed) {
                return;
            }
            this.layoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    public static void fillTipDates(String str, ArrayList<DateData> arrayList) {
        arrayList.clear();
        if (str == null) {
            return;
        }
        String trim = str.trim();
        if (trim.length() < 3) {
            return;
        }
        int i = R.string.SearchTipToday;
        if (LocaleController.getString("SearchTipToday", i).toLowerCase().startsWith(trim) || "today".startsWith(trim)) {
            Calendar calendar = Calendar.getInstance();
            int i2 = calendar.get(1);
            int i3 = calendar.get(2);
            int i4 = calendar.get(5);
            calendar.set(i2, i3, i4, 0, 0, 0);
            long timeInMillis = calendar.getTimeInMillis();
            calendar.set(i2, i3, i4 + 1, 0, 0, 0);
            arrayList.add(new DateData(LocaleController.getString("SearchTipToday", i), timeInMillis, calendar.getTimeInMillis() - 1));
            return;
        }
        int i5 = R.string.SearchTipYesterday;
        if (LocaleController.getString("SearchTipYesterday", i5).toLowerCase().startsWith(trim) || "yesterday".startsWith(trim)) {
            Calendar calendar2 = Calendar.getInstance();
            int i6 = calendar2.get(1);
            int i7 = calendar2.get(2);
            int i8 = calendar2.get(5);
            calendar2.set(i6, i7, i8, 0, 0, 0);
            calendar2.set(i6, i7, i8 + 1, 0, 0, 0);
            arrayList.add(new DateData(LocaleController.getString("SearchTipYesterday", i5), calendar2.getTimeInMillis() - 86400000, calendar2.getTimeInMillis() - 86400001));
            return;
        }
        int dayOfWeek = getDayOfWeek(trim);
        if (dayOfWeek >= 0) {
            Calendar calendar3 = Calendar.getInstance();
            long timeInMillis2 = calendar3.getTimeInMillis();
            calendar3.set(7, dayOfWeek);
            if (calendar3.getTimeInMillis() > timeInMillis2) {
                calendar3.setTimeInMillis(calendar3.getTimeInMillis() - NUM);
            }
            int i9 = calendar3.get(1);
            int i10 = calendar3.get(2);
            int i11 = calendar3.get(5);
            calendar3.set(i9, i10, i11, 0, 0, 0);
            long timeInMillis3 = calendar3.getTimeInMillis();
            calendar3.set(i9, i10, i11 + 1, 0, 0, 0);
            arrayList.add(new DateData(LocaleController.getInstance().formatterWeekLong.format(timeInMillis3), timeInMillis3, calendar3.getTimeInMillis() - 1));
            return;
        }
        Matcher matcher = shortDate.matcher(trim);
        if (matcher.matches()) {
            String group = matcher.group(1);
            String group2 = matcher.group(3);
            int parseInt = Integer.parseInt(group);
            int parseInt2 = Integer.parseInt(group2);
            if (parseInt <= 0 || parseInt > 31) {
                if (parseInt < 2013 || parseInt2 > 12) {
                    return;
                }
                createForMonthYear(arrayList, parseInt2 - 1, parseInt);
                return;
            } else if (parseInt2 >= 2013 && parseInt <= 12) {
                createForMonthYear(arrayList, parseInt - 1, parseInt2);
                return;
            } else if (parseInt2 > 12) {
                return;
            } else {
                createForDayMonth(arrayList, parseInt - 1, parseInt2 - 1);
                return;
            }
        }
        Matcher matcher2 = longDate.matcher(trim);
        if (matcher2.matches()) {
            String group3 = matcher2.group(1);
            String group4 = matcher2.group(3);
            String group5 = matcher2.group(5);
            if (!matcher2.group(2).equals(matcher2.group(4))) {
                return;
            }
            int parseInt3 = Integer.parseInt(group3);
            int parseInt4 = Integer.parseInt(group4) - 1;
            int parseInt5 = Integer.parseInt(group5);
            if (parseInt5 >= 10 && parseInt5 <= 99) {
                parseInt5 += 2000;
            }
            int i12 = Calendar.getInstance().get(1);
            if (!validDateForMont(parseInt3 - 1, parseInt4) || parseInt5 < 2013 || parseInt5 > i12) {
                return;
            }
            Calendar calendar4 = Calendar.getInstance();
            int i13 = parseInt5;
            calendar4.set(i13, parseInt4, parseInt3, 0, 0, 0);
            long timeInMillis4 = calendar4.getTimeInMillis();
            calendar4.set(i13, parseInt4, parseInt3 + 1, 0, 0, 0);
            arrayList.add(new DateData(LocaleController.getInstance().formatterYearMax.format(timeInMillis4), timeInMillis4, calendar4.getTimeInMillis() - 1));
        } else if (yearPatter.matcher(trim).matches()) {
            int intValue = Integer.valueOf(trim).intValue();
            int i14 = Calendar.getInstance().get(1);
            if (intValue < 2013) {
                while (i14 >= 2013) {
                    Calendar calendar5 = Calendar.getInstance();
                    calendar5.set(i14, 0, 1, 0, 0, 0);
                    long timeInMillis5 = calendar5.getTimeInMillis();
                    calendar5.set(i14 + 1, 0, 1, 0, 0, 0);
                    arrayList.add(new DateData(Integer.toString(i14), timeInMillis5, calendar5.getTimeInMillis() - 1));
                    i14--;
                }
            } else if (intValue <= i14) {
                Calendar calendar6 = Calendar.getInstance();
                calendar6.set(intValue, 0, 1, 0, 0, 0);
                long timeInMillis6 = calendar6.getTimeInMillis();
                calendar6.set(intValue + 1, 0, 1, 0, 0, 0);
                arrayList.add(new DateData(Integer.toString(intValue), timeInMillis6, calendar6.getTimeInMillis() - 1));
            }
        } else {
            Matcher matcher3 = monthYearOrDayPatter.matcher(trim);
            if (matcher3.matches()) {
                String group6 = matcher3.group(1);
                String group7 = matcher3.group(2);
                int month = getMonth(group6);
                if (month >= 0) {
                    int intValue2 = Integer.valueOf(group7).intValue();
                    if (intValue2 > 0 && intValue2 <= 31) {
                        createForDayMonth(arrayList, intValue2 - 1, month);
                        return;
                    } else if (intValue2 >= 2013) {
                        createForMonthYear(arrayList, month, intValue2);
                        return;
                    }
                }
            }
            Matcher matcher4 = yearOrDayAndMonthPatter.matcher(trim);
            if (matcher4.matches()) {
                String group8 = matcher4.group(1);
                int month2 = getMonth(matcher4.group(2));
                if (month2 >= 0) {
                    int intValue3 = Integer.valueOf(group8).intValue();
                    if (intValue3 > 0 && intValue3 <= 31) {
                        createForDayMonth(arrayList, intValue3 - 1, month2);
                        return;
                    } else if (intValue3 >= 2013) {
                        createForMonthYear(arrayList, month2, intValue3);
                    }
                }
            }
            if (TextUtils.isEmpty(trim) || trim.length() <= 2) {
                return;
            }
            int month3 = getMonth(trim);
            long timeInMillis7 = Calendar.getInstance().getTimeInMillis();
            if (month3 < 0) {
                return;
            }
            for (int i15 = Calendar.getInstance().get(1); i15 >= 2013; i15--) {
                Calendar calendar7 = Calendar.getInstance();
                calendar7.set(i15, month3, 1, 0, 0, 0);
                long timeInMillis8 = calendar7.getTimeInMillis();
                if (timeInMillis8 <= timeInMillis7) {
                    calendar7.add(2, 1);
                    arrayList.add(new DateData(LocaleController.getInstance().formatterMonthYear.format(timeInMillis8), timeInMillis8, calendar7.getTimeInMillis() - 1));
                }
            }
        }
    }

    private static void createForMonthYear(ArrayList<DateData> arrayList, int i, int i2) {
        int i3 = Calendar.getInstance().get(1);
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        if (i2 < 2013 || i2 > i3) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(i2, i, 1, 0, 0, 0);
        long timeInMillis2 = calendar.getTimeInMillis();
        if (timeInMillis2 > timeInMillis) {
            return;
        }
        calendar.add(2, 1);
        arrayList.add(new DateData(LocaleController.getInstance().formatterMonthYear.format(timeInMillis2), timeInMillis2, calendar.getTimeInMillis() - 1));
    }

    private static void createForDayMonth(ArrayList<DateData> arrayList, int i, int i2) {
        long j;
        if (validDateForMont(i, i2)) {
            int i3 = 1;
            int i4 = Calendar.getInstance().get(1);
            long timeInMillis = Calendar.getInstance().getTimeInMillis();
            GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
            int i5 = i4;
            while (i5 >= 2013) {
                if (i2 != i3 || i != 28 || gregorianCalendar.isLeapYear(i5)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(i5, i2, i + 1, 0, 0, 0);
                    long timeInMillis2 = calendar.getTimeInMillis();
                    if (timeInMillis2 <= timeInMillis) {
                        j = timeInMillis;
                        calendar.set(i5, i2, i + 2, 0, 0, 0);
                        long timeInMillis3 = calendar.getTimeInMillis() - 1;
                        if (i5 == i4) {
                            arrayList.add(new DateData(LocaleController.getInstance().formatterDayMonth.format(timeInMillis2), timeInMillis2, timeInMillis3));
                        } else {
                            arrayList.add(new DateData(LocaleController.getInstance().formatterYearMax.format(timeInMillis2), timeInMillis2, timeInMillis3));
                        }
                        i5--;
                        timeInMillis = j;
                        i3 = 1;
                    }
                }
                j = timeInMillis;
                i5--;
                timeInMillis = j;
                i3 = 1;
            }
        }
    }

    private static boolean validDateForMont(int i, int i2) {
        return i2 >= 0 && i2 < 12 && i >= 0 && i < numberOfDaysEachMonth[i2];
    }

    public static int getDayOfWeek(String str) {
        Calendar calendar = Calendar.getInstance();
        if (str.length() <= 3) {
            return -1;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        int i = 0;
        while (i < 7) {
            calendar.set(7, i);
            if (LocaleController.getInstance().formatterWeekLong.format(calendar.getTime()).toLowerCase().startsWith(str) || simpleDateFormat.format(calendar.getTime()).toLowerCase().startsWith(str)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static int getMonth(String str) {
        String[] strArr = {LocaleController.getString("January", R.string.January).toLowerCase(), LocaleController.getString("February", R.string.February).toLowerCase(), LocaleController.getString("March", R.string.March).toLowerCase(), LocaleController.getString("April", R.string.April).toLowerCase(), LocaleController.getString("May", R.string.May).toLowerCase(), LocaleController.getString("June", R.string.June).toLowerCase(), LocaleController.getString("July", R.string.July).toLowerCase(), LocaleController.getString("August", R.string.August).toLowerCase(), LocaleController.getString("September", R.string.September).toLowerCase(), LocaleController.getString("October", R.string.October).toLowerCase(), LocaleController.getString("November", R.string.November).toLowerCase(), LocaleController.getString("December", R.string.December).toLowerCase()};
        String[] strArr2 = new String[12];
        Calendar calendar = Calendar.getInstance();
        for (int i = 1; i <= 12; i++) {
            calendar.set(0, 0, 0, 0, 0, 0);
            calendar.set(2, i);
            strArr2[i - 1] = calendar.getDisplayName(2, 2, Locale.ENGLISH).toLowerCase();
        }
        for (int i2 = 0; i2 < 12; i2++) {
            if (strArr2[i2].startsWith(str) || strArr[i2].startsWith(str)) {
                return i2;
            }
        }
        return -1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0.0f, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight(), Theme.dividerPaint);
    }

    public void updateColors() {
        getRecycledViewPool().clear();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof FilterView) {
                ((FilterView) childAt).updateColors();
            }
        }
        for (int i2 = 0; i2 < getCachedChildCount(); i2++) {
            View cachedChildAt = getCachedChildAt(i2);
            if (cachedChildAt instanceof FilterView) {
                ((FilterView) cachedChildAt).updateColors();
            }
        }
        for (int i3 = 0; i3 < getAttachedScrapChildCount(); i3++) {
            View attachedScrapChildAt = getAttachedScrapChildAt(i3);
            if (attachedScrapChildAt instanceof FilterView) {
                ((FilterView) attachedScrapChildAt).updateColors();
            }
        }
        setSelectorDrawableColor(getThemedColor("listSelectorSDK21"));
    }

    /* loaded from: classes3.dex */
    private class Adapter extends RecyclerListView.SelectionAdapter {
        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        private Adapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder  reason: collision with other method in class */
        public ViewHolder mo1805onCreateViewHolder(ViewGroup viewGroup, int i) {
            ViewHolder viewHolder = new ViewHolder(FiltersView.this, new FilterView(viewGroup.getContext(), ((RecyclerListView) FiltersView.this).resourcesProvider));
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-2, AndroidUtilities.dp(32.0f));
            ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = AndroidUtilities.dp(6.0f);
            viewHolder.itemView.setLayoutParams(layoutParams);
            return viewHolder;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ((ViewHolder) viewHolder).filterView.setData((MediaFilterData) FiltersView.this.usersFilters.get(i));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return FiltersView.this.usersFilters.size();
        }
    }

    /* loaded from: classes3.dex */
    public static class FilterView extends FrameLayout {
        BackupImageView avatarImageView;
        MediaFilterData data;
        private final Theme.ResourcesProvider resourcesProvider;
        CombinedDrawable thumbDrawable;
        TextView titleView;

        public FilterView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(32, 32.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 14.0f);
            addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 16, 38.0f, 0.0f, 16.0f, 0.0f));
            updateColors();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateColors() {
            setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), getThemedColor("groupcreate_spanBackground")));
            this.titleView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
            CombinedDrawable combinedDrawable = this.thumbDrawable;
            if (combinedDrawable != null) {
                if (this.data.filterType == 7) {
                    Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor("avatar_backgroundArchived"), false);
                    Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
                    return;
                }
                Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor("avatar_backgroundBlue"), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
            }
        }

        public void setData(MediaFilterData mediaFilterData) {
            this.data = mediaFilterData;
            this.avatarImageView.getImageReceiver().clearImage();
            if (mediaFilterData.filterType == 7) {
                CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_archive);
                this.thumbDrawable = createCircleDrawableWithIcon;
                createCircleDrawableWithIcon.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_backgroundArchived"), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
                this.titleView.setText(mediaFilterData.title);
                return;
            }
            CombinedDrawable createCircleDrawableWithIcon2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), mediaFilterData.iconResFilled);
            this.thumbDrawable = createCircleDrawableWithIcon2;
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon2, getThemedColor("avatar_backgroundBlue"), false);
            Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
            if (mediaFilterData.filterType == 4) {
                TLObject tLObject = mediaFilterData.chat;
                if (tLObject instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == tLRPC$User.id) {
                        CombinedDrawable createCircleDrawableWithIcon3 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_saved);
                        createCircleDrawableWithIcon3.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                        Theme.setCombinedDrawableColor(createCircleDrawableWithIcon3, getThemedColor("avatar_backgroundSaved"), false);
                        Theme.setCombinedDrawableColor(createCircleDrawableWithIcon3, getThemedColor("avatar_actionBarIconBlue"), true);
                        this.avatarImageView.setImageDrawable(createCircleDrawableWithIcon3);
                    } else {
                        this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                        this.avatarImageView.getImageReceiver().setForUserOrChat(tLRPC$User, this.thumbDrawable);
                    }
                } else if (tLObject instanceof TLRPC$Chat) {
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setForUserOrChat((TLRPC$Chat) tLObject, this.thumbDrawable);
                }
            } else {
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
            }
            this.titleView.setText(mediaFilterData.title);
        }

        private int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ViewHolder extends RecyclerView.ViewHolder {
        FilterView filterView;

        public ViewHolder(FiltersView filtersView, FilterView filterView) {
            super(filterView);
            this.filterView = filterView;
        }
    }

    /* loaded from: classes3.dex */
    public static class MediaFilterData {
        public TLObject chat;
        public DateData dateData;
        public final TLRPC$MessagesFilter filter;
        public final int filterType;
        public final int iconResFilled;
        public boolean removable = true;
        public final String title;

        public MediaFilterData(int i, String str, TLRPC$MessagesFilter tLRPC$MessagesFilter, int i2) {
            this.iconResFilled = i;
            this.title = str;
            this.filter = tLRPC$MessagesFilter;
            this.filterType = i2;
        }

        public void setUser(TLObject tLObject) {
            this.chat = tLObject;
        }

        public boolean isSameType(MediaFilterData mediaFilterData) {
            if (this.filterType == mediaFilterData.filterType) {
                return true;
            }
            return isMedia() && mediaFilterData.isMedia();
        }

        public boolean isMedia() {
            int i = this.filterType;
            return i == 0 || i == 1 || i == 2 || i == 3 || i == 5;
        }

        public void setDate(DateData dateData) {
            this.dateData = dateData;
        }
    }

    /* loaded from: classes3.dex */
    public static class DateData {
        public final long maxDate;
        public final long minDate;
        public final String title;

        private DateData(String str, long j, long j2) {
            this.title = str;
            this.minDate = j;
            this.maxDate = j2;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this, 0, null, null, null, null, "graySection"));
        arrayList.add(new ThemeDescription(this, 0, null, null, null, null, "key_graySectionText"));
        return arrayList;
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }

    /* loaded from: classes3.dex */
    private static class UpdateCallback implements ListUpdateCallback {
        final RecyclerView.Adapter adapter;
        boolean changed;

        private UpdateCallback(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onInserted(int i, int i2) {
            this.changed = true;
            this.adapter.notifyItemRangeInserted(i, i2);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onRemoved(int i, int i2) {
            this.changed = true;
            this.adapter.notifyItemRangeRemoved(i, i2);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onMoved(int i, int i2) {
            this.changed = true;
            this.adapter.notifyItemMoved(i, i2);
        }

        @Override // androidx.recyclerview.widget.ListUpdateCallback
        public void onChanged(int i, int i2, Object obj) {
            this.adapter.notifyItemRangeChanged(i, i2, obj);
        }
    }
}
