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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
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

public class FiltersView extends RecyclerListView {
    public static final MediaFilterData[] filters = {new MediaFilterData(NUM, NUM, LocaleController.getString("SharedMediaTab2", NUM), new TLRPC$TL_inputMessagesFilterPhotoVideo(), 0), new MediaFilterData(NUM, NUM, LocaleController.getString("SharedLinksTab2", NUM), new TLRPC$TL_inputMessagesFilterUrl(), 2), new MediaFilterData(NUM, NUM, LocaleController.getString("SharedFilesTab2", NUM), new TLRPC$TL_inputMessagesFilterDocument(), 1), new MediaFilterData(NUM, NUM, LocaleController.getString("SharedMusicTab2", NUM), new TLRPC$TL_inputMessagesFilterMusic(), 3), new MediaFilterData(NUM, NUM, LocaleController.getString("SharedVoiceTab2", NUM), new TLRPC$TL_inputMessagesFilterRoundVoice(), 5)};
    private static final String[] formatter = {"dd.MM.yyyy", "dd/MM/yyyy", "dd MM yyyy"};
    private static final Pattern longDate = Pattern.compile("^([0-9]{1,2})(\\.| |\\\\)([0-9]{1,2})(\\.| |\\\\)([0-9]{1,4})$");
    private static final Pattern monthYearOrDayPatter = Pattern.compile("(\\w{3,}) ([0-9]{0,4})");
    private static final int[] numberOfDaysEachMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final Pattern shortDate = Pattern.compile("^([0-9]{1,4})(\\.| |\\\\)([0-9]{1,4})$");
    private static final Pattern yearOrDayAndMonthPatter = Pattern.compile("([0-9]{0,4}) (\\w{2,})");
    private static final Pattern yearPatter = Pattern.compile("20[0-9]{1,2}");
    DiffUtil.Callback diffUtilsCallback = new DiffUtil.Callback() {
        public boolean areContentsTheSame(int i, int i2) {
            return true;
        }

        public int getOldListSize() {
            return FiltersView.this.oldItems.size();
        }

        public int getNewListSize() {
            return FiltersView.this.usersFilters.size();
        }

        public boolean areItemsTheSame(int i, int i2) {
            MediaFilterData mediaFilterData = (MediaFilterData) FiltersView.this.oldItems.get(i);
            MediaFilterData mediaFilterData2 = (MediaFilterData) FiltersView.this.usersFilters.get(i2);
            if (mediaFilterData.isSameType(mediaFilterData2)) {
                if (mediaFilterData.filterType == 4) {
                    TLObject tLObject = mediaFilterData.chat;
                    if (tLObject instanceof TLRPC$User) {
                        TLObject tLObject2 = mediaFilterData2.chat;
                        if (tLObject2 instanceof TLRPC$User) {
                            if (((TLRPC$User) tLObject).id == ((TLRPC$User) tLObject2).id) {
                                return true;
                            }
                            return false;
                        }
                    }
                    TLObject tLObject3 = mediaFilterData.chat;
                    if (tLObject3 instanceof TLRPC$Chat) {
                        TLObject tLObject4 = mediaFilterData2.chat;
                        if (tLObject4 instanceof TLRPC$Chat) {
                            if (((TLRPC$Chat) tLObject3).id == ((TLRPC$Chat) tLObject4).id) {
                                return true;
                            }
                            return false;
                        }
                    }
                }
                if (mediaFilterData.filterType == 6) {
                    return mediaFilterData.title.equals(mediaFilterData2.title);
                }
            }
            return false;
        }
    };
    LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ArrayList<MediaFilterData> oldItems = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<MediaFilterData> usersFilters = new ArrayList<>();

