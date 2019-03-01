package org.telegram.ui;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
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
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.BrightnessControlCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell$ChatMessageCellDelegate$$CC;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.CheckBoxCell;
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
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ThemeEditorView;

public class ThemeActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int THEME_TYPE_BASIC = 0;
    public static final int THEME_TYPE_NIGHT = 1;
    private static final int create_theme = 1;
    private int automaticBrightnessInfoRow;
    private int automaticBrightnessRow;
    private int automaticHeaderRow;
    private int backgroundRow;
    private int contactsReimportRow;
    private int contactsSortRow;
    private int currentType;
    private int customTabsRow;
    private int directShareRow;
    private int emojiRow;
    private int enableAnimationsRow;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener(this, null);
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private GpsLocationListener networkLocationListener = new GpsLocationListener(this, null);
    private int newThemeInfoRow;
    private int nightAutomaticRow;
    private int nightDisabledRow;
    private int nightScheduledRow;
    private int nightThemeRow;
    private int nightTypeInfoRow;
    private int preferedHeaderRow;
    private boolean previousByLocation;
    private int previousUpdatedType;
    private int raiseToSpeakRow;
    private int rowCount;
    private int saveToGalleryRow;
    private int scheduleFromRow;
    private int scheduleFromToInfoRow;
    private int scheduleHeaderRow;
    private int scheduleLocationInfoRow;
    private int scheduleLocationRow;
    private int scheduleToRow;
    private int scheduleUpdateLocationRow;
    private int sendByEnterRow;
    private int settings2Row;
    private int settingsRow;
    private int stickersRow;
    private int stickersSection2Row;
    private int textSizeHeaderRow;
    private int textSizeRow;
    private int themeEndRow;
    private int themeHeaderRow;
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
                case 7:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextSizeCell(this.mContext);
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
            r13 = "org.telegram.messenger.provider";
            r12 = android.support.v4.content.FileProvider.getUriForFile(r12, r13, r5);	 Catch:{ Exception -> 0x00f9 }
            r7.putExtra(r11, r12);	 Catch:{ Exception -> 0x00f9 }
            r11 = 1;
            r7.setFlags(r11);	 Catch:{ Exception -> 0x00f9 }
        L_0x00de:
            r11 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x00f4 }
            r12 = "ShareFile";
            r13 = NUM; // 0x7f0CLASSNAMEf float:1.8613409E38 double:1.0530984256E-314;
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
            r12 = NUM; // 0x7f0CLASSNAMEad float:1.8610581E38 double:1.053097737E-314;
            r11 = org.telegram.messenger.LocaleController.getString(r11, r12);
            r1.setMessage(r11);
            r11 = "AppName";
            r12 = NUM; // 0x7f0CLASSNAME float:1.8609492E38 double:1.0530974716E-314;
            r11 = org.telegram.messenger.LocaleController.getString(r11, r12);
            r1.setTitle(r11);
            r11 = "Delete";
            r12 = NUM; // 0x7f0CLASSNAME float:1.8610537E38 double:1.053097726E-314;
            r11 = org.telegram.messenger.LocaleController.getString(r11, r12);
            r12 = new org.telegram.ui.ThemeActivity$ListAdapter$$Lambda$2;
            r12.<init>(r14, r15);
            r1.setPositiveButton(r11, r12);
            r11 = "Cancel";
            r12 = NUM; // 0x7f0CLASSNAME float:1.860994E38 double:1.053097581E-314;
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
            switch (holder.getItemViewType()) {
                case 0:
                    position -= ThemeActivity.this.themeStartRow;
                    ((ThemeCell) holder.itemView).setTheme((ThemeInfo) Theme.themes.get(position), position != Theme.themes.size() + -1);
                    return;
                case 1:
                    TextSettingsCell cell = holder.itemView;
                    int currentMinute;
                    if (position == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType == 0 || Theme.getCurrentNightTheme() == null) {
                            cell.setTextAndValue(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff), false);
                            return;
                        } else {
                            cell.setTextAndValue(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), Theme.getCurrentNightThemeName(), false);
                            return;
                        }
                    } else if (position == ThemeActivity.this.scheduleFromRow) {
                        currentMinute = Theme.autoNightDayStartTime - ((Theme.autoNightDayStartTime / 60) * 60);
                        cell.setTextAndValue(LocaleController.getString("AutoNightFrom", R.string.AutoNightFrom), String.format("%02d:%02d", new Object[]{Integer.valueOf(Theme.autoNightDayStartTime / 60), Integer.valueOf(currentMinute)}), true);
                        return;
                    } else if (position == ThemeActivity.this.scheduleToRow) {
                        currentMinute = Theme.autoNightDayEndTime - ((Theme.autoNightDayEndTime / 60) * 60);
                        cell.setTextAndValue(LocaleController.getString("AutoNightTo", R.string.AutoNightTo), String.format("%02d:%02d", new Object[]{Integer.valueOf(Theme.autoNightDayEndTime / 60), Integer.valueOf(currentMinute)}), false);
                        return;
                    } else if (position == ThemeActivity.this.scheduleUpdateLocationRow) {
                        cell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
                        return;
                    } else if (position == ThemeActivity.this.contactsSortRow) {
                        String value;
                        int sort = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                        if (sort == 0) {
                            value = LocaleController.getString("Default", R.string.Default);
                        } else if (sort == 1) {
                            value = LocaleController.getString("FirstName", R.string.SortFirstName);
                        } else {
                            value = LocaleController.getString("LastName", R.string.SortLastName);
                        }
                        cell.setTextAndValue(LocaleController.getString("SortBy", R.string.SortBy), value, true);
                        return;
                    } else if (position == ThemeActivity.this.backgroundRow) {
                        cell.setText(LocaleController.getString("ChatBackground", R.string.ChatBackground), true);
                        return;
                    } else if (position == ThemeActivity.this.contactsReimportRow) {
                        cell.setText(LocaleController.getString("ImportContacts", R.string.ImportContacts), true);
                        return;
                    } else if (position == ThemeActivity.this.stickersRow) {
                        cell.setText(LocaleController.getString("StickersAndMasks", R.string.StickersAndMasks), false);
                        return;
                    } else if (position == ThemeActivity.this.emojiRow) {
                        cell.setText(LocaleController.getString("Emoji", R.string.Emoji), true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell2 = holder.itemView;
                    if (position == ThemeActivity.this.automaticBrightnessInfoRow) {
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
                    if (position == ThemeActivity.this.nightDisabledRow) {
                        typeCell.setValue(LocaleController.getString("AutoNightDisabled", R.string.AutoNightDisabled), Theme.selectedAutoNightType == 0, true);
                        return;
                    } else if (position == ThemeActivity.this.nightScheduledRow) {
                        typeCell.setValue(LocaleController.getString("AutoNightScheduled", R.string.AutoNightScheduled), Theme.selectedAutoNightType == 1, true);
                        return;
                    } else if (position == ThemeActivity.this.nightAutomaticRow) {
                        typeCell.setValue(LocaleController.getString("AutoNightAutomatic", R.string.AutoNightAutomatic), Theme.selectedAutoNightType == 2, false);
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
                    } else if (position == ThemeActivity.this.settingsRow) {
                        headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                        return;
                    } else if (position == ThemeActivity.this.themeHeaderRow) {
                        headerCell.setText(LocaleController.getString("ColorTheme", R.string.ColorTheme));
                        return;
                    } else if (position == ThemeActivity.this.textSizeHeaderRow) {
                        headerCell.setText(LocaleController.getString("TextSizeHeader", R.string.TextSizeHeader));
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
                    } else if (position == ThemeActivity.this.enableAnimationsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("EnableAnimations", R.string.EnableAnimations), MessagesController.getGlobalMainSettings().getBoolean("view_animations", true), true);
                        return;
                    } else if (position == ThemeActivity.this.sendByEnterRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SendByEnter", R.string.SendByEnter), MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false), true);
                        return;
                    } else if (position == ThemeActivity.this.saveToGalleryRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", R.string.SaveToGallerySettings), SharedConfig.saveToGallery, false);
                        return;
                    } else if (position == ThemeActivity.this.raiseToSpeakRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), SharedConfig.raiseToSpeak, true);
                        return;
                    } else if (position == ThemeActivity.this.customTabsRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), LocaleController.getString("ChromeCustomTabsInfo", R.string.ChromeCustomTabsInfo), SharedConfig.customTabs, false, true);
                        return;
                    } else if (position == ThemeActivity.this.directShareRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DirectShare", R.string.DirectShare), LocaleController.getString("DirectShareInfo", R.string.DirectShareInfo), SharedConfig.directShare, false, true);
                        return;
                    } else {
                        return;
                    }
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

        public int getItemViewType(int position) {
            if (position == ThemeActivity.this.nightThemeRow || position == ThemeActivity.this.scheduleFromRow || position == ThemeActivity.this.emojiRow || position == ThemeActivity.this.scheduleToRow || position == ThemeActivity.this.scheduleUpdateLocationRow || position == ThemeActivity.this.backgroundRow || position == ThemeActivity.this.contactsReimportRow || position == ThemeActivity.this.contactsSortRow || position == ThemeActivity.this.stickersRow) {
                return 1;
            }
            if (position == ThemeActivity.this.automaticBrightnessInfoRow || position == ThemeActivity.this.scheduleLocationInfoRow) {
                return 2;
            }
            if (position == ThemeActivity.this.themeInfoRow || position == ThemeActivity.this.nightTypeInfoRow || position == ThemeActivity.this.scheduleFromToInfoRow || position == ThemeActivity.this.stickersSection2Row || position == ThemeActivity.this.settings2Row || position == ThemeActivity.this.newThemeInfoRow) {
                return 3;
            }
            if (position == ThemeActivity.this.nightDisabledRow || position == ThemeActivity.this.nightScheduledRow || position == ThemeActivity.this.nightAutomaticRow) {
                return 4;
            }
            if (position == ThemeActivity.this.scheduleHeaderRow || position == ThemeActivity.this.automaticHeaderRow || position == ThemeActivity.this.preferedHeaderRow || position == ThemeActivity.this.settingsRow || position == ThemeActivity.this.themeHeaderRow || position == ThemeActivity.this.textSizeHeaderRow) {
                return 5;
            }
            if (position == ThemeActivity.this.automaticBrightnessRow) {
                return 6;
            }
            if (position == ThemeActivity.this.scheduleLocationRow || position == ThemeActivity.this.enableAnimationsRow || position == ThemeActivity.this.sendByEnterRow || position == ThemeActivity.this.saveToGalleryRow || position == ThemeActivity.this.raiseToSpeakRow || position == ThemeActivity.this.customTabsRow || position == ThemeActivity.this.directShareRow) {
                return 7;
            }
            if (position == ThemeActivity.this.textSizeRow) {
                return 8;
            }
            return 0;
        }
    }

    public interface SizeChooseViewDelegate {
        void onSizeChanged();
    }

    private class TextSizeCell extends FrameLayout {
        private ChatMessageCell[] cells = new ChatMessageCell[2];
        private int endFontSize = 30;
        private int lastWidth;
        private LinearLayout messagesContainer;
        private Drawable shadowDrawable;
        private SeekBarView sizeBar;
        private int startFontSize = 12;
        private TextPaint textPaint;

        public TextSizeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.textPaint = new TextPaint(1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
            this.shadowDrawable = Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow");
            this.sizeBar = new SeekBarView(context);
            this.sizeBar.setReportChanges(true);
            this.sizeBar.setDelegate(new ThemeActivity$TextSizeCell$$Lambda$0(this));
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 9.0f, 5.0f, 43.0f, 0.0f));
            final ThemeActivity themeActivity = ThemeActivity.this;
            this.messagesContainer = new LinearLayout(context) {
                private Drawable backgroundDrawable;

                protected void onDraw(Canvas canvas) {
                    Drawable newDrawable = Theme.getCachedWallpaperNonBlocking();
                    if (newDrawable != null) {
                        this.backgroundDrawable = newDrawable;
                    }
                    float scale;
                    if (this.backgroundDrawable instanceof ColorDrawable) {
                        this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                        this.backgroundDrawable.draw(canvas);
                    } else if (!(this.backgroundDrawable instanceof BitmapDrawable)) {
                        super.onDraw(canvas);
                    } else if (this.backgroundDrawable.getTileModeX() == TileMode.REPEAT) {
                        canvas.save();
                        scale = 2.0f / AndroidUtilities.density;
                        canvas.scale(scale, scale);
                        this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / scale)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / scale)));
                        this.backgroundDrawable.draw(canvas);
                        canvas.restore();
                    } else {
                        int viewHeight = getMeasuredHeight();
                        float scaleX = ((float) getMeasuredWidth()) / ((float) this.backgroundDrawable.getIntrinsicWidth());
                        float scaleY = ((float) viewHeight) / ((float) this.backgroundDrawable.getIntrinsicHeight());
                        if (scaleX < scaleY) {
                            scale = scaleY;
                        } else {
                            scale = scaleX;
                        }
                        int width = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicWidth()) * scale));
                        int height = (int) Math.ceil((double) (((float) this.backgroundDrawable.getIntrinsicHeight()) * scale));
                        int x = (getMeasuredWidth() - width) / 2;
                        int y = (viewHeight - height) / 2;
                        canvas.save();
                        canvas.clipRect(0, 0, width, getMeasuredHeight());
                        this.backgroundDrawable.setBounds(x, y, x + width, y + height);
                        this.backgroundDrawable.draw(canvas);
                        canvas.restore();
                    }
                    TextSizeCell.this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    TextSizeCell.this.shadowDrawable.draw(canvas);
                }

                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    return false;
                }

                public boolean dispatchTouchEvent(MotionEvent ev) {
                    return false;
                }

                protected void dispatchSetPressed(boolean pressed) {
                }

                public boolean onTouchEvent(MotionEvent event) {
                    return false;
                }
            };
            this.messagesContainer.setOrientation(1);
            this.messagesContainer.setWillNotDraw(false);
            this.messagesContainer.setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            addView(this.messagesContainer, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 53.0f, 0.0f, 0.0f));
            int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            Message message = new TL_message();
            message.message = LocaleController.getString("FontSizePreviewReply", R.string.FontSizePreviewReply);
            message.date = date + 60;
            message.dialog_id = 1;
            message.flags = 259;
            message.from_id = UserConfig.getInstance(ThemeActivity.this.currentAccount).getClientUserId();
            message.id = 1;
            message.media = new TL_messageMediaEmpty();
            message.out = true;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = 0;
            MessageObject messageObject = new MessageObject(ThemeActivity.this.currentAccount, message, true);
            message = new TL_message();
            message.message = LocaleController.getString("FontSizePreviewLine2", R.string.FontSizePreviewLine2);
            message.date = date + 960;
            message.dialog_id = 1;
            message.flags = 259;
            message.from_id = UserConfig.getInstance(ThemeActivity.this.currentAccount).getClientUserId();
            message.id = 1;
            message.media = new TL_messageMediaEmpty();
            message.out = true;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = 0;
            MessageObject message1 = new MessageObject(ThemeActivity.this.currentAccount, message, true);
            message1.resetLayout();
            message1.eventId = 1;
            message = new TL_message();
            message.message = LocaleController.getString("FontSizePreviewLine1", R.string.FontSizePreviewLine1);
            message.date = date + 60;
            message.dialog_id = 1;
            message.flags = 265;
            message.from_id = 0;
            message.id = 1;
            message.reply_to_msg_id = 5;
            message.media = new TL_messageMediaEmpty();
            message.out = false;
            message.to_id = new TL_peerUser();
            message.to_id.user_id = UserConfig.getInstance(ThemeActivity.this.currentAccount).getClientUserId();
            MessageObject message2 = new MessageObject(ThemeActivity.this.currentAccount, message, true);
            message2.customReplyName = LocaleController.getString("FontSizePreviewName", R.string.FontSizePreviewName);
            message2.eventId = 1;
            message2.resetLayout();
            message2.replyMessageObject = messageObject;
            for (int a = 0; a < this.cells.length; a++) {
                MessageObject messageObject2;
                this.cells[a] = new ChatMessageCell(context);
                final ThemeActivity themeActivity2 = ThemeActivity.this;
                this.cells[a].setDelegate(new ChatMessageCellDelegate() {
                    public boolean canPerformActions() {
                        return ChatMessageCell$ChatMessageCellDelegate$$CC.canPerformActions(this);
                    }

                    public void didLongPress(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didLongPress(this, chatMessageCell);
                    }

                    public void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressBotButton(this, chatMessageCell, keyboardButton);
                    }

                    public void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressCancelSendButton(this, chatMessageCell);
                    }

                    public void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressChannelAvatar(this, chatMessageCell, chat, i);
                    }

                    public void didPressImage(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressImage(this, chatMessageCell);
                    }

                    public void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressInstantButton(this, chatMessageCell, i);
                    }

                    public void didPressOther(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressOther(this, chatMessageCell);
                    }

                    public void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressReplyMessage(this, chatMessageCell, i);
                    }

                    public void didPressShare(ChatMessageCell chatMessageCell) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressShare(this, chatMessageCell);
                    }

                    public void didPressUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressUrl(this, messageObject, characterStyle, z);
                    }

                    public void didPressUserAvatar(ChatMessageCell chatMessageCell, User user) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressUserAvatar(this, chatMessageCell, user);
                    }

                    public void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressViaBot(this, chatMessageCell, str);
                    }

                    public void didPressVoteButton(ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.didPressVoteButton(this, chatMessageCell, tL_pollAnswer);
                    }

                    public boolean isChatAdminCell(int i) {
                        return ChatMessageCell$ChatMessageCellDelegate$$CC.isChatAdminCell(this, i);
                    }

                    public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.needOpenWebView(this, str, str2, str3, str4, i, i2);
                    }

                    public boolean needPlayMessage(MessageObject messageObject) {
                        return ChatMessageCell$ChatMessageCellDelegate$$CC.needPlayMessage(this, messageObject);
                    }

                    public void videoTimerReached() {
                        ChatMessageCell$ChatMessageCellDelegate$$CC.videoTimerReached(this);
                    }
                });
                this.cells[a].isChat = false;
                this.cells[a].setFullyDraw(true);
                ChatMessageCell chatMessageCell = this.cells[a];
                if (a == 0) {
                    messageObject2 = message2;
                } else {
                    messageObject2 = message1;
                }
                chatMessageCell.setMessageObject(messageObject2, null, false, false);
                this.messagesContainer.addView(this.cells[a], LayoutHelper.createLinear(-1, -2));
            }
        }

        final /* synthetic */ void lambda$new$0$ThemeActivity$TextSizeCell(float progress) {
            int fontSize = Math.round(((float) this.startFontSize) + (((float) (this.endFontSize - this.startFontSize)) * progress));
            if (fontSize != SharedConfig.fontSize) {
                SharedConfig.fontSize = fontSize;
                Editor editor = MessagesController.getGlobalMainSettings().edit();
                editor.putInt("fons_size", SharedConfig.fontSize);
                editor.commit();
                Theme.chat_msgTextPaint.setTextSize((float) AndroidUtilities.dp((float) SharedConfig.fontSize));
                for (int a = 0; a < this.cells.length; a++) {
                    this.cells[a].getMessageObject().resetLayout();
                    this.cells[a].requestLayout();
                }
            }
        }

        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            canvas.drawText("" + SharedConfig.fontSize, (float) (getMeasuredWidth() - AndroidUtilities.dp(39.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int w = MeasureSpec.getSize(widthMeasureSpec);
            if (this.lastWidth != w) {
                this.sizeBar.setProgress(((float) (SharedConfig.fontSize - this.startFontSize)) / ((float) (this.endFontSize - this.startFontSize)));
                this.lastWidth = w;
            }
        }

        public void invalidate() {
            super.invalidate();
            this.messagesContainer.invalidate();
            this.sizeBar.invalidate();
            for (ChatMessageCell invalidate : this.cells) {
                invalidate.invalidate();
            }
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
        this.emojiRow = -1;
        this.contactsReimportRow = -1;
        this.contactsSortRow = -1;
        this.scheduleLocationRow = -1;
        this.scheduleUpdateLocationRow = -1;
        this.scheduleLocationInfoRow = -1;
        this.nightDisabledRow = -1;
        this.nightScheduledRow = -1;
        this.nightAutomaticRow = -1;
        this.nightTypeInfoRow = -1;
        this.scheduleHeaderRow = -1;
        this.nightThemeRow = -1;
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
        this.textSizeHeaderRow = -1;
        this.themeHeaderRow = -1;
        this.textSizeRow = -1;
        this.backgroundRow = -1;
        this.settingsRow = -1;
        this.customTabsRow = -1;
        this.directShareRow = -1;
        this.enableAnimationsRow = -1;
        this.raiseToSpeakRow = -1;
        this.sendByEnterRow = -1;
        this.saveToGalleryRow = -1;
        this.settings2Row = -1;
        this.stickersRow = -1;
        this.stickersSection2Row = -1;
        if (this.currentType == 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.textSizeHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.textSizeRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.backgroundRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.nightThemeRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.newThemeInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.themeHeaderRow = i;
            this.themeStartRow = this.rowCount;
            this.rowCount += Theme.themes.size();
            this.themeEndRow = this.rowCount;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.themeInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.settingsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.customTabsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.directShareRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.enableAnimationsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.raiseToSpeakRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendByEnterRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.saveToGalleryRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.settings2Row = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersSection2Row = i;
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
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        stopLocationUpdate();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        Theme.saveAutoNightThemeConfig();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.locationPermissionGranted) {
            updateSunTime(null, true);
        } else if (id == NotificationCenter.didSetNewWallpapper && this.listView != null) {
            this.listView.invalidateViews();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatSettings", R.string.ChatSettings));
            this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_other).addSubItem(1, LocaleController.getString("CreateNewThemeMenu", R.string.CreateNewThemeMenu));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ThemeActivity.this.lambda$checkDiscard$70$PassportActivity();
                } else if (id == 1 && ThemeActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("NewTheme", R.string.NewTheme));
                    builder.setMessage(LocaleController.getString("CreateNewThemeAlert", R.string.CreateNewThemeAlert));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder.setPositiveButton(LocaleController.getString("CreateTheme", R.string.CreateTheme), new ThemeActivity$1$$Lambda$0(this));
                    ThemeActivity.this.showDialog(builder.create());
                }
            }

            final /* synthetic */ void lambda$onItemClick$0$ThemeActivity$1(DialogInterface dialog, int which) {
                ThemeActivity.this.openThemeCreate();
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

    final /* synthetic */ void lambda$createView$4$ThemeActivity(View view, int position) {
        SharedPreferences preferences;
        Editor editor;
        if (position == this.enableAnimationsRow) {
            preferences = MessagesController.getGlobalMainSettings();
            boolean animations = preferences.getBoolean("view_animations", true);
            editor = preferences.edit();
            editor.putBoolean("view_animations", !animations);
            editor.commit();
            if (view instanceof TextCheckCell) {
                boolean z;
                TextCheckCell textCheckCell = (TextCheckCell) view;
                if (animations) {
                    z = false;
                } else {
                    z = true;
                }
                textCheckCell.setChecked(z);
            }
        } else if (position == this.backgroundRow) {
            presentFragment(new WallpapersListActivity(0));
        } else if (position == this.sendByEnterRow) {
            preferences = MessagesController.getGlobalMainSettings();
            boolean send = preferences.getBoolean("send_by_enter", false);
            editor = preferences.edit();
            editor.putBoolean("send_by_enter", !send);
            editor.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!send);
            }
        } else if (position == this.raiseToSpeakRow) {
            SharedConfig.toogleRaiseToSpeak();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
            }
        } else if (position == this.saveToGalleryRow) {
            SharedConfig.toggleSaveToGallery();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.saveToGallery);
            }
        } else if (position == this.customTabsRow) {
            SharedConfig.toggleCustomTabs();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
            }
        } else if (position == this.directShareRow) {
            SharedConfig.toggleDirectShare();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.directShare);
            }
        } else if (position == this.contactsReimportRow) {
        } else {
            int a;
            if (position == this.contactsSortRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("SortBy", R.string.SortBy));
                    builder.setItems(new CharSequence[]{LocaleController.getString("Default", R.string.Default), LocaleController.getString("SortFirstName", R.string.SortFirstName), LocaleController.getString("SortLastName", R.string.SortLastName)}, new ThemeActivity$$Lambda$10(this, position));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                }
            } else if (position == this.stickersRow) {
                presentFragment(new StickersActivity(0));
            } else if (position == this.emojiRow) {
                if (getParentActivity() != null) {
                    boolean[] maskValues = new boolean[2];
                    Builder builder2 = new Builder(getParentActivity());
                    builder2.setApplyTopPadding(false);
                    builder2.setApplyBottomPadding(false);
                    View linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setOrientation(1);
                    a = 0;
                    while (true) {
                        if (a < (VERSION.SDK_INT >= 19 ? 2 : 1)) {
                            String name = null;
                            if (a == 0) {
                                maskValues[a] = SharedConfig.allowBigEmoji;
                                name = LocaleController.getString("EmojiBigSize", R.string.EmojiBigSize);
                            } else if (a == 1) {
                                maskValues[a] = SharedConfig.useSystemEmoji;
                                name = LocaleController.getString("EmojiUseDefault", R.string.EmojiUseDefault);
                            }
                            CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1, 21);
                            checkBoxCell.setTag(Integer.valueOf(a));
                            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                            checkBoxCell.setText(name, "", maskValues[a], true);
                            checkBoxCell.setTextColor(Theme.getColor("dialogTextBlack"));
                            checkBoxCell.setOnClickListener(new ThemeActivity$$Lambda$11(maskValues));
                            a++;
                        } else {
                            BottomSheetCell cell = new BottomSheetCell(getParentActivity(), 1);
                            cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            cell.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
                            cell.setTextColor(Theme.getColor("dialogTextBlue2"));
                            cell.setOnClickListener(new ThemeActivity$$Lambda$12(this, maskValues, position));
                            linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
                            builder2.setCustomView(linearLayout);
                            showDialog(builder2.create());
                            return;
                        }
                    }
                }
            } else if (position >= this.themeStartRow && position < this.themeEndRow) {
                int p = position - this.themeStartRow;
                if (p >= 0 && p < Theme.themes.size()) {
                    ThemeInfo themeInfo = (ThemeInfo) Theme.themes.get(p);
                    if (this.currentType != 0) {
                        Theme.setCurrentNightTheme(themeInfo);
                    } else if (themeInfo != Theme.getCurrentTheme()) {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.valueOf(false));
                    } else {
                        return;
                    }
                    int count = this.listView.getChildCount();
                    for (a = 0; a < count; a++) {
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
                    showDialog(new TimePickerDialog(getParentActivity(), new ThemeActivity$$Lambda$13(this, position, (TextSettingsCell) view), currentHour, currentMinute, true));
                }
            } else if (position == this.scheduleUpdateLocationRow) {
                updateSunTime(null, true);
            }
        }
    }

    final /* synthetic */ void lambda$null$0$ThemeActivity(int position, DialogInterface dialog, int which) {
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("sortContactsBy", which);
        editor.commit();
        if (this.listAdapter != null) {
            this.listAdapter.notifyItemChanged(position);
        }
    }

    static final /* synthetic */ void lambda$null$1$ThemeActivity(boolean[] maskValues, View v) {
        CheckBoxCell cell = (CheckBoxCell) v;
        int num = ((Integer) cell.getTag()).intValue();
        maskValues[num] = !maskValues[num];
        cell.setChecked(maskValues[num], true);
    }

    final /* synthetic */ void lambda$null$2$ThemeActivity(boolean[] maskValues, int position, View v) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        boolean z = maskValues[0];
        SharedConfig.allowBigEmoji = z;
        editor.putBoolean("allowBigEmoji", z);
        z = maskValues[1];
        SharedConfig.useSystemEmoji = z;
        editor.putBoolean("useSystemEmoji", z);
        editor.commit();
        if (this.listAdapter != null) {
            this.listAdapter.notifyItemChanged(position);
        }
    }

    final /* synthetic */ void lambda$null$3$ThemeActivity(int position, TextSettingsCell cell, TimePicker view1, int hourOfDay, int minute) {
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

    private void openThemeCreate() {
        EditTextBoldCursor editText = new EditTextBoldCursor(getParentActivity());
        editText.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("NewTheme", R.string.NewTheme));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), ThemeActivity$$Lambda$1.$instance);
        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        TextView message = new TextView(getParentActivity());
        message.setText(LocaleController.formatString("EnterThemeName", R.string.EnterThemeName, new Object[0]));
        message.setTextSize(16.0f);
        message.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
        message.setTextColor(Theme.getColor("dialogTextBlack"));
        linearLayout.addView(message, LayoutHelper.createLinear(-1, -2));
        editText.setTextSize(1, 16.0f);
        editText.setTextColor(Theme.getColor("dialogTextBlack"));
        editText.setMaxLines(1);
        editText.setLines(1);
        editText.setInputType(16385);
        editText.setGravity(51);
        editText.setSingleLine(true);
        editText.setImeOptions(6);
        editText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        editText.setCursorSize(AndroidUtilities.dp(20.0f));
        editText.setCursorWidth(1.5f);
        editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        linearLayout.addView(editText, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
        editText.setOnEditorActionListener(ThemeActivity$$Lambda$2.$instance);
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new ThemeActivity$$Lambda$3(editText));
        showDialog(alertDialog);
        alertDialog.getButton(-1).setOnClickListener(new ThemeActivity$$Lambda$4(this, editText, alertDialog));
    }

    static final /* synthetic */ void lambda$openThemeCreate$5$ThemeActivity(DialogInterface dialog, int which) {
    }

    static final /* synthetic */ void lambda$null$7$ThemeActivity(EditTextBoldCursor editText) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    final /* synthetic */ void lambda$openThemeCreate$9$ThemeActivity(EditTextBoldCursor editText, AlertDialog alertDialog, View v) {
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
                        builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new ThemeActivity$$Lambda$5(this));
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
        Utilities.globalQueue.postRunnable(new ThemeActivity$$Lambda$6(this));
        Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
        if (holder != null && (holder.itemView instanceof TextInfoPrivacyCell)) {
            ((TextInfoPrivacyCell) holder.itemView).setText(getLocationSunString());
        }
        if (Theme.autoNightScheduleByLocation && Theme.selectedAutoNightType == 1) {
            Theme.checkAutoNightThemeConditions();
        }
    }

    final /* synthetic */ void lambda$updateSunTime$10$ThemeActivity(DialogInterface dialog, int id) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception e) {
            }
        }
    }

    final /* synthetic */ void lambda$updateSunTime$12$ThemeActivity() {
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
        AndroidUtilities.runOnUIThread(new ThemeActivity$$Lambda$8(this, name));
    }

    final /* synthetic */ void lambda$null$11$ThemeActivity(String nameFinal) {
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
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new ThemeActivity$$Lambda$7(this));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$showPermissionAlert$13$ThemeActivity(DialogInterface dialog, int which) {
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
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[52];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, ThemeCell.class, TextSizeCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"checkImage"}, null, null, null, "featuredStickers_addedIcon");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{ThemeCell.class}, new String[]{"optionsButton"}, null, null, null, "stickers_menu");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, null, null, null, "profile_actionIcon");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, null, null, null, "profile_actionIcon");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, "player_progressBackground");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, "player_progress");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, null, null, null, "featuredStickers_addedIcon");
        themeDescriptionArr[27] = new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, null, null, null, "player_progress");
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, null, null, null, "player_progressBackground");
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow");
        themeDescriptionArr[35] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextIn");
        themeDescriptionArr[36] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextOut");
        themeDescriptionArr[37] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheck");
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        themeDescriptionArr[39] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        themeDescriptionArr[40] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyLine");
        themeDescriptionArr[41] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyLine");
        themeDescriptionArr[42] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyNameText");
        themeDescriptionArr[43] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyNameText");
        themeDescriptionArr[44] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMessageText");
        themeDescriptionArr[45] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMessageText");
        themeDescriptionArr[46] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMediaMessageSelectedText");
        themeDescriptionArr[47] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMediaMessageSelectedText");
        themeDescriptionArr[48] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeText");
        themeDescriptionArr[49] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeText");
        themeDescriptionArr[50] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeSelectedText");
        themeDescriptionArr[51] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeSelectedText");
        return themeDescriptionArr;
    }
}
