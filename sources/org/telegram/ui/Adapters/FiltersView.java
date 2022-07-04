package org.telegram.ui.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
import j$.time.LocalDate;
import j$.time.LocalDateTime;
import j$.time.LocalTime;
import j$.time.format.DateTimeFormatter;
import j$.time.format.DateTimeParseException;
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
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class FiltersView extends RecyclerListView {
    public static final int FILTER_INDEX_FILES = 2;
    public static final int FILTER_INDEX_LINKS = 1;
    public static final int FILTER_INDEX_MEDIA = 0;
    public static final int FILTER_INDEX_MUSIC = 3;
    public static final int FILTER_INDEX_VOICE = 4;
    public static final int FILTER_TYPE_ARCHIVE = 7;
    public static final int FILTER_TYPE_CHAT = 4;
    public static final int FILTER_TYPE_DATE = 6;
    public static final int FILTER_TYPE_FILES = 1;
    public static final int FILTER_TYPE_LINKS = 2;
    public static final int FILTER_TYPE_MEDIA = 0;
    public static final int FILTER_TYPE_MUSIC = 3;
    public static final int FILTER_TYPE_VOICE = 5;
    public static final MediaFilterData[] filters = {new MediaFilterData(NUM, LocaleController.getString("SharedMediaTab2", NUM), new TLRPC.TL_inputMessagesFilterPhotoVideo(), 0), new MediaFilterData(NUM, LocaleController.getString("SharedLinksTab2", NUM), new TLRPC.TL_inputMessagesFilterUrl(), 2), new MediaFilterData(NUM, LocaleController.getString("SharedFilesTab2", NUM), new TLRPC.TL_inputMessagesFilterDocument(), 1), new MediaFilterData(NUM, LocaleController.getString("SharedMusicTab2", NUM), new TLRPC.TL_inputMessagesFilterMusic(), 3), new MediaFilterData(NUM, LocaleController.getString("SharedVoiceTab2", NUM), new TLRPC.TL_inputMessagesFilterRoundVoice(), 5)};
    private static final Pattern longDate = Pattern.compile("^([0-9]{1,2})(\\.| |/|\\-)([0-9]{1,2})(\\.| |/|\\-)([0-9]{1,4})$");
    private static final int minYear = 2013;
    private static final Pattern monthYearOrDayPatter = Pattern.compile("(\\w{3,}) ([0-9]{0,4})");
    private static final int[] numberOfDaysEachMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final Pattern shortDate = Pattern.compile("^([0-9]{1,4})(\\.| |/|\\-)([0-9]{1,4})$");
    private static final Pattern yearOrDayAndMonthPatter = Pattern.compile("([0-9]{0,4}) (\\w{2,})");
    private static final Pattern yearPatter = Pattern.compile("20[0-9]{1,2}");
    DiffUtil.Callback diffUtilsCallback = new DiffUtil.Callback() {
        public int getOldListSize() {
            return FiltersView.this.oldItems.size();
        }

        public int getNewListSize() {
            return FiltersView.this.usersFilters.size();
        }

        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            MediaFilterData oldItem = (MediaFilterData) FiltersView.this.oldItems.get(oldItemPosition);
            MediaFilterData newItem = (MediaFilterData) FiltersView.this.usersFilters.get(newItemPosition);
            if (oldItem.isSameType(newItem)) {
                if (oldItem.filterType == 4) {
                    if (!(oldItem.chat instanceof TLRPC.User) || !(newItem.chat instanceof TLRPC.User)) {
                        if (!(oldItem.chat instanceof TLRPC.Chat) || !(newItem.chat instanceof TLRPC.Chat) || ((TLRPC.Chat) oldItem.chat).id != ((TLRPC.Chat) newItem.chat).id) {
                            return false;
                        }
                        return true;
                    } else if (((TLRPC.User) oldItem.chat).id == ((TLRPC.User) newItem.chat).id) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (oldItem.filterType == 6) {
                    return oldItem.title.equals(newItem.title);
                } else {
                    if (oldItem.filterType == 7) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
        }
    };
    LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ArrayList<MediaFilterData> oldItems = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<MediaFilterData> usersFilters = new ArrayList<>();

    public FiltersView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        AnonymousClass1 r0 = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(recycler, state, info);
                if (!FiltersView.this.isEnabled()) {
                    info.setVisibleToUser(false);
                }
            }
        };
        this.layoutManager = r0;
        r0.setOrientation(0);
        setLayoutManager(this.layoutManager);
        setAdapter(new Adapter());
        addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                outRect.left = AndroidUtilities.dp(8.0f);
                if (position == state.getItemCount() - 1) {
                    outRect.right = AndroidUtilities.dp(10.0f);
                }
                if (position == 0) {
                    outRect.left = AndroidUtilities.dp(10.0f);
                }
            }
        });
        setItemAnimator(new DefaultItemAnimator() {
            private final float scaleFrom = 0.0f;

            /* access modifiers changed from: protected */
            public long getMoveAnimationDelay() {
                return 0;
            }

            /* access modifiers changed from: protected */
            public long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
                return 0;
            }

            public long getMoveDuration() {
                return 220;
            }

            public long getAddDuration() {
                return 220;
            }

            public boolean animateAdd(RecyclerView.ViewHolder holder) {
                boolean r = super.animateAdd(holder);
                if (r) {
                    holder.itemView.setScaleX(0.0f);
                    holder.itemView.setScaleY(0.0f);
                }
                return r;
            }

            public void animateAddImpl(final RecyclerView.ViewHolder holder) {
                final View view = holder.itemView;
                final ViewPropertyAnimator animation = view.animate();
                this.mAddAnimations.add(holder);
                animation.alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(getAddDuration()).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        AnonymousClass3.this.dispatchAddStarting(holder);
                    }

                    public void onAnimationCancel(Animator animator) {
                        view.setAlpha(1.0f);
                    }

                    public void onAnimationEnd(Animator animator) {
                        animation.setListener((Animator.AnimatorListener) null);
                        AnonymousClass3.this.dispatchAddFinished(holder);
                        AnonymousClass3.this.mAddAnimations.remove(holder);
                        AnonymousClass3.this.dispatchFinishedWhenDone();
                    }
                }).start();
            }

            /* access modifiers changed from: protected */
            public void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
                final View view = holder.itemView;
                final ViewPropertyAnimator animation = view.animate();
                this.mRemoveAnimations.add(holder);
                animation.setDuration(getRemoveDuration()).alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        AnonymousClass3.this.dispatchRemoveStarting(holder);
                    }

                    public void onAnimationEnd(Animator animator) {
                        animation.setListener((Animator.AnimatorListener) null);
                        view.setAlpha(1.0f);
                        view.setTranslationX(0.0f);
                        view.setTranslationY(0.0f);
                        view.setScaleX(1.0f);
                        view.setScaleY(1.0f);
                        AnonymousClass3.this.dispatchRemoveFinished(holder);
                        AnonymousClass3.this.mRemoveAnimations.remove(holder);
                        AnonymousClass3.this.dispatchFinishedWhenDone();
                    }
                }).start();
            }
        });
        setWillNotDraw(false);
        setHideIfEmpty(false);
        setSelectorRadius(AndroidUtilities.dp(28.0f));
        setSelectorDrawableColor(getThemedColor("listSelectorSDK21"));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
    }

    public MediaFilterData getFilterAt(int i) {
        if (this.usersFilters.isEmpty()) {
            return filters[i];
        }
        return this.usersFilters.get(i);
    }

    public void setUsersAndDates(ArrayList<Object> localUsers, ArrayList<DateData> dates, boolean archive) {
        String title;
        ArrayList<Object> arrayList = localUsers;
        ArrayList<DateData> arrayList2 = dates;
        this.oldItems.clear();
        this.oldItems.addAll(this.usersFilters);
        this.usersFilters.clear();
        if (arrayList != null) {
            for (int i = 0; i < localUsers.size(); i++) {
                Object object = arrayList.get(i);
                if (object instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) object;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == user.id) {
                        title = LocaleController.getString("SavedMessages", NUM);
                    } else {
                        title = ContactsController.formatName(user.first_name, user.last_name, 10);
                    }
                    MediaFilterData data = new MediaFilterData(NUM, title, (TLRPC.MessagesFilter) null, 4);
                    data.setUser(user);
                    this.usersFilters.add(data);
                } else if (object instanceof TLRPC.Chat) {
                    TLRPC.Chat chat = (TLRPC.Chat) object;
                    String title2 = chat.title;
                    if (chat.title.length() > 12) {
                        title2 = String.format("%s...", new Object[]{title2.substring(0, 10)});
                    }
                    MediaFilterData data2 = new MediaFilterData(NUM, title2, (TLRPC.MessagesFilter) null, 4);
                    data2.setUser(chat);
                    this.usersFilters.add(data2);
                }
            }
        }
        if (arrayList2 != null) {
            for (int i2 = 0; i2 < dates.size(); i2++) {
                DateData dateData = arrayList2.get(i2);
                MediaFilterData data3 = new MediaFilterData(NUM, dateData.title, (TLRPC.MessagesFilter) null, 6);
                data3.setDate(dateData);
                this.usersFilters.add(data3);
            }
        }
        if (archive) {
            this.usersFilters.add(new MediaFilterData(NUM, LocaleController.getString("ArchiveSearchFilter", NUM), (TLRPC.MessagesFilter) null, 7));
        }
        if (getAdapter() != null) {
            UpdateCallback updateCallback = new UpdateCallback(getAdapter());
            DiffUtil.calculateDiff(this.diffUtilsCallback).dispatchUpdatesTo((ListUpdateCallback) updateCallback);
            if (!this.usersFilters.isEmpty() && updateCallback.changed) {
                this.layoutManager.scrollToPositionWithOffset(0, 0);
            }
        }
    }

    public static void fillTipDates(String query, ArrayList<DateData> dates) {
        int year;
        ArrayList<DateData> arrayList = dates;
        dates.clear();
        if (query != null) {
            String q = query.trim();
            if (q.length() >= 3) {
                int i = 2;
                if (LocaleController.getString("SearchTipToday", NUM).toLowerCase().startsWith(q) || "today".startsWith(q)) {
                    Calendar calendar = Calendar.getInstance();
                    int year2 = calendar.get(1);
                    int month = calendar.get(2);
                    int day = calendar.get(5);
                    Calendar calendar2 = calendar;
                    int i2 = year2;
                    int i3 = month;
                    calendar2.set(i2, i3, day, 0, 0, 0);
                    long minDate = calendar.getTimeInMillis();
                    calendar2.set(i2, i3, day + 1, 0, 0, 0);
                    DateData dateData = r8;
                    DateData dateData2 = new DateData(LocaleController.getString("SearchTipToday", NUM), minDate, calendar.getTimeInMillis() - 1);
                    arrayList.add(dateData);
                } else if (LocaleController.getString("SearchTipYesterday", NUM).toLowerCase().startsWith(q) || "yesterday".startsWith(q)) {
                    Calendar calendar3 = Calendar.getInstance();
                    int year3 = calendar3.get(1);
                    int month2 = calendar3.get(2);
                    int day2 = calendar3.get(5);
                    Calendar calendar4 = calendar3;
                    int i4 = year3;
                    int i5 = month2;
                    calendar4.set(i4, i5, day2, 0, 0, 0);
                    calendar4.set(i4, i5, day2 + 1, 0, 0, 0);
                    DateData dateData3 = r12;
                    DateData dateData4 = new DateData(LocaleController.getString("SearchTipYesterday", NUM), calendar3.getTimeInMillis() - 86400000, calendar3.getTimeInMillis() - 86400001);
                    arrayList.add(dateData3);
                } else {
                    int dayOfWeek = getDayOfWeek(q);
                    if (dayOfWeek >= 0) {
                        Calendar calendar5 = Calendar.getInstance();
                        long now = calendar5.getTimeInMillis();
                        calendar5.set(7, dayOfWeek);
                        if (calendar5.getTimeInMillis() > now) {
                            calendar5.setTimeInMillis(calendar5.getTimeInMillis() - NUM);
                        }
                        int year4 = calendar5.get(1);
                        int month3 = calendar5.get(2);
                        int day3 = calendar5.get(5);
                        Calendar calendar6 = calendar5;
                        int i6 = year4;
                        int i7 = month3;
                        calendar6.set(i6, i7, day3, 0, 0, 0);
                        long minDate2 = calendar5.getTimeInMillis();
                        calendar6.set(i6, i7, day3 + 1, 0, 0, 0);
                        long minDate3 = minDate2;
                        arrayList.add(new DateData(LocaleController.getInstance().formatterWeekLong.format(minDate3), minDate3, calendar5.getTimeInMillis() - 1));
                        return;
                    }
                    Matcher matcher = shortDate.matcher(q);
                    Matcher matcher2 = matcher;
                    if (matcher.matches()) {
                        String g1 = matcher2.group(1);
                        String g2 = matcher2.group(3);
                        int k = Integer.parseInt(g1);
                        int k1 = Integer.parseInt(g2);
                        if (k <= 0 || k > 31) {
                            if (k >= 2013 && k1 <= 12) {
                                createForMonthYear(arrayList, k1 - 1, k);
                            }
                        } else if (k1 >= 2013 && k <= 12) {
                            createForMonthYear(arrayList, k - 1, k1);
                        } else if (k1 <= 12) {
                            createForDayMonth(arrayList, k - 1, k1 - 1);
                        }
                    } else {
                        Matcher matcher3 = longDate.matcher(q);
                        Matcher matcher4 = matcher3;
                        if (matcher3.matches()) {
                            String g12 = matcher4.group(1);
                            String g22 = matcher4.group(3);
                            String g3 = matcher4.group(5);
                            if (matcher4.group(2).equals(matcher4.group(4))) {
                                int day4 = Integer.parseInt(g12);
                                int month4 = Integer.parseInt(g22) - 1;
                                int year5 = Integer.parseInt(g3);
                                if (year5 < 10 || year5 > 99) {
                                    year = year5;
                                } else {
                                    year = year5 + 2000;
                                }
                                int currentYear = Calendar.getInstance().get(1);
                                if (!validDateForMont(day4 - 1, month4) || year < 2013 || year > currentYear) {
                                    int i8 = year;
                                    int year6 = dayOfWeek;
                                    return;
                                }
                                Calendar calendar7 = Calendar.getInstance();
                                Calendar calendar8 = calendar7;
                                calendar8.set(year, month4, day4, 0, 0, 0);
                                long minDate4 = calendar7.getTimeInMillis();
                                calendar8.set(year, month4, day4 + 1, 0, 0, 0);
                                int i9 = dayOfWeek;
                                String str = g22;
                                long minDate5 = minDate4;
                                arrayList.add(new DateData(LocaleController.getInstance().formatterYearMax.format(minDate5), minDate5, calendar7.getTimeInMillis() - 1));
                                return;
                            }
                            return;
                        }
                        Matcher matcher5 = yearPatter.matcher(q);
                        Matcher matcher6 = matcher5;
                        if (matcher5.matches()) {
                            int selectedYear = Integer.valueOf(q).intValue();
                            int currentYear2 = Calendar.getInstance().get(1);
                            if (selectedYear < 2013) {
                                for (int i10 = currentYear2; i10 >= 2013; i10--) {
                                    Calendar calendar9 = Calendar.getInstance();
                                    Calendar calendar10 = calendar9;
                                    calendar10.set(i10, 0, 1, 0, 0, 0);
                                    long minDate6 = calendar9.getTimeInMillis();
                                    calendar10.set(i10 + 1, 0, 1, 0, 0, 0);
                                    arrayList.add(new DateData(Integer.toString(i10), minDate6, calendar9.getTimeInMillis() - 1));
                                }
                            } else if (selectedYear <= currentYear2) {
                                Calendar calendar11 = Calendar.getInstance();
                                Calendar calendar12 = calendar11;
                                calendar12.set(selectedYear, 0, 1, 0, 0, 0);
                                long minDate7 = calendar11.getTimeInMillis();
                                calendar12.set(selectedYear + 1, 0, 1, 0, 0, 0);
                                arrayList.add(new DateData(Integer.toString(selectedYear), minDate7, calendar11.getTimeInMillis() - 1));
                            }
                        } else {
                            Matcher matcher7 = monthYearOrDayPatter.matcher(q);
                            Matcher matcher8 = matcher7;
                            if (matcher7.matches()) {
                                String g13 = matcher8.group(1);
                                String g23 = matcher8.group(2);
                                int month5 = getMonth(g13);
                                if (month5 >= 0) {
                                    int k2 = Integer.valueOf(g23).intValue();
                                    if (k2 > 0 && k2 <= 31) {
                                        createForDayMonth(arrayList, k2 - 1, month5);
                                        return;
                                    } else if (k2 >= 2013) {
                                        createForMonthYear(arrayList, month5, k2);
                                        return;
                                    }
                                }
                            }
                            Matcher matcher9 = yearOrDayAndMonthPatter.matcher(q);
                            Matcher matcher10 = matcher9;
                            if (matcher9.matches()) {
                                String g14 = matcher10.group(1);
                                int month6 = getMonth(matcher10.group(2));
                                if (month6 >= 0) {
                                    int k3 = Integer.valueOf(g14).intValue();
                                    if (k3 > 0 && k3 <= 31) {
                                        createForDayMonth(arrayList, k3 - 1, month6);
                                        return;
                                    } else if (k3 >= 2013) {
                                        createForMonthYear(arrayList, month6, k3);
                                    }
                                }
                            }
                            if (!TextUtils.isEmpty(q) && q.length() > 2) {
                                int month7 = getMonth(q);
                                long today = Calendar.getInstance().getTimeInMillis();
                                if (month7 >= 0) {
                                    int j = Calendar.getInstance().get(1);
                                    while (j >= 2013) {
                                        Calendar calendar13 = Calendar.getInstance();
                                        calendar13.set(j, month7, 1, 0, 0, 0);
                                        long minDate8 = calendar13.getTimeInMillis();
                                        if (minDate8 <= today) {
                                            calendar13.add(i, 1);
                                            arrayList.add(new DateData(LocaleController.getInstance().formatterMonthYear.format(minDate8), minDate8, calendar13.getTimeInMillis() - 1));
                                        }
                                        j--;
                                        i = 2;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createForMonthYear(ArrayList<DateData> dates, int month, int selectedYear) {
        int i = selectedYear;
        int currentYear = Calendar.getInstance().get(1);
        long today = Calendar.getInstance().getTimeInMillis();
        if (i < 2013 || i > currentYear) {
            ArrayList<DateData> arrayList = dates;
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, month, 1, 0, 0, 0);
        long minDate = calendar.getTimeInMillis();
        if (minDate <= today) {
            calendar.add(2, 1);
            dates.add(new DateData(LocaleController.getInstance().formatterMonthYear.format(minDate), minDate, calendar.getTimeInMillis() - 1));
        }
    }

    private static void createForDayMonth(ArrayList<DateData> dates, int day, int month) {
        long today;
        ArrayList<DateData> arrayList = dates;
        int i = day;
        if (validDateForMont(day, month)) {
            int i2 = 1;
            int currentYear = Calendar.getInstance().get(1);
            long today2 = Calendar.getInstance().getTimeInMillis();
            GregorianCalendar georgianCal = (GregorianCalendar) GregorianCalendar.getInstance();
            int i3 = currentYear;
            while (i3 >= 2013) {
                if (month == i2 && i == 28 && !georgianCal.isLeapYear(i3)) {
                    today = today2;
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(i3, month, i + 1, 0, 0, 0);
                    long minDate = calendar.getTimeInMillis();
                    if (minDate > today2) {
                        today = today2;
                    } else {
                        today = today2;
                        long minDate2 = minDate;
                        calendar.set(i3, month, i + 2, 0, 0, 0);
                        long maxDate = calendar.getTimeInMillis() - 1;
                        if (i3 == currentYear) {
                            arrayList.add(new DateData(LocaleController.getInstance().formatterDayMonth.format(minDate2), minDate2, maxDate));
                        } else {
                            arrayList.add(new DateData(LocaleController.getInstance().formatterYearMax.format(minDate2), minDate2, maxDate));
                        }
                    }
                }
                i3--;
                today2 = today;
                i2 = 1;
            }
            int i4 = month;
            long j = today2;
            return;
        }
        int i5 = month;
    }

    private static boolean validDateForMont(int day, int month) {
        if (month < 0 || month >= 12 || day < 0 || day >= numberOfDaysEachMonth[month]) {
            return false;
        }
        return true;
    }

    public static int getDayOfWeek(String q) {
        Calendar c = Calendar.getInstance();
        if (q.length() <= 3) {
            return -1;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        int i = 0;
        while (i < 7) {
            c.set(7, i);
            if (LocaleController.getInstance().formatterWeekLong.format(c.getTime()).toLowerCase().startsWith(q) || dateFormat.format(c.getTime()).toLowerCase().startsWith(q)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static int getMonth(String q) {
        String[] months = {LocaleController.getString("January", NUM).toLowerCase(), LocaleController.getString("February", NUM).toLowerCase(), LocaleController.getString("March", NUM).toLowerCase(), LocaleController.getString("April", NUM).toLowerCase(), LocaleController.getString("May", NUM).toLowerCase(), LocaleController.getString("June", NUM).toLowerCase(), LocaleController.getString("July", NUM).toLowerCase(), LocaleController.getString("August", NUM).toLowerCase(), LocaleController.getString("September", NUM).toLowerCase(), LocaleController.getString("October", NUM).toLowerCase(), LocaleController.getString("November", NUM).toLowerCase(), LocaleController.getString("December", NUM).toLowerCase()};
        String[] monthsEng = new String[12];
        Calendar c = Calendar.getInstance();
        for (int i = 1; i <= 12; i++) {
            c.set(0, 0, 0, 0, 0, 0);
            c.set(2, i);
            monthsEng[i - 1] = c.getDisplayName(2, 2, Locale.ENGLISH).toLowerCase();
        }
        for (int i2 = 0; i2 < 12; i2++) {
            if (monthsEng[i2].startsWith(q) || months[i2].startsWith(q)) {
                return i2;
            }
        }
        return -1;
    }

    public static boolean isValidFormat(String format, String value, Locale locale) {
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);
        try {
            return LocalDateTime.parse(value, fomatter).format(fomatter).equals(value);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(value, fomatter).format(fomatter).equals(value);
            } catch (DateTimeParseException e2) {
                try {
                    return LocalTime.parse(value, fomatter).format(fomatter).equals(value);
                } catch (DateTimeParseException e3) {
                    return false;
                }
            }
        }
    }

    public void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawRect(0.0f, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dividerPaint);
    }

    public void updateColors() {
        getRecycledViewPool().clear();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof FilterView) {
                ((FilterView) view).updateColors();
            }
        }
        for (int i2 = 0; i2 < getCachedChildCount(); i2++) {
            View view2 = getCachedChildAt(i2);
            if (view2 instanceof FilterView) {
                ((FilterView) view2).updateColors();
            }
        }
        for (int i3 = 0; i3 < getAttachedScrapChildCount(); i3++) {
            View view3 = getAttachedScrapChildAt(i3);
            if (view3 instanceof FilterView) {
                ((FilterView) view3).updateColors();
            }
        }
        setSelectorDrawableColor(getThemedColor("listSelectorSDK21"));
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(new FilterView(parent.getContext(), FiltersView.this.resourcesProvider));
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-2, AndroidUtilities.dp(32.0f));
            lp.topMargin = AndroidUtilities.dp(6.0f);
            holder.itemView.setLayoutParams(lp);
            return holder;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).filterView.setData((MediaFilterData) FiltersView.this.usersFilters.get(position));
        }

        public int getItemCount() {
            return FiltersView.this.usersFilters.size();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }
    }

    public static class FilterView extends FrameLayout {
        BackupImageView avatarImageView;
        MediaFilterData data;
        private final Theme.ResourcesProvider resourcesProvider;
        CombinedDrawable thumbDrawable;
        TextView titleView;

        public FilterView(Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(32, 32.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 14.0f);
            addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 16, 38.0f, 0.0f, 16.0f, 0.0f));
            updateColors();
        }

        /* access modifiers changed from: private */
        public void updateColors() {
            setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), getThemedColor("groupcreate_spanBackground")));
            this.titleView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
            if (this.thumbDrawable == null) {
                return;
            }
            if (this.data.filterType == 7) {
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_backgroundArchived"), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
                return;
            }
            Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_backgroundBlue"), false);
            Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
        }

        public void setData(MediaFilterData data2) {
            this.data = data2;
            this.avatarImageView.getImageReceiver().clearImage();
            if (data2.filterType == 7) {
                CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), NUM);
                this.thumbDrawable = createCircleDrawableWithIcon;
                createCircleDrawableWithIcon.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_backgroundArchived"), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
                this.titleView.setText(data2.title);
                return;
            }
            CombinedDrawable createCircleDrawableWithIcon2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), data2.iconResFilled);
            this.thumbDrawable = createCircleDrawableWithIcon2;
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon2, getThemedColor("avatar_backgroundBlue"), false);
            Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
            if (data2.filterType != 4) {
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
            } else if (data2.chat instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) data2.chat;
                if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == user.id) {
                    CombinedDrawable combinedDrawable = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), NUM);
                    combinedDrawable.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                    Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor("avatar_backgroundSaved"), false);
                    Theme.setCombinedDrawableColor(combinedDrawable, getThemedColor("avatar_actionBarIconBlue"), true);
                    this.avatarImageView.setImageDrawable(combinedDrawable);
                } else {
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setForUserOrChat(user, this.thumbDrawable);
                }
            } else if (data2.chat instanceof TLRPC.Chat) {
                this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                this.avatarImageView.getImageReceiver().setForUserOrChat((TLRPC.Chat) data2.chat, this.thumbDrawable);
            }
            this.titleView.setText(data2.title);
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        FilterView filterView;

        public ViewHolder(FilterView itemView) {
            super(itemView);
            this.filterView = itemView;
        }
    }

    public static class MediaFilterData {
        public TLObject chat;
        public DateData dateData;
        public final TLRPC.MessagesFilter filter;
        public final int filterType;
        public final int iconResFilled;
        public boolean removable = true;
        public final String title;

        public MediaFilterData(int iconResFilled2, String title2, TLRPC.MessagesFilter filter2, int filterType2) {
            this.iconResFilled = iconResFilled2;
            this.title = title2;
            this.filter = filter2;
            this.filterType = filterType2;
        }

        public void setUser(TLObject chat2) {
            this.chat = chat2;
        }

        public boolean isSameType(MediaFilterData filterData) {
            if (this.filterType == filterData.filterType) {
                return true;
            }
            if (!isMedia() || !filterData.isMedia()) {
                return false;
            }
            return true;
        }

        public boolean isMedia() {
            int i = this.filterType;
            return i == 0 || i == 1 || i == 2 || i == 3 || i == 5;
        }

        public void setDate(DateData dateData2) {
            this.dateData = dateData2;
        }
    }

    public static class DateData {
        public final long maxDate;
        public final long minDate;
        public final String title;

        private DateData(String title2, long minDate2, long maxDate2) {
            this.title = title2;
            this.minDate = minDate2;
            this.maxDate = maxDate2;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        arrayList.add(new ThemeDescription(this, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        return arrayList;
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!isEnabled()) {
            return false;
        }
        return super.onInterceptTouchEvent(e);
    }

    public boolean onTouchEvent(MotionEvent e) {
        if (!isEnabled()) {
            return false;
        }
        return super.onTouchEvent(e);
    }

    private static class UpdateCallback implements ListUpdateCallback {
        final RecyclerView.Adapter adapter;
        boolean changed;

        private UpdateCallback(RecyclerView.Adapter adapter2) {
            this.adapter = adapter2;
        }

        public void onInserted(int position, int count) {
            this.changed = true;
            this.adapter.notifyItemRangeInserted(position, count);
        }

        public void onRemoved(int position, int count) {
            this.changed = true;
            this.adapter.notifyItemRangeRemoved(position, count);
        }

        public void onMoved(int fromPosition, int toPosition) {
            this.changed = true;
            this.adapter.notifyItemMoved(fromPosition, toPosition);
        }

        public void onChanged(int position, int count, Object payload) {
            this.adapter.notifyItemRangeChanged(position, count, payload);
        }
    }
}