    public FiltersView(Context context) {
        super(context);
        AnonymousClass1 r0 = new LinearLayoutManager(this, context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r0;
        r0.setOrientation(0);
        setLayoutManager(this.layoutManager);
        setAdapter(new Adapter());
        addItemDecoration(new RecyclerView.ItemDecoration(this) {
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
        setItemAnimator(new DefaultItemAnimator(this) {
            /* access modifiers changed from: protected */
            public long getAddAnimationDelay(long j, long j2, long j3) {
                return 0;
            }

            public long getAddDuration() {
                return 220;
            }

            /* access modifiers changed from: protected */
            public long getMoveAnimationDelay() {
                return 0;
            }

            public long getMoveDuration() {
                return 220;
            }

            public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
                boolean animateAdd = super.animateAdd(viewHolder);
                if (animateAdd) {
                    viewHolder.itemView.setScaleX(0.0f);
                    viewHolder.itemView.setScaleY(0.0f);
                }
                return animateAdd;
            }

            public void animateAddImpl(final RecyclerView.ViewHolder viewHolder) {
                final View view = viewHolder.itemView;
                final ViewPropertyAnimator animate = view.animate();
                this.mAddAnimations.add(viewHolder);
                animate.alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(getAddDuration()).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        AnonymousClass3.this.dispatchAddStarting(viewHolder);
                    }

                    public void onAnimationCancel(Animator animator) {
                        view.setAlpha(1.0f);
                    }

                    public void onAnimationEnd(Animator animator) {
                        animate.setListener((Animator.AnimatorListener) null);
                        AnonymousClass3.this.dispatchAddFinished(viewHolder);
                        AnonymousClass3.this.mAddAnimations.remove(viewHolder);
                        AnonymousClass3.this.dispatchFinishedWhenDone();
                    }
                }).start();
            }

            /* access modifiers changed from: protected */
            public void animateRemoveImpl(final RecyclerView.ViewHolder viewHolder) {
                final View view = viewHolder.itemView;
                final ViewPropertyAnimator animate = view.animate();
                this.mRemoveAnimations.add(viewHolder);
                animate.setDuration(getRemoveDuration()).alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        AnonymousClass3.this.dispatchRemoveStarting(viewHolder);
                    }

                    public void onAnimationEnd(Animator animator) {
                        animate.setListener((Animator.AnimatorListener) null);
                        view.setAlpha(1.0f);
                        view.setTranslationX(0.0f);
                        view.setTranslationY(0.0f);
                        view.setScaleX(1.0f);
                        view.setScaleY(1.0f);
                        AnonymousClass3.this.dispatchRemoveFinished(viewHolder);
                        AnonymousClass3.this.mRemoveAnimations.remove(viewHolder);
                        AnonymousClass3.this.dispatchFinishedWhenDone();
                    }
                }).start();
            }
        });
        setWillNotDraw(false);
        setHideIfEmpty(false);
        setSelectorRadius(AndroidUtilities.dp(28.0f));
        setSelectorDrawableColor(Theme.getColor("listSelectorSDK21"));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), NUM));
    }

    public MediaFilterData getFilterAt(int i) {
        if (this.usersFilters.isEmpty()) {
            return filters[i];
        }
        return this.usersFilters.get(i);
    }

    public void setUsersAndDates(ArrayList<TLObject> arrayList, ArrayList<DateData> arrayList2) {
        String str;
        this.oldItems.clear();
        this.oldItems.addAll(this.usersFilters);
        this.usersFilters.clear();
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLObject tLObject = arrayList.get(i);
                if (tLObject instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == tLRPC$User.id) {
                        str = LocaleController.getString("SavedMessages", NUM);
                    } else {
                        str = ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name, 10);
                    }
                    MediaFilterData mediaFilterData = new MediaFilterData(NUM, NUM, str, (TLRPC$MessagesFilter) null, 4);
                    mediaFilterData.setUser(tLRPC$User);
                    this.usersFilters.add(mediaFilterData);
                } else if (tLObject instanceof TLRPC$Chat) {
                    TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
                    String str2 = tLRPC$Chat.title;
                    if (str2.length() > 12) {
                        str2 = String.format("%s...", new Object[]{str2.substring(0, 10)});
                    }
                    MediaFilterData mediaFilterData2 = new MediaFilterData(NUM, NUM, str2, (TLRPC$MessagesFilter) null, 4);
                    mediaFilterData2.setUser(tLRPC$Chat);
                    this.usersFilters.add(mediaFilterData2);
                }
            }
        }
        if (arrayList2 != null) {
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                DateData dateData = arrayList2.get(i2);
                MediaFilterData mediaFilterData3 = new MediaFilterData(NUM, NUM, dateData.title, (TLRPC$MessagesFilter) null, 6);
                mediaFilterData3.setDate(dateData);
                this.usersFilters.add(mediaFilterData3);
            }
        }
        if (getAdapter() != null) {
            UpdateCallback updateCallback = new UpdateCallback(getAdapter());
            DiffUtil.calculateDiff(this.diffUtilsCallback).dispatchUpdatesTo((ListUpdateCallback) updateCallback);
            if (!this.usersFilters.isEmpty() && updateCallback.changed) {
                this.layoutManager.scrollToPositionWithOffset(0, 0);
            }
        }
    }

    public static void fillTipDates(String str, ArrayList<DateData> arrayList) {
        int month;
        Date parse;
        ArrayList<DateData> arrayList2 = arrayList;
        arrayList.clear();
        if (str != null) {
            String trim = str.trim();
            if (trim.length() >= 3) {
                if (LocaleController.getString("SearchTipToday", NUM).toLowerCase().startsWith(trim)) {
                    Calendar instance = Calendar.getInstance();
                    int i = instance.get(1);
                    int i2 = instance.get(2);
                    int i3 = instance.get(5);
                    Calendar calendar = instance;
                    int i4 = i;
                    int i5 = i2;
                    calendar.set(i4, i5, i3, 0, 0, 0);
                    long timeInMillis = instance.getTimeInMillis();
                    calendar.set(i4, i5, i3 + 1, 0, 0, 0);
                    arrayList2.add(new DateData(LocaleController.getString("SearchTipToday", NUM), timeInMillis, instance.getTimeInMillis() - 1));
                    return;
                }
                int dayOfWeek = getDayOfWeek(trim);
                if (dayOfWeek >= 0) {
                    Calendar instance2 = Calendar.getInstance();
                    long timeInMillis2 = instance2.getTimeInMillis();
                    instance2.set(7, dayOfWeek);
                    if (instance2.getTimeInMillis() > timeInMillis2) {
                        instance2.setTimeInMillis(instance2.getTimeInMillis() - NUM);
                    }
                    int i6 = instance2.get(1);
                    int i7 = instance2.get(2);
                    int i8 = instance2.get(5);
                    Calendar calendar2 = instance2;
                    int i9 = i6;
                    int i10 = i7;
                    calendar2.set(i9, i10, i8, 0, 0, 0);
                    long timeInMillis3 = instance2.getTimeInMillis();
                    calendar2.set(i9, i10, i8 + 1, 0, 0, 0);
                    arrayList2.add(new DateData(LocaleController.getInstance().formatterWeekLong.format(timeInMillis3), timeInMillis3, instance2.getTimeInMillis() - 1));
                    return;
                }
                Matcher matcher = shortDate.matcher(trim);
                if (matcher.matches()) {
                    String group = matcher.group(1);
                    String group2 = matcher.group(3);
                    int parseInt = Integer.parseInt(group);
                    int parseInt2 = Integer.parseInt(group2);
                    if (parseInt <= 0 || parseInt > 31) {
                        if (parseInt >= 2013 && parseInt2 <= 12) {
                            createForMonthYear(arrayList2, parseInt2 - 1, parseInt);
                        }
                    } else if (parseInt2 >= 2013 && parseInt <= 12) {
                        createForMonthYear(arrayList2, parseInt - 1, parseInt2);
                    } else if (parseInt2 <= 12) {
                        createForDayMonth(arrayList2, parseInt - 1, parseInt2 - 1);
                    }
                } else {
                    Matcher matcher2 = longDate.matcher(trim);
                    if (matcher2.matches()) {
                        String group3 = matcher2.group(1);
                        String group4 = matcher2.group(3);
                        String group5 = matcher2.group(5);
                        if (matcher2.group(2).equals(matcher2.group(4))) {
                            int parseInt3 = Integer.parseInt(group3) - 1;
                            int parseInt4 = Integer.parseInt(group4) - 1;
                            int parseInt5 = Integer.parseInt(group5);
                            int i11 = Calendar.getInstance().get(1);
                            if (validDateForMont(parseInt3, parseInt4 + 1) && parseInt5 >= 2013 && parseInt5 <= i11) {
                                Calendar instance3 = Calendar.getInstance();
                                Calendar calendar3 = instance3;
                                int i12 = parseInt5;
                                int i13 = parseInt4;
                                calendar3.set(i12, i13, parseInt3, 0, 0, 0);
                                long timeInMillis4 = instance3.getTimeInMillis();
                                calendar3.set(i12, i13, parseInt3 + 1, 0, 0, 0);
                                arrayList2.add(new DateData(LocaleController.getInstance().formatterYearMax.format(instance3.getTime()), timeInMillis4, instance3.getTimeInMillis() - 1));
                            }
                        }
                    } else if (yearPatter.matcher(trim).matches()) {
                        int intValue = Integer.valueOf(trim).intValue();
                        int i14 = Calendar.getInstance().get(1);
                        if (intValue < 2013) {
                            while (i14 >= 2013) {
                                Calendar instance4 = Calendar.getInstance();
                                Calendar calendar4 = instance4;
                                calendar4.set(i14, 0, 0, 0, 0, 0);
                                long timeInMillis5 = instance4.getTimeInMillis();
                                calendar4.set(i14 + 1, 0, 0, 0, 0, 0);
                                arrayList2.add(new DateData(Integer.toString(i14), timeInMillis5, instance4.getTimeInMillis() - 1));
                                i14--;
                            }
                        } else if (intValue <= i14) {
                            Calendar instance5 = Calendar.getInstance();
                            Calendar calendar5 = instance5;
                            calendar5.set(intValue, 0, 0, 0, 0, 0);
                            long timeInMillis6 = instance5.getTimeInMillis();
                            calendar5.set(intValue + 1, 0, 0, 0, 0, 0);
                            arrayList2.add(new DateData(Integer.toString(intValue), timeInMillis6, instance5.getTimeInMillis() - 1));
                        }
                    } else {
                        Matcher matcher3 = monthYearOrDayPatter.matcher(trim);
                        if (matcher3.matches()) {
                            String group6 = matcher3.group(1);
                            String group7 = matcher3.group(2);
                            int month2 = getMonth(group6);
                            if (month2 >= 0) {
                                int intValue2 = Integer.valueOf(group7).intValue();
                                if (intValue2 > 0 && intValue2 <= 31) {
                                    createForDayMonth(arrayList2, intValue2 - 1, month2);
                                    return;
                                } else if (intValue2 >= 2013) {
                                    createForMonthYear(arrayList2, month2, intValue2);
                                    return;
                                }
                            }
                        }
                        Matcher matcher4 = yearOrDayAndMonthPatter.matcher(trim);
                        if (matcher4.matches()) {
                            String group8 = matcher4.group(1);
                            int month3 = getMonth(matcher4.group(2));
                            if (month3 >= 0) {
                                int intValue3 = Integer.valueOf(group8).intValue();
                                if (intValue3 > 0 && intValue3 <= 31) {
                                    createForDayMonth(arrayList2, intValue3 - 1, month3);
                                    return;
                                } else if (intValue3 >= 2013) {
                                    createForMonthYear(arrayList2, month3, intValue3);
                                }
                            }
                        }
                        int i15 = 0;
                        while (true) {
                            String[] strArr = formatter;
                            if (i15 < strArr.length) {
                                try {
                                    if (isValidFormat(strArr[i15], trim, Locale.ENGLISH) && (parse = new SimpleDateFormat(formatter[i15]).parse(trim)) != null) {
                                        Calendar instance6 = Calendar.getInstance();
                                        instance6.setTime(parse);
                                        if (instance6.get(1) >= 2013) {
                                            long time = parse.getTime();
                                            arrayList2.add(new DateData(LocaleController.getInstance().formatterYearMax.format(parse), time, (86400000 + time) - 1));
                                            return;
                                        }
                                        return;
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                i15++;
                            } else if (!TextUtils.isEmpty(trim) && trim.length() > 2 && (month = getMonth(trim)) >= 0) {
                                for (int i16 = Calendar.getInstance().get(1); i16 >= 2013; i16--) {
                                    Calendar instance7 = Calendar.getInstance();
                                    Calendar calendar6 = instance7;
                                    int i17 = i16;
                                    calendar6.set(i17, month, 0, 0, 0, 0);
                                    long timeInMillis7 = instance7.getTimeInMillis();
                                    calendar6.set(i17, month + 1, 0, 0, 0, 0);
                                    long timeInMillis8 = instance7.getTimeInMillis() - 1;
                                    arrayList2.add(new DateData(LocaleController.getInstance().formatterMonthYear.format(timeInMillis8), timeInMillis7, timeInMillis8));
                                }
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createForMonthYear(ArrayList<DateData> arrayList, int i, int i2) {
        int i3 = i2;
        int i4 = Calendar.getInstance().get(1);
        if (i3 >= 2013 && i3 <= i4) {
            Calendar instance = Calendar.getInstance();
            Calendar calendar = instance;
            int i5 = i2;
            calendar.set(i5, i, 0, 0, 0, 0);
            long timeInMillis = instance.getTimeInMillis();
            calendar.set(i5, i + 1, 0, 0, 0, 0);
            long timeInMillis2 = instance.getTimeInMillis() - 1;
            arrayList.add(new DateData(LocaleController.getInstance().formatterMonthYear.format(timeInMillis2), timeInMillis, timeInMillis2));
        }
    }

    private static void createForDayMonth(ArrayList<DateData> arrayList, int i, int i2) {
        ArrayList<DateData> arrayList2 = arrayList;
        int i3 = i;
        if (validDateForMont(i, i2)) {
            int i4 = Calendar.getInstance().get(1);
            GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
            for (int i5 = i4; i5 >= 2013; i5--) {
                if (i2 != 1 || i3 != 28 || gregorianCalendar.isLeapYear(i5)) {
                    Calendar instance = Calendar.getInstance();
                    Calendar calendar = instance;
                    int i6 = i5;
                    int i7 = i2;
                    calendar.set(i6, i7, i, 0, 0, 0);
                    long timeInMillis = instance.getTimeInMillis();
                    calendar.set(i6, i7, i3 + 1, 0, 0, 0);
                    long timeInMillis2 = instance.getTimeInMillis() - 1;
                    if (i5 == i4) {
                        arrayList2.add(new DateData(LocaleController.getInstance().formatterDayMonth.format(timeInMillis2), timeInMillis, timeInMillis2));
                    } else {
                        arrayList2.add(new DateData(LocaleController.getInstance().formatterYearMax.format(timeInMillis2), timeInMillis, timeInMillis2));
                    }
                }
            }
        }
    }

    private static boolean validDateForMont(int i, int i2) {
        return i2 >= 0 && i2 < 12 && i >= 0 && i < numberOfDaysEachMonth[i2];
    }

    public static int getDayOfWeek(String str) {
        Calendar instance = Calendar.getInstance();
        if (str.length() <= 3) {
            return -1;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        int i = 0;
        while (i < 7) {
            instance.set(7, i);
            if (LocaleController.getInstance().formatterWeekLong.format(instance.getTime()).toLowerCase().startsWith(str) || simpleDateFormat.format(instance.getTime()).toLowerCase().startsWith(str)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static int getMonth(String str) {
        String[] strArr = {LocaleController.getString("January", NUM).toLowerCase(), LocaleController.getString("February", NUM).toLowerCase(), LocaleController.getString("March", NUM).toLowerCase(), LocaleController.getString("April", NUM).toLowerCase(), LocaleController.getString("May", NUM).toLowerCase(), LocaleController.getString("June", NUM).toLowerCase(), LocaleController.getString("July", NUM).toLowerCase(), LocaleController.getString("August", NUM).toLowerCase(), LocaleController.getString("September", NUM).toLowerCase(), LocaleController.getString("October", NUM).toLowerCase(), LocaleController.getString("November", NUM).toLowerCase(), LocaleController.getString("December", NUM).toLowerCase()};
        String[] strArr2 = new String[12];
        Calendar instance = Calendar.getInstance();
        for (int i = 0; i < 12; i++) {
            instance.set(2, i);
            strArr2[i] = instance.getDisplayName(2, 2, Locale.ENGLISH).toLowerCase();
        }
        for (int i2 = 0; i2 < 12; i2++) {
            if (strArr2[i2].startsWith(str) || strArr[i2].startsWith(str)) {
                return i2;
            }
        }
        return -1;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(3:4|5|6) */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002b, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001d, code lost:
        return j$.time.LocalDate.parse(r1, r0).format(r0).equals(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x002a, code lost:
        return j$.time.LocalTime.parse(r1, r0).format(r0).equals(r1);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x0011 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x001e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isValidFormat(java.lang.String r0, java.lang.String r1, java.util.Locale r2) {
        /*
            j$.time.format.DateTimeFormatter r0 = j$.time.format.DateTimeFormatter.ofPattern(r0, r2)
            j$.time.LocalDateTime r2 = j$.time.LocalDateTime.parse(r1, r0)     // Catch:{ DateTimeParseException -> 0x0011 }
            java.lang.String r2 = r2.format(r0)     // Catch:{ DateTimeParseException -> 0x0011 }
            boolean r0 = r2.equals(r1)     // Catch:{ DateTimeParseException -> 0x0011 }
            return r0
        L_0x0011:
            j$.time.LocalDate r2 = j$.time.LocalDate.parse(r1, r0)     // Catch:{ DateTimeParseException -> 0x001e }
            java.lang.String r2 = r2.format(r0)     // Catch:{ DateTimeParseException -> 0x001e }
            boolean r0 = r2.equals(r1)     // Catch:{ DateTimeParseException -> 0x001e }
            return r0
        L_0x001e:
            j$.time.LocalTime r2 = j$.time.LocalTime.parse(r1, r0)     // Catch:{ DateTimeParseException -> 0x002b }
            java.lang.String r0 = r2.format(r0)     // Catch:{ DateTimeParseException -> 0x002b }
            boolean r0 = r0.equals(r1)     // Catch:{ DateTimeParseException -> 0x002b }
            return r0
        L_0x002b:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.FiltersView.isValidFormat(java.lang.String, java.lang.String, java.util.Locale):boolean");
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0.0f, (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dividerPaint);
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
        setSelectorDrawableColor(Theme.getColor("listSelectorSDK21"));
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        private Adapter() {
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ViewHolder viewHolder = new ViewHolder(FiltersView.this, new FilterView(FiltersView.this, viewGroup.getContext()));
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-2, AndroidUtilities.dp(32.0f));
            layoutParams.topMargin = AndroidUtilities.dp(6.0f);
            viewHolder.itemView.setLayoutParams(layoutParams);
            return viewHolder;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ((ViewHolder) viewHolder).filterView.setData((MediaFilterData) FiltersView.this.usersFilters.get(i));
        }

        public int getItemCount() {
            return FiltersView.this.usersFilters.size();
        }
    }

    public class FilterView extends FrameLayout {
        BackupImageView avatarImageView;
        Drawable thumbDrawable;
        TextView titleView;

        public FilterView(FiltersView filtersView, Context context) {
            super(context);
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
            setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), Theme.getColor("groupcreate_spanBackground")));
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            Drawable drawable = this.thumbDrawable;
            if (drawable != null) {
                Theme.setCombinedDrawableColor(drawable, Theme.getColor("avatar_backgroundBlue"), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, Theme.getColor("avatar_actionBarIconBlue"), true);
            }
        }

        public void setData(MediaFilterData mediaFilterData) {
            this.avatarImageView.getImageReceiver().clearImage();
            CombinedDrawable createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), mediaFilterData.iconResFilled);
            this.thumbDrawable = createCircleDrawableWithIcon;
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, Theme.getColor("avatar_backgroundBlue"), false);
            Theme.setCombinedDrawableColor(this.thumbDrawable, Theme.getColor("avatar_actionBarIconBlue"), true);
            if (mediaFilterData.filterType == 4) {
                TLObject tLObject = mediaFilterData.chat;
                if (tLObject instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                    if (UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id == tLRPC$User.id) {
                        CombinedDrawable createCircleDrawableWithIcon2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), NUM);
                        createCircleDrawableWithIcon2.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                        Theme.setCombinedDrawableColor(createCircleDrawableWithIcon2, Theme.getColor("avatar_backgroundSaved"), false);
                        Theme.setCombinedDrawableColor(createCircleDrawableWithIcon2, Theme.getColor("avatar_actionBarIconBlue"), true);
                        this.avatarImageView.setImageDrawable(createCircleDrawableWithIcon2);
                    } else {
                        this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                        this.avatarImageView.getImageReceiver().setImage(ImageLocation.getForUser(tLRPC$User, false), "50_50", this.thumbDrawable, (String) null, tLRPC$User, 0);
                    }
                } else if (tLObject instanceof TLRPC$Chat) {
                    TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
                    this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                    this.avatarImageView.getImageReceiver().setImage(ImageLocation.getForChat(tLRPC$Chat, false), "50_50", this.thumbDrawable, (String) null, tLRPC$Chat, 0);
                }
            } else {
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
            }
            this.titleView.setText(mediaFilterData.title);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        FilterView filterView;

        public ViewHolder(FiltersView filtersView, FilterView filterView2) {
            super(filterView2);
            this.filterView = filterView2;
        }
    }

    public static class MediaFilterData {
        public TLObject chat;
        public DateData dateData;
        public final TLRPC$MessagesFilter filter;
        public final int filterType;
        public final int iconResFilled;
        public final String title;

        public MediaFilterData(int i, int i2, String str, TLRPC$MessagesFilter tLRPC$MessagesFilter, int i3) {
            this.iconResFilled = i2;
            this.title = str;
            this.filter = tLRPC$MessagesFilter;
            this.filterType = i3;
        }

        public void setUser(TLObject tLObject) {
            this.chat = tLObject;
        }

        public boolean isSameType(MediaFilterData mediaFilterData) {
            if (this.filterType == mediaFilterData.filterType) {
                return true;
            }
            if (!isMedia() || !mediaFilterData.isMedia()) {
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

        private DateData(String str, long j, long j2) {
            this.title = str;
            this.minDate = j;
            this.maxDate = j2;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        arrayList.add(new ThemeDescription(this, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        return arrayList;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }

    private class UpdateCallback implements ListUpdateCallback {
        final RecyclerView.Adapter adapter;
        boolean changed;

        private UpdateCallback(FiltersView filtersView, RecyclerView.Adapter adapter2) {
            this.adapter = adapter2;
        }

        public void onInserted(int i, int i2) {
            this.changed = true;
            this.adapter.notifyItemRangeInserted(i, i2);
        }

        public void onRemoved(int i, int i2) {
            this.changed = true;
            this.adapter.notifyItemRangeRemoved(i, i2);
        }

        public void onMoved(int i, int i2) {
            this.changed = true;
            this.adapter.notifyItemMoved(i, i2);
        }

        public void onChanged(int i, int i2, Object obj) {
            this.adapter.notifyItemRangeChanged(i, i2, obj);
        }
    }
}
