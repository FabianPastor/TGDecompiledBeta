package org.telegram.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.time.SunDate;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
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
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ThemeEditorView;

public class ThemeActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int THEME_TYPE_BASIC = 0;
    public static final int THEME_TYPE_NIGHT = 1;
    private int automaticBrightnessInfoRow;
    private int automaticBrightnessRow;
    private int automaticHeaderRow;
    private int currentType;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener(this, null);
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private GpsLocationListener networkLocationListener = new GpsLocationListener(this, null);
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

    private class GpsLocationListener implements LocationListener {
        private GpsLocationListener() {
        }

        /* synthetic */ GpsLocationListener(ThemeActivity x0, AnonymousClass1 x1) {
            this();
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                ThemeActivity.this.stopLocationUpdate();
                ThemeActivity.this.updateSunTime(location, false);
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ThemeActivity.this.rowCount;
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 0 || type == 1 || type == 4 || type == 7) {
                return true;
            }
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            boolean z = true;
            switch (viewType) {
                case 0:
                    Context context = this.mContext;
                    if (ThemeActivity.this.currentType != 1) {
                        z = false;
                    }
                    view = new ThemeCell(context, z);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    if (ThemeActivity.this.currentType == 0) {
                        ((ThemeCell) view).setOnOptionsClick(new ThemeActivity$ListAdapter$$Lambda$0(this));
                        break;
                    }
                    break;
                case 1:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, "windowBackgroundGrayShadow"));
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    break;
                case 4:
                    view = new ThemeTypeCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 5:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 6:
                    view = new BrightnessControlCell(this.mContext) {
                        protected void didChangedValue(float value) {
                            int oldValue = (int) (Theme.autoNightBrighnessThreshold * 100.0f);
                            int newValue = (int) (value * 100.0f);
                            Theme.autoNightBrighnessThreshold = value;
                            if (oldValue != newValue) {
                                Holder holder = (Holder) ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.automaticBrightnessInfoRow);
                                if (holder != null) {
                                    holder.itemView.setText(LocaleController.formatString("AutoNightBrightnessInfo", R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                                }
                                Theme.checkAutoNightThemeConditions(true);
                            }
                        }
                    };
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            return new Holder(view);
        }

        final /* synthetic */ void lambda$onCreateViewHolder$2$ThemeActivity$ListAdapter(View v) {
            ThemeInfo themeInfo = ((ThemeCell) v.getParent()).getCurrentThemeInfo();
            if (ThemeActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(ThemeActivity.this.getParentActivity());
                builder.setItems(themeInfo.pathToFile == null ? new CharSequence[]{LocaleController.getString("ShareFile", R.string.ShareFile)} : new CharSequence[]{LocaleController.getString("ShareFile", R.string.ShareFile), LocaleController.getString("Edit", R.string.Edit), LocaleController.getString("Delete", R.string.Delete)}, new ThemeActivity$ListAdapter$$Lambda$1(this, themeInfo));
                ThemeActivity.this.showDialog(builder.create());
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:43:0x00b3 A:{SYNTHETIC, Splitter: B:43:0x00b3} */
        /* JADX WARNING: Removed duplicated region for block: B:70:? A:{SYNTHETIC, RETURN, ORIG_RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x0097 A:{SYNTHETIC, Splitter: B:34:0x0097} */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x008b A:{SYNTHETIC, Splitter: B:28:0x008b} */
        /* JADX WARNING: Removed duplicated region for block: B:70:? A:{SYNTHETIC, RETURN, ORIG_RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00b3 A:{SYNTHETIC, Splitter: B:43:0x00b3} */
        final /* synthetic */ void lambda$null$1$ThemeActivity$ListAdapter(org.telegram.ui.ActionBar.Theme.ThemeInfo r15, android.content.DialogInterface r16, int r17) {
            /*
            r14 = this;
            if (r17 != 0) goto L_0x0110;
        L_0x0002:
            r11 = r15.pathToFile;
            if (r11 != 0) goto L_0x00a0;
        L_0x0006:
            r11 = r15.assetName;
            if (r11 != 0) goto L_0x00a0;
        L_0x000a:
            r8 = new java.lang.StringBuilder;
            r8.<init>();
            r11 = org.telegram.ui.ActionBar.Theme.getDefaultColors();
            r11 = r11.entrySet();
            r12 = r11.iterator();
        L_0x001b:
            r11 = r12.hasNext();
            if (r11 == 0) goto L_0x0047;
        L_0x0021:
            r4 = r12.next();
            r4 = (java.util.Map.Entry) r4;
            r11 = r4.getKey();
            r11 = (java.lang.String) r11;
            r11 = r8.append(r11);
            r13 = "=";
            r11 = r11.append(r13);
            r13 = r4.getValue();
            r11 = r11.append(r13);
            r13 = "\n";
            r11.append(r13);
            goto L_0x001b;
        L_0x0047:
            r2 = new java.io.File;
            r11 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
            r12 = "default_theme.attheme";
            r2.<init>(r11, r12);
            r9 = 0;
            r10 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0085 }
            r10.<init>(r2);	 Catch:{ Exception -> 0x0085 }
            r11 = r8.toString();	 Catch:{ Exception -> 0x0199, all -> 0x0195 }
            r11 = org.telegram.messenger.AndroidUtilities.getStringBytes(r11);	 Catch:{ Exception -> 0x0199, all -> 0x0195 }
            r10.write(r11);	 Catch:{ Exception -> 0x0199, all -> 0x0195 }
            if (r10 == 0) goto L_0x0069;
        L_0x0066:
            r10.close();	 Catch:{ Exception -> 0x007f }
        L_0x0069:
            r9 = r10;
        L_0x006a:
            r5 = new java.io.File;
            r11 = 4;
            r11 = org.telegram.messenger.FileLoader.getDirectory(r11);
            r12 = r2.getName();
            r5.<init>(r11, r12);
            r11 = org.telegram.messenger.AndroidUtilities.copyFile(r2, r5);	 Catch:{ Exception -> 0x00f4 }
            if (r11 != 0) goto L_0x00b3;
        L_0x007e:
            return;
        L_0x007f:
            r3 = move-exception;
            org.telegram.messenger.FileLog.e(r3);
            r9 = r10;
            goto L_0x006a;
        L_0x0085:
            r3 = move-exception;
        L_0x0086:
            org.telegram.messenger.FileLog.e(r3);	 Catch:{ all -> 0x0094 }
            if (r9 == 0) goto L_0x006a;
        L_0x008b:
            r9.close();	 Catch:{ Exception -> 0x008f }
            goto L_0x006a;
        L_0x008f:
            r3 = move-exception;
            org.telegram.messenger.FileLog.e(r3);
            goto L_0x006a;
        L_0x0094:
            r11 = move-exception;
        L_0x0095:
            if (r9 == 0) goto L_0x009a;
        L_0x0097:
            r9.close();	 Catch:{ Exception -> 0x009b }
        L_0x009a:
            throw r11;
        L_0x009b:
            r3 = move-exception;
            org.telegram.messenger.FileLog.e(r3);
            goto L_0x009a;
        L_0x00a0:
            r11 = r15.assetName;
            if (r11 == 0) goto L_0x00ab;
        L_0x00a4:
            r11 = r15.assetName;
            r2 = org.telegram.ui.ActionBar.Theme.getAssetFile(r11);
            goto L_0x006a;
        L_0x00ab:
            r2 = new java.io.File;
            r11 = r15.pathToFile;
            r2.<init>(r11);
            goto L_0x006a;
        L_0x00b3:
            r7 = new android.content.Intent;	 Catch:{ Exception -> 0x00f4 }
            r11 = "android.intent.action.SEND";
            r7.<init>(r11);	 Catch:{ Exception -> 0x00f4 }
            r11 = "text/xml";
            r7.setType(r11);	 Catch:{ Exception -> 0x00f4 }
            r11 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00f4 }
            r12 = 24;
            if (r11 < r12) goto L_0x0105;
        L_0x00c7:
            r11 = "android.intent.extra.STREAM";
            r12 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x00f9 }
            r12 = r12.getParentActivity();	 Catch:{ Exception -> 0x00f9 }
            r13 = "org.telegram.messenger.beta.provider";
            r12 = android.support.v4.content.FileProvider.getUriForFile(r12, r13, r5);	 Catch:{ Exception -> 0x00f9 }
            r7.putExtra(r11, r12);	 Catch:{ Exception -> 0x00f9 }
            r11 = 1;
            r7.setFlags(r11);	 Catch:{ Exception -> 0x00f9 }
        L_0x00de:
            r11 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x00f4 }
            r12 = "ShareFile";
            r13 = NUM; // 0x7f0CLASSNAMEbc float:1.8613208E38 double:1.0530983767E-314;
            r12 = org.telegram.messenger.LocaleController.getString(r12, r13);	 Catch:{ Exception -> 0x00f4 }
            r12 = android.content.Intent.createChooser(r7, r12);	 Catch:{ Exception -> 0x00f4 }
            r13 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
            r11.startActivityForResult(r12, r13);	 Catch:{ Exception -> 0x00f4 }
            goto L_0x007e;
        L_0x00f4:
            r3 = move-exception;
            org.telegram.messenger.FileLog.e(r3);
            goto L_0x007e;
        L_0x00f9:
            r6 = move-exception;
            r11 = "android.intent.extra.STREAM";
            r12 = android.net.Uri.fromFile(r5);	 Catch:{ Exception -> 0x00f4 }
            r7.putExtra(r11, r12);	 Catch:{ Exception -> 0x00f4 }
            goto L_0x00de;
        L_0x0105:
            r11 = "android.intent.extra.STREAM";
            r12 = android.net.Uri.fromFile(r5);	 Catch:{ Exception -> 0x00f4 }
            r7.putExtra(r11, r12);	 Catch:{ Exception -> 0x00f4 }
            goto L_0x00de;
        L_0x0110:
            r11 = 1;
            r0 = r17;
            if (r0 != r11) goto L_0x013d;
        L_0x0115:
            r11 = org.telegram.ui.ThemeActivity.this;
            r11 = r11.parentLayout;
            if (r11 == 0) goto L_0x007e;
        L_0x011d:
            org.telegram.ui.ActionBar.Theme.applyTheme(r15);
            r11 = org.telegram.ui.ThemeActivity.this;
            r11 = r11.parentLayout;
            r12 = 1;
            r13 = 1;
            r11.rebuildAllFragmentViews(r12, r13);
            r11 = new org.telegram.ui.Components.ThemeEditorView;
            r11.<init>();
            r12 = org.telegram.ui.ThemeActivity.this;
            r12 = r12.getParentActivity();
            r13 = r15.name;
            r11.show(r12, r13);
            goto L_0x007e;
        L_0x013d:
            r11 = org.telegram.ui.ThemeActivity.this;
            r11 = r11.getParentActivity();
            if (r11 == 0) goto L_0x007e;
        L_0x0145:
            r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
            r11 = org.telegram.ui.ThemeActivity.this;
            r11 = r11.getParentActivity();
            r1.<init>(r11);
            r11 = "DeleteThemeAlert";
            r12 = NUM; // 0x7f0CLASSNAMEa float:1.8610445E38 double:1.053097704E-314;
            r11 = org.telegram.messenger.LocaleController.getString(r11, r12);
            r1.setMessage(r11);
            r11 = "AppName";
            r12 = NUM; // 0x7f0CLASSNAME float:1.8609484E38 double:1.0530974696E-314;
            r11 = org.telegram.messenger.LocaleController.getString(r11, r12);
            r1.setTitle(r11);
            r11 = "Delete";
            r12 = NUM; // 0x7f0CLASSNAME float:1.8610403E38 double:1.0530976934E-314;
            r11 = org.telegram.messenger.LocaleController.getString(r11, r12);
            r12 = new org.telegram.ui.ThemeActivity$ListAdapter$$Lambda$2;
            r12.<init>(r14, r15);
            r1.setPositiveButton(r11, r12);
            r11 = "Cancel";
            r12 = NUM; // 0x7f0CLASSNAME float:1.8609825E38 double:1.0530975526E-314;
            r11 = org.telegram.messenger.LocaleController.getString(r11, r12);
            r12 = 0;
            r1.setNegativeButton(r11, r12);
            r11 = org.telegram.ui.ThemeActivity.this;
            r12 = r1.create();
            r11.showDialog(r12);
            goto L_0x007e;
        L_0x0195:
            r11 = move-exception;
            r9 = r10;
            goto L_0x0095;
        L_0x0199:
            r3 = move-exception;
            r9 = r10;
            goto L_0x0086;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.ListAdapter.lambda$null$1$ThemeActivity$ListAdapter(org.telegram.ui.ActionBar.Theme$ThemeInfo, android.content.DialogInterface, int):void");
        }

        final /* synthetic */ void lambda$null$0$ThemeActivity$ListAdapter(ThemeInfo themeInfo, DialogInterface dialogInterface, int i) {
            if (Theme.deleteTheme(themeInfo)) {
                ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
            }
            ThemeActivity.this.updateRows();
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    position -= ThemeActivity.this.themeStartRow;
                    ThemeInfo themeInfo = (ThemeInfo) Theme.themes.get(position);
                    ThemeCell themeCell = (ThemeCell) holder.itemView;
                    if (position == Theme.themes.size() - 1) {
                        z2 = false;
                    }
                    themeCell.setTheme(themeInfo, z2);
                    return;
                case 1:
                    TextSettingsCell cell = holder.itemView;
                    int currentMinute;
                    if (position == ThemeActivity.this.newThemeRow) {
                        cell.setText(LocaleController.getString("CreateNewTheme", R.string.CreateNewTheme), false);
                        return;
                    } else if (position == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType == 0 || Theme.getCurrentNightTheme() == null) {
                            cell.setText(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), true);
                            return;
                        } else {
                            cell.setTextAndValue(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), Theme.getCurrentNightThemeName(), true);
                            return;
                        }
                    } else if (position == ThemeActivity.this.scheduleFromRow) {
                        currentMinute = Theme.autoNightDayStartTime - ((Theme.autoNightDayStartTime / 60) * 60);
                        cell.setTextAndValue(LocaleController.getString("AutoNightFrom", R.string.AutoNightFrom), String.format("%02d:%02d", new Object[]{Integer.valueOf(currentHour), Integer.valueOf(currentMinute)}), true);
                        return;
                    } else if (position == ThemeActivity.this.scheduleToRow) {
                        currentMinute = Theme.autoNightDayEndTime - ((Theme.autoNightDayEndTime / 60) * 60);
                        cell.setTextAndValue(LocaleController.getString("AutoNightTo", R.string.AutoNightTo), String.format("%02d:%02d", new Object[]{Integer.valueOf(currentHour), Integer.valueOf(currentMinute)}), false);
                        return;
                    } else if (position == ThemeActivity.this.scheduleUpdateLocationRow) {
                        cell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell2 = holder.itemView;
                    if (position == ThemeActivity.this.newThemeInfoRow) {
                        cell2.setText(LocaleController.getString("CreateNewThemeInfo", R.string.CreateNewThemeInfo));
                        return;
                    } else if (position == ThemeActivity.this.automaticBrightnessInfoRow) {
                        cell2.setText(LocaleController.formatString("AutoNightBrightnessInfo", R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (100.0f * Theme.autoNightBrighnessThreshold))));
                        return;
                    } else if (position == ThemeActivity.this.scheduleLocationInfoRow) {
                        cell2.setText(ThemeActivity.this.getLocationSunString());
                        return;
                    } else {
                        return;
                    }
                case 4:
                    ThemeTypeCell typeCell = holder.itemView;
                    String string;
                    if (position == ThemeActivity.this.nightDisabledRow) {
                        string = LocaleController.getString("AutoNightDisabled", R.string.AutoNightDisabled);
                        if (Theme.selectedAutoNightType == 0) {
                            z = true;
                        }
                        typeCell.setValue(string, z, true);
                        return;
                    } else if (position == ThemeActivity.this.nightScheduledRow) {
                        string = LocaleController.getString("AutoNightScheduled", R.string.AutoNightScheduled);
                        if (Theme.selectedAutoNightType == 1) {
                            z = true;
                        }
                        typeCell.setValue(string, z, true);
                        return;
                    } else if (position == ThemeActivity.this.nightAutomaticRow) {
                        string = LocaleController.getString("AutoNightAutomatic", R.string.AutoNightAutomatic);
                        if (Theme.selectedAutoNightType != 2) {
                            z2 = false;
                        }
                        typeCell.setValue(string, z2, false);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    HeaderCell headerCell = holder.itemView;
                    if (position == ThemeActivity.this.scheduleHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightSchedule", R.string.AutoNightSchedule));
                        return;
                    } else if (position == ThemeActivity.this.automaticHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightBrightness", R.string.AutoNightBrightness));
                        return;
                    } else if (position == ThemeActivity.this.preferedHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightPreferred", R.string.AutoNightPreferred));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    holder.itemView.setProgress(Theme.autoNightBrighnessThreshold);
                    return;
                case 7:
                    TextCheckCell textCheckCell = holder.itemView;
                    if (position == ThemeActivity.this.scheduleLocationRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("AutoNightLocation", R.string.AutoNightLocation), Theme.autoNightScheduleByLocation, true);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 4) {
                ((ThemeTypeCell) holder.itemView).setTypeChecked(holder.getAdapterPosition() == Theme.selectedAutoNightType);
            } else if (type == 0) {
                ((ThemeCell) holder.itemView).updateCurrentThemeCheck();
            }
            if (type != 2 && type != 3) {
                holder.itemView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
        }

        public int getItemViewType(int i) {
            if (i == ThemeActivity.this.newThemeRow || i == ThemeActivity.this.nightThemeRow || i == ThemeActivity.this.scheduleFromRow || i == ThemeActivity.this.scheduleToRow || i == ThemeActivity.this.scheduleUpdateLocationRow) {
                return 1;
            }
            if (i == ThemeActivity.this.newThemeInfoRow || i == ThemeActivity.this.automaticBrightnessInfoRow || i == ThemeActivity.this.scheduleLocationInfoRow) {
                return 2;
            }
            if (i == ThemeActivity.this.themeInfoRow || i == ThemeActivity.this.nightTypeInfoRow || i == ThemeActivity.this.scheduleFromToInfoRow) {
                return 3;
            }
            if (i == ThemeActivity.this.nightDisabledRow || i == ThemeActivity.this.nightScheduledRow || i == ThemeActivity.this.nightAutomaticRow) {
                return 4;
            }
            if (i == ThemeActivity.this.scheduleHeaderRow || i == ThemeActivity.this.automaticHeaderRow || i == ThemeActivity.this.preferedHeaderRow) {
                return 5;
            }
            if (i == ThemeActivity.this.automaticBrightnessRow) {
                return 6;
            }
            if (i == ThemeActivity.this.scheduleLocationRow) {
                return 7;
            }
            return 0;
        }
    }

    public ThemeActivity(int type) {
        this.currentType = type;
        updateRows();
    }

    private void updateRows() {
        int i;
        int i2 = 2;
        int oldRowCount = this.rowCount;
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
            if (this.currentType == 0 || this.previousUpdatedType == -1) {
                this.listAdapter.notifyDataSetChanged();
            } else {
                int start = this.nightTypeInfoRow + 1;
                if (this.previousUpdatedType != Theme.selectedAutoNightType) {
                    int a = 0;
                    while (a < 3) {
                        Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(a);
                        if (holder != null) {
                            ((ThemeTypeCell) holder.itemView).setTypeChecked(a == Theme.selectedAutoNightType);
                        }
                        a++;
                    }
                    if (Theme.selectedAutoNightType == 0) {
                        this.listAdapter.notifyItemRangeRemoved(start, oldRowCount - start);
                    } else if (Theme.selectedAutoNightType == 1) {
                        if (this.previousUpdatedType == 0) {
                            this.listAdapter.notifyItemRangeInserted(start, this.rowCount - start);
                        } else if (this.previousUpdatedType == 2) {
                            this.listAdapter.notifyItemRangeRemoved(start, 3);
                            this.listAdapter.notifyItemRangeInserted(start, Theme.autoNightScheduleByLocation ? 4 : 5);
                        }
                    } else if (Theme.selectedAutoNightType == 2) {
                        if (this.previousUpdatedType == 0) {
                            this.listAdapter.notifyItemRangeInserted(start, this.rowCount - start);
                        } else if (this.previousUpdatedType == 1) {
                            this.listAdapter.notifyItemRangeRemoved(start, Theme.autoNightScheduleByLocation ? 4 : 5);
                            this.listAdapter.notifyItemRangeInserted(start, 3);
                        }
                    }
                } else if (this.previousByLocation != Theme.autoNightScheduleByLocation) {
                    ListAdapter listAdapter = this.listAdapter;
                    int i3 = start + 2;
                    if (Theme.autoNightScheduleByLocation) {
                        i = 3;
                    } else {
                        i = 2;
                    }
                    listAdapter.notifyItemRangeRemoved(i3, i);
                    ListAdapter listAdapter2 = this.listAdapter;
                    int i4 = start + 2;
                    if (!Theme.autoNightScheduleByLocation) {
                        i2 = 3;
                    }
                    listAdapter2.notifyItemRangeInserted(i4, i2);
                }
            }
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.locationPermissionGranted) {
            updateSunTime(null, true);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("Theme", R.string.Theme));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ThemeActivity.this.lambda$checkDiscard$70$PassportActivity();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView = frameLayout;
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new ThemeActivity$$Lambda$0(this));
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$6$ThemeActivity(View view, int position) {
        if (position == this.newThemeRow) {
            if (getParentActivity() != null) {
                View editTextBoldCursor = new EditTextBoldCursor(getParentActivity());
                editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("NewTheme", R.string.NewTheme));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), ThemeActivity$$Lambda$5.$instance);
                editTextBoldCursor = new LinearLayout(getParentActivity());
                editTextBoldCursor.setOrientation(1);
                builder.setView(editTextBoldCursor);
                editTextBoldCursor = new TextView(getParentActivity());
                editTextBoldCursor.setText(LocaleController.formatString("EnterThemeName", R.string.EnterThemeName, new Object[0]));
                editTextBoldCursor.setTextSize(16.0f);
                editTextBoldCursor.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
                editTextBoldCursor.setTextColor(Theme.getColor("dialogTextBlack"));
                editTextBoldCursor.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, -2));
                editTextBoldCursor.setTextSize(1, 16.0f);
                editTextBoldCursor.setTextColor(Theme.getColor("dialogTextBlack"));
                editTextBoldCursor.setMaxLines(1);
                editTextBoldCursor.setLines(1);
                editTextBoldCursor.setInputType(16385);
                editTextBoldCursor.setGravity(51);
                editTextBoldCursor.setSingleLine(true);
                editTextBoldCursor.setImeOptions(6);
                editTextBoldCursor.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
                editTextBoldCursor.setCursorWidth(1.5f);
                editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                editTextBoldCursor.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
                editTextBoldCursor.setOnEditorActionListener(ThemeActivity$$Lambda$6.$instance);
                AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new ThemeActivity$$Lambda$7(editTextBoldCursor));
                showDialog(alertDialog);
                alertDialog.getButton(-1).setOnClickListener(new ThemeActivity$$Lambda$8(this, editTextBoldCursor, alertDialog));
            }
        } else if (position >= this.themeStartRow && position < this.themeEndRow) {
            int p = position - this.themeStartRow;
            if (p >= 0 && p < Theme.themes.size()) {
                ThemeInfo themeInfo = (ThemeInfo) Theme.themes.get(p);
                if (this.currentType == 0) {
                    Theme.applyTheme(themeInfo);
                    if (this.parentLayout != null) {
                        this.parentLayout.rebuildAllFragmentViews(false, false);
                    }
                    lambda$checkDiscard$70$PassportActivity();
                    return;
                }
                Theme.setCurrentNightTheme(themeInfo);
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.listView.getChildAt(a);
                    if (child instanceof ThemeCell) {
                        ((ThemeCell) child).updateCurrentThemeCheck();
                    }
                }
            }
        } else if (position == this.nightThemeRow) {
            presentFragment(new ThemeActivity(1));
        } else if (position == this.nightDisabledRow) {
            Theme.selectedAutoNightType = 0;
            updateRows();
            Theme.checkAutoNightThemeConditions();
        } else if (position == this.nightScheduledRow) {
            Theme.selectedAutoNightType = 1;
            if (Theme.autoNightScheduleByLocation) {
                updateSunTime(null, true);
            }
            updateRows();
            Theme.checkAutoNightThemeConditions();
        } else if (position == this.nightAutomaticRow) {
            Theme.selectedAutoNightType = 2;
            updateRows();
            Theme.checkAutoNightThemeConditions();
        } else if (position == this.scheduleLocationRow) {
            Theme.autoNightScheduleByLocation = !Theme.autoNightScheduleByLocation;
            ((TextCheckCell) view).setChecked(Theme.autoNightScheduleByLocation);
            updateRows();
            if (Theme.autoNightScheduleByLocation) {
                updateSunTime(null, true);
            }
            Theme.checkAutoNightThemeConditions();
        } else if (position == this.scheduleFromRow || position == this.scheduleToRow) {
            if (getParentActivity() != null) {
                int currentHour;
                int currentMinute;
                if (position == this.scheduleFromRow) {
                    currentHour = Theme.autoNightDayStartTime / 60;
                    currentMinute = Theme.autoNightDayStartTime - (currentHour * 60);
                } else {
                    currentHour = Theme.autoNightDayEndTime / 60;
                    currentMinute = Theme.autoNightDayEndTime - (currentHour * 60);
                }
                showDialog(new TimePickerDialog(getParentActivity(), new ThemeActivity$$Lambda$9(this, position, (TextSettingsCell) view), currentHour, currentMinute, true));
            }
        } else if (position == this.scheduleUpdateLocationRow) {
            updateSunTime(null, true);
        }
    }

    static final /* synthetic */ void lambda$null$0$ThemeActivity(DialogInterface dialog, int which) {
    }

    static final /* synthetic */ void lambda$null$2$ThemeActivity(EditTextBoldCursor editText) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    final /* synthetic */ void lambda$null$4$ThemeActivity(EditTextBoldCursor editText, AlertDialog alertDialog, View v) {
        if (editText.length() == 0) {
            Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(editText, 2.0f, 0);
            return;
        }
        ThemeEditorView themeEditorView = new ThemeEditorView();
        String name = editText.getText().toString() + ".attheme";
        themeEditorView.show(getParentActivity(), name);
        Theme.saveCurrentTheme(name, true);
        updateRows();
        alertDialog.dismiss();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (!preferences.getBoolean("themehint", false)) {
            preferences.edit().putBoolean("themehint", true).commit();
            try {
                Toast.makeText(getParentActivity(), LocaleController.getString("CreateNewThemeHelp", R.string.CreateNewThemeHelp), 1).show();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    final /* synthetic */ void lambda$null$5$ThemeActivity(int position, TextSettingsCell cell, TimePicker view1, int hourOfDay, int minute) {
        int time = (hourOfDay * 60) + minute;
        if (position == this.scheduleFromRow) {
            Theme.autoNightDayStartTime = time;
            cell.setTextAndValue(LocaleController.getString("AutoNightFrom", R.string.AutoNightFrom), String.format("%02d:%02d", new Object[]{Integer.valueOf(hourOfDay), Integer.valueOf(minute)}), true);
            return;
        }
        Theme.autoNightDayEndTime = time;
        cell.setTextAndValue(LocaleController.getString("AutoNightTo", R.string.AutoNightTo), String.format("%02d:%02d", new Object[]{Integer.valueOf(hourOfDay), Integer.valueOf(minute)}), true);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void updateSunTime(Location lastKnownLocation, boolean forceUpdate) {
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        if (VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (!(activity == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0)) {
                activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                return;
            }
        }
        if (getParentActivity() != null) {
            if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                try {
                    if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("GpsDisabledAlert", R.string.GpsDisabledAlert));
                        builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new ThemeActivity$$Lambda$1(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        showDialog(builder.create());
                        return;
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            } else {
                return;
            }
        }
        try {
            lastKnownLocation = locationManager.getLastKnownLocation("gps");
            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation("network");
            } else if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation("passive");
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        if (lastKnownLocation == null || forceUpdate) {
            startLocationUpdate();
            if (lastKnownLocation == null) {
                return;
            }
        }
        Theme.autoNightLocationLatitude = lastKnownLocation.getLatitude();
        Theme.autoNightLocationLongitude = lastKnownLocation.getLongitude();
        int[] time = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
        Theme.autoNightSunriseTime = time[0];
        Theme.autoNightSunsetTime = time[1];
        Theme.autoNightCityName = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Theme.autoNightLastSunCheckDay = calendar.get(5);
        Utilities.globalQueue.postRunnable(new ThemeActivity$$Lambda$2(this));
        Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
        if (holder != null && (holder.itemView instanceof TextInfoPrivacyCell)) {
            ((TextInfoPrivacyCell) holder.itemView).setText(getLocationSunString());
        }
        if (Theme.autoNightScheduleByLocation && Theme.selectedAutoNightType == 1) {
            Theme.checkAutoNightThemeConditions();
        }
    }

    final /* synthetic */ void lambda$updateSunTime$7$ThemeActivity(DialogInterface dialog, int id) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception e) {
            }
        }
    }

    final /* synthetic */ void lambda$updateSunTime$9$ThemeActivity() {
        String name;
        try {
            List<Address> addresses = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault()).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (addresses.size() > 0) {
                name = ((Address) addresses.get(0)).getLocality();
            } else {
                name = null;
            }
        } catch (Exception e) {
            name = null;
        }
        AndroidUtilities.runOnUIThread(new ThemeActivity$$Lambda$4(this, name));
    }

    final /* synthetic */ void lambda$null$8$ThemeActivity(String nameFinal) {
        Theme.autoNightCityName = nameFinal;
        if (Theme.autoNightCityName == null) {
            Theme.autoNightCityName = String.format("(%.06f, %.06f)", new Object[]{Double.valueOf(Theme.autoNightLocationLatitude), Double.valueOf(Theme.autoNightLocationLongitude)});
        }
        Theme.saveAutoNightThemeConfig();
        if (this.listView != null) {
            Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleUpdateLocationRow);
            if (holder != null && (holder.itemView instanceof TextSettingsCell)) {
                ((TextSettingsCell) holder.itemView).setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
            }
        }
    }

    private void startLocationUpdate() {
        if (!this.updatingLocation) {
            this.updatingLocation = true;
            LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            try {
                locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            try {
                locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
    }

    private void stopLocationUpdate() {
        this.updatingLocation = false;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        locationManager.removeUpdates(this.gpsLocationListener);
        locationManager.removeUpdates(this.networkLocationListener);
    }

    private void showPermissionAlert(boolean byButton) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (byButton) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", R.string.PermissionNoLocationPosition));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", R.string.PermissionNoLocation));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new ThemeActivity$$Lambda$3(this));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$showPermissionAlert$10$ThemeActivity(DialogInterface dialog, int which) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                getParentActivity().startActivity(intent);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    private String getLocationSunString() {
        int currentMinute = Theme.autoNightSunriseTime - ((Theme.autoNightSunriseTime / 60) * 60);
        String sunriseTimeStr = String.format("%02d:%02d", new Object[]{Integer.valueOf(Theme.autoNightSunriseTime / 60), Integer.valueOf(currentMinute)});
        currentMinute = Theme.autoNightSunsetTime - ((Theme.autoNightSunsetTime / 60) * 60);
        String sunsetTimeStr = String.format("%02d:%02d", new Object[]{Integer.valueOf(Theme.autoNightSunsetTime / 60), Integer.valueOf(currentMinute)});
        return LocaleController.formatString("AutoNightUpdateLocationInfo", R.string.AutoNightUpdateLocationInfo, sunsetTimeStr, sunriseTimeStr);
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[26];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, ThemeCell.class}, null, null, null, "windowBackgroundWhite");
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r9[9] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"checkImage"}, null, null, null, "featuredStickers_addedIcon");
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"optionsButton"}, null, null, null, "stickers_menu");
        r9[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r9[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        r9[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r9[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, null, null, null, "profile_actionIcon");
        r9[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, null, null, null, "profile_actionIcon");
        r9[22] = new ThemeDescription(this.listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, "player_progressBackground");
        r9[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, "player_progress");
        r9[24] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[25] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, null, null, null, "featuredStickers_addedIcon");
        return r9;
    }
}
