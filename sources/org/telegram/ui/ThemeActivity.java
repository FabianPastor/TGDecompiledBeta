package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.time.SunDate;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.BrightnessControlCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemeCell;
import org.telegram.ui.Cells.ThemeTypeCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ThemeEditorView;

public class ThemeActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int THEME_TYPE_BASIC = 0;
    public static final int THEME_TYPE_NIGHT = 1;
    private int automaticBrightnessInfoRow;
    private int automaticBrightnessRow;
    private int automaticHeaderRow;
    private int currentType;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener();
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private GpsLocationListener networkLocationListener = new GpsLocationListener();
    private int newThemeInfoRow;
    private int newThemeRow;
    private int nightAutomaticRow;
    private int nightDisabledRow;
    private int nightScheduledRow;
    private int nightThemeRow;
    private int nightTypeInfoRow;
    private int preferedHeaderRow;
    private boolean previousByLocation;
    private int previousUpdatedType;
    private int rowCount;
    private int scheduleFromRow;
    private int scheduleFromToInfoRow;
    private int scheduleHeaderRow;
    private int scheduleLocationInfoRow;
    private int scheduleLocationRow;
    private int scheduleToRow;
    private int scheduleUpdateLocationRow;
    private int themeEndRow;
    private int themeInfoRow;
    private int themeStartRow;
    private boolean updatingLocation;

    /* renamed from: org.telegram.ui.ThemeActivity$3 */
    class C17183 implements OnClickListener {
        C17183() {
        }

        public void onClick(android.content.DialogInterface r2, int r3) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r1 = this;
            r2 = org.telegram.ui.ThemeActivity.this;
            r2 = r2.getParentActivity();
            if (r2 != 0) goto L_0x0009;
        L_0x0008:
            return;
        L_0x0009:
            r2 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x0019 }
            r2 = r2.getParentActivity();	 Catch:{ Exception -> 0x0019 }
            r3 = new android.content.Intent;	 Catch:{ Exception -> 0x0019 }
            r0 = "android.settings.LOCATION_SOURCE_SETTINGS";	 Catch:{ Exception -> 0x0019 }
            r3.<init>(r0);	 Catch:{ Exception -> 0x0019 }
            r2.startActivity(r3);	 Catch:{ Exception -> 0x0019 }
        L_0x0019:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.3.onClick(android.content.DialogInterface, int):void");
        }
    }

    /* renamed from: org.telegram.ui.ThemeActivity$4 */
    class C17204 implements Runnable {
        C17204() {
        }

        public void run() {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r7 = this;
            r0 = 0;
            r1 = new android.location.Geocoder;	 Catch:{ Exception -> 0x0027 }
            r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0027 }
            r3 = java.util.Locale.getDefault();	 Catch:{ Exception -> 0x0027 }
            r1.<init>(r2, r3);	 Catch:{ Exception -> 0x0027 }
            r2 = org.telegram.ui.ActionBar.Theme.autoNightLocationLatitude;	 Catch:{ Exception -> 0x0027 }
            r4 = org.telegram.ui.ActionBar.Theme.autoNightLocationLongitude;	 Catch:{ Exception -> 0x0027 }
            r6 = 1;	 Catch:{ Exception -> 0x0027 }
            r1 = r1.getFromLocation(r2, r4, r6);	 Catch:{ Exception -> 0x0027 }
            r2 = r1.size();	 Catch:{ Exception -> 0x0027 }
            if (r2 <= 0) goto L_0x0027;	 Catch:{ Exception -> 0x0027 }
        L_0x001b:
            r2 = 0;	 Catch:{ Exception -> 0x0027 }
            r1 = r1.get(r2);	 Catch:{ Exception -> 0x0027 }
            r1 = (android.location.Address) r1;	 Catch:{ Exception -> 0x0027 }
            r1 = r1.getLocality();	 Catch:{ Exception -> 0x0027 }
            r0 = r1;
        L_0x0027:
            r1 = new org.telegram.ui.ThemeActivity$4$1;
            r1.<init>(r0);
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.4.run():void");
        }
    }

    /* renamed from: org.telegram.ui.ThemeActivity$5 */
    class C17215 implements OnClickListener {
        C17215() {
        }

        @TargetApi(9)
        public void onClick(DialogInterface dialogInterface, int i) {
            if (ThemeActivity.this.getParentActivity() != null) {
                try {
                    dialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    i = new StringBuilder();
                    i.append("package:");
                    i.append(ApplicationLoader.applicationContext.getPackageName());
                    dialogInterface.setData(Uri.parse(i.toString()));
                    ThemeActivity.this.getParentActivity().startActivity(dialogInterface);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
    }

    private class GpsLocationListener implements LocationListener {
        public void onProviderDisabled(String str) {
        }

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
        }

        private GpsLocationListener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                ThemeActivity.this.stopLocationUpdate();
                ThemeActivity.this.updateSunTime(location, false);
            }
        }
    }

    /* renamed from: org.telegram.ui.ThemeActivity$1 */
    class C22961 extends ActionBarMenuOnItemClick {
        C22961() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ThemeActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ThemeActivity$2 */
    class C22972 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ThemeActivity$2$1 */
        class C17121 implements OnClickListener {
            public void onClick(DialogInterface dialogInterface, int i) {
            }

            C17121() {
            }
        }

        /* renamed from: org.telegram.ui.ThemeActivity$2$2 */
        class C17132 implements OnEditorActionListener {
            C17132() {
            }

            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                AndroidUtilities.hideKeyboard(textView);
                return null;
            }
        }

        C22972() {
        }

        public void onItemClick(View view, int i) {
            final int i2 = i;
            int i3 = 0;
            if (i2 == ThemeActivity.this.newThemeRow) {
                if (ThemeActivity.this.getParentActivity() != null) {
                    final View editTextBoldCursor = new EditTextBoldCursor(ThemeActivity.this.getParentActivity());
                    editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(ThemeActivity.this.getParentActivity(), true));
                    Builder builder = new Builder(ThemeActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("NewTheme", C0446R.string.NewTheme));
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C17121());
                    View linearLayout = new LinearLayout(ThemeActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    builder.setView(linearLayout);
                    View textView = new TextView(ThemeActivity.this.getParentActivity());
                    textView.setText(LocaleController.formatString("EnterThemeName", C0446R.string.EnterThemeName, new Object[0]));
                    textView.setTextSize(16.0f);
                    textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
                    textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
                    editTextBoldCursor.setTextSize(1, 16.0f);
                    editTextBoldCursor.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                    editTextBoldCursor.setMaxLines(1);
                    editTextBoldCursor.setLines(1);
                    editTextBoldCursor.setInputType(16385);
                    editTextBoldCursor.setGravity(51);
                    editTextBoldCursor.setSingleLine(true);
                    editTextBoldCursor.setImeOptions(6);
                    editTextBoldCursor.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
                    editTextBoldCursor.setCursorWidth(1.5f);
                    editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                    linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
                    editTextBoldCursor.setOnEditorActionListener(new C17132());
                    final Dialog create = builder.create();
                    create.setOnShowListener(new OnShowListener() {

                        /* renamed from: org.telegram.ui.ThemeActivity$2$3$1 */
                        class C17141 implements Runnable {
                            C17141() {
                            }

                            public void run() {
                                editTextBoldCursor.requestFocus();
                                AndroidUtilities.showKeyboard(editTextBoldCursor);
                            }
                        }

                        public void onShow(DialogInterface dialogInterface) {
                            AndroidUtilities.runOnUIThread(new C17141());
                        }
                    });
                    ThemeActivity.this.showDialog(create);
                    create.getButton(-1).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            if (editTextBoldCursor.length() == null) {
                                Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
                                if (vibrator != null) {
                                    vibrator.vibrate(200);
                                }
                                AndroidUtilities.shakeView(editTextBoldCursor, 2.0f, 0);
                                return;
                            }
                            view = new ThemeEditorView();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(editTextBoldCursor.getText().toString());
                            stringBuilder.append(".attheme");
                            String stringBuilder2 = stringBuilder.toString();
                            view.show(ThemeActivity.this.getParentActivity(), stringBuilder2);
                            Theme.saveCurrentTheme(stringBuilder2, true);
                            ThemeActivity.this.updateRows();
                            create.dismiss();
                            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                            if (!globalMainSettings.getBoolean("themehint", false)) {
                                globalMainSettings.edit().putBoolean("themehint", true).commit();
                                try {
                                    Toast.makeText(ThemeActivity.this.getParentActivity(), LocaleController.getString("CreateNewThemeHelp", C0446R.string.CreateNewThemeHelp), 1).show();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        }
                    });
                }
            } else if (i2 >= ThemeActivity.this.themeStartRow && i2 < ThemeActivity.this.themeEndRow) {
                int access$500 = i2 - ThemeActivity.this.themeStartRow;
                if (access$500 >= 0 && access$500 < Theme.themes.size()) {
                    ThemeInfo themeInfo = (ThemeInfo) Theme.themes.get(access$500);
                    if (ThemeActivity.this.currentType == 0) {
                        Theme.applyTheme(themeInfo);
                        if (ThemeActivity.this.parentLayout != null) {
                            ThemeActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
                        }
                        ThemeActivity.this.finishFragment();
                    } else {
                        Theme.setCurrentNightTheme(themeInfo);
                        access$500 = ThemeActivity.this.listView.getChildCount();
                        while (i3 < access$500) {
                            View childAt = ThemeActivity.this.listView.getChildAt(i3);
                            if (childAt instanceof ThemeCell) {
                                ((ThemeCell) childAt).updateCurrentThemeCheck();
                            }
                            i3++;
                        }
                    }
                }
            } else if (i2 == ThemeActivity.this.nightThemeRow) {
                ThemeActivity.this.presentFragment(new ThemeActivity(1));
            } else if (i2 == ThemeActivity.this.nightDisabledRow) {
                Theme.selectedAutoNightType = 0;
                ThemeActivity.this.updateRows();
                Theme.checkAutoNightThemeConditions();
            } else if (i2 == ThemeActivity.this.nightScheduledRow) {
                Theme.selectedAutoNightType = 1;
                if (Theme.autoNightScheduleByLocation) {
                    ThemeActivity.this.updateSunTime(null, true);
                }
                ThemeActivity.this.updateRows();
                Theme.checkAutoNightThemeConditions();
            } else if (i2 == ThemeActivity.this.nightAutomaticRow) {
                Theme.selectedAutoNightType = 2;
                ThemeActivity.this.updateRows();
                Theme.checkAutoNightThemeConditions();
            } else if (i2 == ThemeActivity.this.scheduleLocationRow) {
                Theme.autoNightScheduleByLocation ^= true;
                ((TextCheckCell) view).setChecked(Theme.autoNightScheduleByLocation);
                ThemeActivity.this.updateRows();
                if (Theme.autoNightScheduleByLocation) {
                    ThemeActivity.this.updateSunTime(null, true);
                }
                Theme.checkAutoNightThemeConditions();
            } else {
                if (i2 != ThemeActivity.this.scheduleFromRow) {
                    if (i2 != ThemeActivity.this.scheduleToRow) {
                        if (i2 == ThemeActivity.this.scheduleUpdateLocationRow) {
                            ThemeActivity.this.updateSunTime(null, true);
                        }
                    }
                }
                if (ThemeActivity.this.getParentActivity() != null) {
                    int i4;
                    int i5;
                    if (i2 == ThemeActivity.this.scheduleFromRow) {
                        i4 = Theme.autoNightDayStartTime / 60;
                        i5 = Theme.autoNightDayStartTime - (i4 * 60);
                    } else {
                        i4 = Theme.autoNightDayEndTime / 60;
                        i5 = Theme.autoNightDayEndTime - (i4 * 60);
                    }
                    final TextSettingsCell textSettingsCell = (TextSettingsCell) view;
                    ThemeActivity.this.showDialog(new TimePickerDialog(ThemeActivity.this.getParentActivity(), new OnTimeSetListener() {
                        public void onTimeSet(TimePicker timePicker, int i, int i2) {
                            timePicker = (i * 60) + i2;
                            if (i2 == ThemeActivity.this.scheduleFromRow) {
                                Theme.autoNightDayStartTime = timePicker;
                                textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", C0446R.string.AutoNightFrom), String.format("%02d:%02d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}), true);
                                return;
                            }
                            Theme.autoNightDayEndTime = timePicker;
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", C0446R.string.AutoNightTo), String.format("%02d:%02d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}), true);
                        }
                    }, i4, i5, true));
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.ThemeActivity$ListAdapter$1 */
        class C17241 implements View.OnClickListener {
            C17241() {
            }

            public void onClick(View view) {
                view = ((ThemeCell) view.getParent()).getCurrentThemeInfo();
                if (ThemeActivity.this.getParentActivity() != null) {
                    BottomSheet.Builder builder = new BottomSheet.Builder(ThemeActivity.this.getParentActivity());
                    builder.setItems(view.pathToFile == null ? new CharSequence[]{LocaleController.getString("ShareFile", C0446R.string.ShareFile)} : new CharSequence[]{LocaleController.getString("ShareFile", C0446R.string.ShareFile), LocaleController.getString("Edit", C0446R.string.Edit), LocaleController.getString("Delete", C0446R.string.Delete)}, new OnClickListener() {

                        /* renamed from: org.telegram.ui.ThemeActivity$ListAdapter$1$1$1 */
                        class C17221 implements OnClickListener {
                            C17221() {
                            }

                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Theme.deleteTheme(view) != null) {
                                    ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
                                }
                                ThemeActivity.this.updateRows();
                            }
                        }

                        public void onClick(android.content.DialogInterface r5, int r6) {
                            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
                            /*
                            r4 = this;
                            r5 = 0;
                            r0 = 1;
                            if (r6 != 0) goto L_0x0117;
                        L_0x0004:
                            r6 = r7;
                            r6 = r6.pathToFile;
                            if (r6 != 0) goto L_0x008c;
                        L_0x000a:
                            r6 = r7;
                            r6 = r6.assetName;
                            if (r6 != 0) goto L_0x008c;
                        L_0x0010:
                            r6 = new java.lang.StringBuilder;
                            r6.<init>();
                            r1 = org.telegram.ui.ActionBar.Theme.getDefaultColors();
                            r1 = r1.entrySet();
                            r1 = r1.iterator();
                        L_0x0021:
                            r2 = r1.hasNext();
                            if (r2 == 0) goto L_0x0048;
                        L_0x0027:
                            r2 = r1.next();
                            r2 = (java.util.Map.Entry) r2;
                            r3 = r2.getKey();
                            r3 = (java.lang.String) r3;
                            r6.append(r3);
                            r3 = "=";
                            r6.append(r3);
                            r2 = r2.getValue();
                            r6.append(r2);
                            r2 = "\n";
                            r6.append(r2);
                            goto L_0x0021;
                        L_0x0048:
                            r1 = new java.io.File;
                            r2 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
                            r3 = "default_theme.attheme";
                            r1.<init>(r2, r3);
                            r2 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x006f, all -> 0x006b }
                            r2.<init>(r1);	 Catch:{ Exception -> 0x006f, all -> 0x006b }
                            r5 = r6.toString();	 Catch:{ Exception -> 0x0069 }
                            r5 = r5.getBytes();	 Catch:{ Exception -> 0x0069 }
                            r2.write(r5);	 Catch:{ Exception -> 0x0069 }
                            if (r2 == 0) goto L_0x00a4;
                        L_0x0065:
                            r2.close();	 Catch:{ Exception -> 0x007b }
                            goto L_0x00a4;
                        L_0x0069:
                            r5 = move-exception;
                            goto L_0x0072;
                        L_0x006b:
                            r6 = move-exception;
                            r2 = r5;
                            r5 = r6;
                            goto L_0x0081;
                        L_0x006f:
                            r6 = move-exception;
                            r2 = r5;
                            r5 = r6;
                        L_0x0072:
                            org.telegram.messenger.FileLog.m3e(r5);	 Catch:{ all -> 0x0080 }
                            if (r2 == 0) goto L_0x00a4;
                        L_0x0077:
                            r2.close();	 Catch:{ Exception -> 0x007b }
                            goto L_0x00a4;
                        L_0x007b:
                            r5 = move-exception;
                            org.telegram.messenger.FileLog.m3e(r5);
                            goto L_0x00a4;
                        L_0x0080:
                            r5 = move-exception;
                        L_0x0081:
                            if (r2 == 0) goto L_0x008b;
                        L_0x0083:
                            r2.close();	 Catch:{ Exception -> 0x0087 }
                            goto L_0x008b;
                        L_0x0087:
                            r6 = move-exception;
                            org.telegram.messenger.FileLog.m3e(r6);
                        L_0x008b:
                            throw r5;
                        L_0x008c:
                            r5 = r7;
                            r5 = r5.assetName;
                            if (r5 == 0) goto L_0x009b;
                        L_0x0092:
                            r5 = r7;
                            r5 = r5.assetName;
                            r1 = org.telegram.ui.ActionBar.Theme.getAssetFile(r5);
                            goto L_0x00a4;
                        L_0x009b:
                            r1 = new java.io.File;
                            r5 = r7;
                            r5 = r5.pathToFile;
                            r1.<init>(r5);
                        L_0x00a4:
                            r5 = new java.io.File;
                            r6 = 4;
                            r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
                            r2 = r1.getName();
                            r5.<init>(r6, r2);
                            r6 = org.telegram.messenger.AndroidUtilities.copyFile(r1, r5);	 Catch:{ Exception -> 0x0111 }
                            if (r6 != 0) goto L_0x00b9;	 Catch:{ Exception -> 0x0111 }
                        L_0x00b8:
                            return;	 Catch:{ Exception -> 0x0111 }
                        L_0x00b9:
                            r6 = new android.content.Intent;	 Catch:{ Exception -> 0x0111 }
                            r1 = "android.intent.action.SEND";	 Catch:{ Exception -> 0x0111 }
                            r6.<init>(r1);	 Catch:{ Exception -> 0x0111 }
                            r1 = "text/xml";	 Catch:{ Exception -> 0x0111 }
                            r6.setType(r1);	 Catch:{ Exception -> 0x0111 }
                            r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0111 }
                            r2 = 24;
                            if (r1 < r2) goto L_0x00ee;
                        L_0x00cb:
                            r1 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x00e4 }
                            r2 = org.telegram.ui.ThemeActivity.ListAdapter.C17241.this;	 Catch:{ Exception -> 0x00e4 }
                            r2 = org.telegram.ui.ThemeActivity.ListAdapter.this;	 Catch:{ Exception -> 0x00e4 }
                            r2 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x00e4 }
                            r2 = r2.getParentActivity();	 Catch:{ Exception -> 0x00e4 }
                            r3 = "org.telegram.messenger.provider";	 Catch:{ Exception -> 0x00e4 }
                            r2 = android.support.v4.content.FileProvider.getUriForFile(r2, r3, r5);	 Catch:{ Exception -> 0x00e4 }
                            r6.putExtra(r1, r2);	 Catch:{ Exception -> 0x00e4 }
                            r6.setFlags(r0);	 Catch:{ Exception -> 0x00e4 }
                            goto L_0x00f7;
                        L_0x00e4:
                            r0 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x0111 }
                            r5 = android.net.Uri.fromFile(r5);	 Catch:{ Exception -> 0x0111 }
                            r6.putExtra(r0, r5);	 Catch:{ Exception -> 0x0111 }
                            goto L_0x00f7;	 Catch:{ Exception -> 0x0111 }
                        L_0x00ee:
                            r0 = "android.intent.extra.STREAM";	 Catch:{ Exception -> 0x0111 }
                            r5 = android.net.Uri.fromFile(r5);	 Catch:{ Exception -> 0x0111 }
                            r6.putExtra(r0, r5);	 Catch:{ Exception -> 0x0111 }
                        L_0x00f7:
                            r5 = org.telegram.ui.ThemeActivity.ListAdapter.C17241.this;	 Catch:{ Exception -> 0x0111 }
                            r5 = org.telegram.ui.ThemeActivity.ListAdapter.this;	 Catch:{ Exception -> 0x0111 }
                            r5 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x0111 }
                            r0 = "ShareFile";	 Catch:{ Exception -> 0x0111 }
                            r1 = NUM; // 0x7f0c05ef float:1.8612273E38 double:1.053098149E-314;	 Catch:{ Exception -> 0x0111 }
                            r0 = org.telegram.messenger.LocaleController.getString(r0, r1);	 Catch:{ Exception -> 0x0111 }
                            r6 = android.content.Intent.createChooser(r6, r0);	 Catch:{ Exception -> 0x0111 }
                            r0 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;	 Catch:{ Exception -> 0x0111 }
                            r5.startActivityForResult(r6, r0);	 Catch:{ Exception -> 0x0111 }
                            goto L_0x01ac;
                        L_0x0111:
                            r5 = move-exception;
                            org.telegram.messenger.FileLog.m3e(r5);
                            goto L_0x01ac;
                        L_0x0117:
                            if (r6 != r0) goto L_0x014e;
                        L_0x0119:
                            r5 = org.telegram.ui.ThemeActivity.ListAdapter.C17241.this;
                            r5 = org.telegram.ui.ThemeActivity.ListAdapter.this;
                            r5 = org.telegram.ui.ThemeActivity.this;
                            r5 = r5.parentLayout;
                            if (r5 == 0) goto L_0x01ac;
                        L_0x0125:
                            r5 = r7;
                            org.telegram.ui.ActionBar.Theme.applyTheme(r5);
                            r5 = org.telegram.ui.ThemeActivity.ListAdapter.C17241.this;
                            r5 = org.telegram.ui.ThemeActivity.ListAdapter.this;
                            r5 = org.telegram.ui.ThemeActivity.this;
                            r5 = r5.parentLayout;
                            r5.rebuildAllFragmentViews(r0, r0);
                            r5 = new org.telegram.ui.Components.ThemeEditorView;
                            r5.<init>();
                            r6 = org.telegram.ui.ThemeActivity.ListAdapter.C17241.this;
                            r6 = org.telegram.ui.ThemeActivity.ListAdapter.this;
                            r6 = org.telegram.ui.ThemeActivity.this;
                            r6 = r6.getParentActivity();
                            r0 = r7;
                            r0 = r0.name;
                            r5.show(r6, r0);
                            goto L_0x01ac;
                        L_0x014e:
                            r6 = org.telegram.ui.ThemeActivity.ListAdapter.C17241.this;
                            r6 = org.telegram.ui.ThemeActivity.ListAdapter.this;
                            r6 = org.telegram.ui.ThemeActivity.this;
                            r6 = r6.getParentActivity();
                            if (r6 != 0) goto L_0x015b;
                        L_0x015a:
                            return;
                        L_0x015b:
                            r6 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
                            r0 = org.telegram.ui.ThemeActivity.ListAdapter.C17241.this;
                            r0 = org.telegram.ui.ThemeActivity.ListAdapter.this;
                            r0 = org.telegram.ui.ThemeActivity.this;
                            r0 = r0.getParentActivity();
                            r6.<init>(r0);
                            r0 = "DeleteThemeAlert";
                            r1 = NUM; // 0x7f0c0200 float:1.861023E38 double:1.0530976514E-314;
                            r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
                            r6.setMessage(r0);
                            r0 = "AppName";
                            r1 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
                            r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
                            r6.setTitle(r0);
                            r0 = "Delete";
                            r1 = NUM; // 0x7f0c01ec float:1.861019E38 double:1.0530976415E-314;
                            r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
                            r1 = new org.telegram.ui.ThemeActivity$ListAdapter$1$1$1;
                            r1.<init>();
                            r6.setPositiveButton(r0, r1);
                            r0 = "Cancel";
                            r1 = NUM; // 0x7f0c0107 float:1.8609725E38 double:1.0530975284E-314;
                            r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
                            r6.setNegativeButton(r0, r5);
                            r5 = org.telegram.ui.ThemeActivity.ListAdapter.C17241.this;
                            r5 = org.telegram.ui.ThemeActivity.ListAdapter.this;
                            r5 = org.telegram.ui.ThemeActivity.this;
                            r6 = r6.create();
                            r5.showDialog(r6);
                        L_0x01ac:
                            return;
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.ListAdapter.1.1.onClick(android.content.DialogInterface, int):void");
                        }
                    });
                    ThemeActivity.this.showDialog(builder.create());
                }
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ThemeActivity.this.rowCount;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getItemViewType();
            if (viewHolder == null || viewHolder == 1 || viewHolder == 4) {
                return true;
            }
            return viewHolder == 7;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    i = this.mContext;
                    boolean z = true;
                    if (ThemeActivity.this.currentType != 1) {
                        z = false;
                    }
                    viewGroup = new ThemeCell(i, z);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    if (ThemeActivity.this.currentType == 0) {
                        ((ThemeCell) viewGroup).setOnOptionsClick(new C17241());
                        break;
                    }
                    break;
                case 1:
                    viewGroup = new TextSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    viewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 3:
                    viewGroup = new ShadowSectionCell(this.mContext);
                    viewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 4:
                    viewGroup = new ThemeTypeCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    viewGroup = new HeaderCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    viewGroup = new BrightnessControlCell(this.mContext) {
                        protected void didChangedValue(float f) {
                            int i = (int) (Theme.autoNightBrighnessThreshold * 100.0f);
                            int i2 = (int) (f * 100.0f);
                            Theme.autoNightBrighnessThreshold = f;
                            if (i != i2) {
                                Holder holder = (Holder) ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.automaticBrightnessInfoRow);
                                if (holder != null) {
                                    ((TextInfoPrivacyCell) holder.itemView).setText(LocaleController.formatString("AutoNightBrightnessInfo", C0446R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (100.0f * Theme.autoNightBrighnessThreshold))));
                                }
                                Theme.checkAutoNightThemeConditions(true);
                            }
                        }
                    };
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    viewGroup = new TextCheckCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            boolean z2 = true;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    i -= ThemeActivity.this.themeStartRow;
                    ThemeInfo themeInfo = (ThemeInfo) Theme.themes.get(i);
                    ThemeCell themeCell = (ThemeCell) viewHolder.itemView;
                    if (i != Theme.themes.size() - 1) {
                        z = true;
                    }
                    themeCell.setTheme(themeInfo, z);
                    return;
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.newThemeRow) {
                        textSettingsCell.setText(LocaleController.getString("CreateNewTheme", C0446R.string.CreateNewTheme), false);
                        return;
                    } else if (i == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType != 0) {
                            if (Theme.getCurrentNightTheme() != 0) {
                                textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTheme", C0446R.string.AutoNightTheme), Theme.getCurrentNightThemeName(), true);
                                return;
                            }
                        }
                        textSettingsCell.setText(LocaleController.getString("AutoNightTheme", C0446R.string.AutoNightTheme), true);
                        return;
                    } else if (i == ThemeActivity.this.scheduleFromRow) {
                        r0 = Theme.autoNightDayStartTime - ((Theme.autoNightDayStartTime / 60) * 60);
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", C0446R.string.AutoNightFrom), String.format("%02d:%02d", new Object[]{Integer.valueOf(i), Integer.valueOf(r0)}), true);
                        return;
                    } else if (i == ThemeActivity.this.scheduleToRow) {
                        r0 = Theme.autoNightDayEndTime - ((Theme.autoNightDayEndTime / 60) * 60);
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", C0446R.string.AutoNightTo), String.format("%02d:%02d", new Object[]{Integer.valueOf(i), Integer.valueOf(r0)}), false);
                        return;
                    } else if (i == ThemeActivity.this.scheduleUpdateLocationRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", C0446R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.newThemeInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("CreateNewThemeInfo", C0446R.string.CreateNewThemeInfo));
                        return;
                    } else if (i == ThemeActivity.this.automaticBrightnessInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.formatString("AutoNightBrightnessInfo", C0446R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (100.0f * Theme.autoNightBrighnessThreshold))));
                        return;
                    } else if (i == ThemeActivity.this.scheduleLocationInfoRow) {
                        textInfoPrivacyCell.setText(ThemeActivity.this.getLocationSunString());
                        return;
                    } else {
                        return;
                    }
                case 4:
                    ThemeTypeCell themeTypeCell = (ThemeTypeCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.nightDisabledRow) {
                        i = LocaleController.getString("AutoNightDisabled", C0446R.string.AutoNightDisabled);
                        if (Theme.selectedAutoNightType == 0) {
                            z = true;
                        }
                        themeTypeCell.setValue(i, z, true);
                        return;
                    } else if (i == ThemeActivity.this.nightScheduledRow) {
                        i = LocaleController.getString("AutoNightScheduled", C0446R.string.AutoNightScheduled);
                        if (Theme.selectedAutoNightType == 1) {
                            z = true;
                        }
                        themeTypeCell.setValue(i, z, true);
                        return;
                    } else if (i == ThemeActivity.this.nightAutomaticRow) {
                        i = LocaleController.getString("AutoNightAutomatic", C0446R.string.AutoNightAutomatic);
                        if (Theme.selectedAutoNightType != 2) {
                            z2 = false;
                        }
                        themeTypeCell.setValue(i, z2, false);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.scheduleHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightSchedule", C0446R.string.AutoNightSchedule));
                        return;
                    } else if (i == ThemeActivity.this.automaticHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightBrightness", C0446R.string.AutoNightBrightness));
                        return;
                    } else if (i == ThemeActivity.this.preferedHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightPreferred", C0446R.string.AutoNightPreferred));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    ((BrightnessControlCell) viewHolder.itemView).setProgress(Theme.autoNightBrighnessThreshold);
                    return;
                case 7:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.scheduleLocationRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("AutoNightLocation", C0446R.string.AutoNightLocation), Theme.autoNightScheduleByLocation, true);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 4) {
                ((ThemeTypeCell) viewHolder.itemView).setTypeChecked(viewHolder.getAdapterPosition() == Theme.selectedAutoNightType);
            } else if (itemViewType == 0) {
                ((ThemeCell) viewHolder.itemView).updateCurrentThemeCheck();
            }
            if (itemViewType != 2 && itemViewType != 3) {
                viewHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
        }

        public int getItemViewType(int i) {
            if (!(i == ThemeActivity.this.newThemeRow || i == ThemeActivity.this.nightThemeRow || i == ThemeActivity.this.scheduleFromRow || i == ThemeActivity.this.scheduleToRow)) {
                if (i != ThemeActivity.this.scheduleUpdateLocationRow) {
                    if (!(i == ThemeActivity.this.newThemeInfoRow || i == ThemeActivity.this.automaticBrightnessInfoRow)) {
                        if (i != ThemeActivity.this.scheduleLocationInfoRow) {
                            if (!(i == ThemeActivity.this.themeInfoRow || i == ThemeActivity.this.nightTypeInfoRow)) {
                                if (i != ThemeActivity.this.scheduleFromToInfoRow) {
                                    if (!(i == ThemeActivity.this.nightDisabledRow || i == ThemeActivity.this.nightScheduledRow)) {
                                        if (i != ThemeActivity.this.nightAutomaticRow) {
                                            if (!(i == ThemeActivity.this.scheduleHeaderRow || i == ThemeActivity.this.automaticHeaderRow)) {
                                                if (i != ThemeActivity.this.preferedHeaderRow) {
                                                    if (i == ThemeActivity.this.automaticBrightnessRow) {
                                                        return 6;
                                                    }
                                                    return i == ThemeActivity.this.scheduleLocationRow ? 7 : 0;
                                                }
                                            }
                                            return 5;
                                        }
                                    }
                                    return 4;
                                }
                            }
                            return 3;
                        }
                    }
                    return 2;
                }
            }
            return 1;
        }
    }

    public ThemeActivity(int i) {
        this.currentType = i;
        updateRows();
    }

    private void updateRows() {
        int i;
        int i2 = this.rowCount;
        this.rowCount = 0;
        this.scheduleLocationRow = -1;
        this.scheduleUpdateLocationRow = -1;
        this.scheduleLocationInfoRow = -1;
        this.nightDisabledRow = -1;
        this.nightScheduledRow = -1;
        this.nightAutomaticRow = -1;
        this.nightTypeInfoRow = -1;
        this.scheduleHeaderRow = -1;
        this.nightThemeRow = -1;
        this.newThemeRow = -1;
        this.newThemeInfoRow = -1;
        this.scheduleFromRow = -1;
        this.scheduleToRow = -1;
        this.scheduleFromToInfoRow = -1;
        this.themeStartRow = -1;
        this.themeEndRow = -1;
        this.themeInfoRow = -1;
        this.preferedHeaderRow = -1;
        this.automaticHeaderRow = -1;
        this.automaticBrightnessRow = -1;
        this.automaticBrightnessInfoRow = -1;
        int i3 = 2;
        if (this.currentType == 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightThemeRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.newThemeRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.newThemeInfoRow = i;
            this.themeStartRow = this.rowCount;
            this.rowCount += Theme.themes.size();
            this.themeEndRow = this.rowCount;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.themeInfoRow = i;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightDisabledRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightScheduledRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightAutomaticRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightTypeInfoRow = i;
            if (Theme.selectedAutoNightType == 1) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.scheduleHeaderRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.scheduleLocationRow = i;
                if (Theme.autoNightScheduleByLocation) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.scheduleUpdateLocationRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.scheduleLocationInfoRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.scheduleFromRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.scheduleToRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.scheduleFromToInfoRow = i;
                }
            } else if (Theme.selectedAutoNightType == 2) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.automaticHeaderRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.automaticBrightnessRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.automaticBrightnessInfoRow = i;
            }
            if (Theme.selectedAutoNightType != 0) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.preferedHeaderRow = i;
                this.themeStartRow = this.rowCount;
                this.rowCount += Theme.themes.size();
                this.themeEndRow = this.rowCount;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.themeInfoRow = i;
            }
        }
        if (this.listAdapter != null) {
            if (this.currentType != 0) {
                if (this.previousUpdatedType != -1) {
                    int i4 = this.nightTypeInfoRow + 1;
                    ListAdapter listAdapter;
                    if (this.previousUpdatedType != Theme.selectedAutoNightType) {
                        i = 0;
                        while (i < 3) {
                            Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(i);
                            if (holder != null) {
                                ((ThemeTypeCell) holder.itemView).setTypeChecked(i == Theme.selectedAutoNightType);
                            }
                            i++;
                        }
                        if (Theme.selectedAutoNightType == 0) {
                            this.listAdapter.notifyItemRangeRemoved(i4, i2 - i4);
                        } else {
                            int i5 = 5;
                            if (Theme.selectedAutoNightType == 1) {
                                if (this.previousUpdatedType == 0) {
                                    this.listAdapter.notifyItemRangeInserted(i4, this.rowCount - i4);
                                } else if (this.previousUpdatedType == 2) {
                                    this.listAdapter.notifyItemRangeRemoved(i4, 3);
                                    listAdapter = this.listAdapter;
                                    if (Theme.autoNightScheduleByLocation) {
                                        i5 = 4;
                                    }
                                    listAdapter.notifyItemRangeInserted(i4, i5);
                                }
                            } else if (Theme.selectedAutoNightType == 2) {
                                if (this.previousUpdatedType == 0) {
                                    this.listAdapter.notifyItemRangeInserted(i4, this.rowCount - i4);
                                } else if (this.previousUpdatedType == 1) {
                                    listAdapter = this.listAdapter;
                                    if (Theme.autoNightScheduleByLocation) {
                                        i5 = 4;
                                    }
                                    listAdapter.notifyItemRangeRemoved(i4, i5);
                                    this.listAdapter.notifyItemRangeInserted(i4, 3);
                                }
                            }
                        }
                    } else if (this.previousByLocation != Theme.autoNightScheduleByLocation) {
                        i4 += 2;
                        this.listAdapter.notifyItemRangeRemoved(i4, Theme.autoNightScheduleByLocation ? 3 : 2);
                        listAdapter = this.listAdapter;
                        if (!Theme.autoNightScheduleByLocation) {
                            i3 = 3;
                        }
                        listAdapter.notifyItemRangeInserted(i4, i3);
                    }
                }
            }
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.currentType == 1) {
            this.previousByLocation = Theme.autoNightScheduleByLocation;
            this.previousUpdatedType = Theme.selectedAutoNightType;
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        stopLocationUpdate();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        Theme.saveAutoNightThemeConfig();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.locationPermissionGranted) {
            updateSunTime(0, 1);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("Theme", C0446R.string.Theme));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", C0446R.string.AutoNightTheme));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C22961());
        this.listAdapter = new ListAdapter(context);
        View frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView = frameLayout;
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C22972());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void updateSunTime(Location location, boolean z) {
        Throwable e;
        Holder holder;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        if (VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (!(parentActivity == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                return;
            }
        }
        if (getParentActivity() != null) {
            if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                try {
                    if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                        builder.setMessage(LocaleController.getString("GpsDisabledAlert", C0446R.string.GpsDisabledAlert));
                        builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", C0446R.string.ConnectingToProxyEnable), new C17183());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                        showDialog(builder.create());
                        return;
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            } else {
                return;
            }
        }
        Location lastKnownLocation;
        try {
            lastKnownLocation = locationManager.getLastKnownLocation("gps");
            if (lastKnownLocation == null) {
                try {
                    location = locationManager.getLastKnownLocation("network");
                } catch (Exception e3) {
                    e = e3;
                    FileLog.m3e(e);
                    location = lastKnownLocation;
                    startLocationUpdate();
                    if (location == null) {
                        return;
                    }
                    Theme.autoNightLocationLatitude = location.getLatitude();
                    Theme.autoNightLocationLongitude = location.getLongitude();
                    location = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
                    Theme.autoNightSunriseTime = location[0];
                    Theme.autoNightSunsetTime = location[1];
                    Theme.autoNightCityName = null;
                    location = Calendar.getInstance();
                    location.setTimeInMillis(System.currentTimeMillis());
                    Theme.autoNightLastSunCheckDay = location.get(true);
                    Utilities.globalQueue.postRunnable(new C17204());
                    holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
                    ((TextInfoPrivacyCell) holder.itemView).setText(getLocationSunString());
                    Theme.checkAutoNightThemeConditions();
                }
            }
            if (lastKnownLocation == null) {
                location = locationManager.getLastKnownLocation("passive");
            }
            location = lastKnownLocation;
        } catch (Throwable e4) {
            lastKnownLocation = location;
            e = e4;
            FileLog.m3e(e);
            location = lastKnownLocation;
            startLocationUpdate();
            if (location == null) {
                return;
            }
            Theme.autoNightLocationLatitude = location.getLatitude();
            Theme.autoNightLocationLongitude = location.getLongitude();
            location = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
            Theme.autoNightSunriseTime = location[0];
            Theme.autoNightSunsetTime = location[1];
            Theme.autoNightCityName = null;
            location = Calendar.getInstance();
            location.setTimeInMillis(System.currentTimeMillis());
            Theme.autoNightLastSunCheckDay = location.get(true);
            Utilities.globalQueue.postRunnable(new C17204());
            holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
            ((TextInfoPrivacyCell) holder.itemView).setText(getLocationSunString());
            Theme.checkAutoNightThemeConditions();
        }
        if (location == null || z) {
            startLocationUpdate();
            if (location == null) {
                return;
            }
        }
        Theme.autoNightLocationLatitude = location.getLatitude();
        Theme.autoNightLocationLongitude = location.getLongitude();
        location = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
        Theme.autoNightSunriseTime = location[0];
        Theme.autoNightSunsetTime = location[1];
        Theme.autoNightCityName = null;
        location = Calendar.getInstance();
        location.setTimeInMillis(System.currentTimeMillis());
        Theme.autoNightLastSunCheckDay = location.get(true);
        Utilities.globalQueue.postRunnable(new C17204());
        holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
        if (holder != null && (holder.itemView instanceof TextInfoPrivacyCell)) {
            ((TextInfoPrivacyCell) holder.itemView).setText(getLocationSunString());
        }
        if (Theme.autoNightScheduleByLocation != null && Theme.selectedAutoNightType == 1) {
            Theme.checkAutoNightThemeConditions();
        }
    }

    private void startLocationUpdate() {
        if (!this.updatingLocation) {
            this.updatingLocation = true;
            LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            try {
                locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            try {
                locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }
    }

    private void stopLocationUpdate() {
        this.updatingLocation = false;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        locationManager.removeUpdates(this.gpsLocationListener);
        locationManager.removeUpdates(this.networkLocationListener);
    }

    private void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            if (z) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", C0446R.string.PermissionNoLocationPosition));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", C0446R.string.PermissionNoLocation));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", C0446R.string.PermissionOpenSettings), new C17215());
            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
            showDialog(builder.create());
        }
    }

    private String getLocationSunString() {
        int i = Theme.autoNightSunriseTime - ((Theme.autoNightSunriseTime / 60) * 60);
        String format = String.format("%02d:%02d", new Object[]{Integer.valueOf(Theme.autoNightSunriseTime / 60), Integer.valueOf(i)});
        int i2 = Theme.autoNightSunsetTime - ((Theme.autoNightSunsetTime / 60) * 60);
        Object[] objArr = new Object[]{Integer.valueOf(Theme.autoNightSunsetTime / 60), Integer.valueOf(i2)};
        return LocaleController.formatString("AutoNightUpdateLocationInfo", C0446R.string.AutoNightUpdateLocationInfo, String.format("%02d:%02d", objArr), format);
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[28];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, ThemeCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[9] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[10] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon);
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"optionsButton"}, null, null, null, Theme.key_stickers_menu);
        r1[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[16] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        r1[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r1[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        r1[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r1[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, null, null, null, Theme.key_profile_actionIcon);
        r1[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, null, null, null, Theme.key_profile_actionIcon);
        r1[24] = new ThemeDescription(this.listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, Theme.key_player_progressBackground);
        r1[25] = new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, Theme.key_player_progress);
        r1[26] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[27] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon);
        return r1;
    }
}
