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
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
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
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
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
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ThemeEditorView;

public class ThemeActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  public static final int THEME_TYPE_BASIC = 0;
  public static final int THEME_TYPE_NIGHT = 1;
  private int automaticBrightnessInfoRow;
  private int automaticBrightnessRow;
  private int automaticHeaderRow;
  private int currentType;
  private GpsLocationListener gpsLocationListener = new GpsLocationListener(null);
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private GpsLocationListener networkLocationListener = new GpsLocationListener(null);
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
  
  public ThemeActivity(int paramInt)
  {
    this.currentType = paramInt;
    updateRows();
  }
  
  private String getLocationSunString()
  {
    int i = Theme.autoNightSunriseTime / 60;
    String str = String.format("%02d:%02d", new Object[] { Integer.valueOf(i), Integer.valueOf(Theme.autoNightSunriseTime - i * 60) });
    i = Theme.autoNightSunsetTime / 60;
    return LocaleController.formatString("AutoNightUpdateLocationInfo", NUM, new Object[] { String.format("%02d:%02d", new Object[] { Integer.valueOf(i), Integer.valueOf(Theme.autoNightSunsetTime - i * 60) }), str });
  }
  
  private void showPermissionAlert(boolean paramBoolean)
  {
    if (getParentActivity() == null) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", NUM));
    if (paramBoolean) {
      localBuilder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
    }
    for (;;)
    {
      localBuilder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener()
      {
        @TargetApi(9)
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (ThemeActivity.this.getParentActivity() == null) {}
          for (;;)
          {
            return;
            try
            {
              paramAnonymousDialogInterface = new android/content/Intent;
              paramAnonymousDialogInterface.<init>("android.settings.APPLICATION_DETAILS_SETTINGS");
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              paramAnonymousDialogInterface.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
              ThemeActivity.this.getParentActivity().startActivity(paramAnonymousDialogInterface);
            }
            catch (Exception paramAnonymousDialogInterface)
            {
              FileLog.e(paramAnonymousDialogInterface);
            }
          }
        }
      });
      localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
      showDialog(localBuilder.create());
      break;
      localBuilder.setMessage(LocaleController.getString("PermissionNoLocation", NUM));
    }
  }
  
  private void startLocationUpdate()
  {
    if (this.updatingLocation) {}
    for (;;)
    {
      return;
      this.updatingLocation = true;
      LocationManager localLocationManager = (LocationManager)ApplicationLoader.applicationContext.getSystemService("location");
      try
      {
        localLocationManager.requestLocationUpdates("gps", 1L, 0.0F, this.gpsLocationListener);
        try
        {
          localLocationManager.requestLocationUpdates("network", 1L, 0.0F, this.networkLocationListener);
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e(localException2);
        }
      }
    }
  }
  
  private void stopLocationUpdate()
  {
    this.updatingLocation = false;
    LocationManager localLocationManager = (LocationManager)ApplicationLoader.applicationContext.getSystemService("location");
    localLocationManager.removeUpdates(this.gpsLocationListener);
    localLocationManager.removeUpdates(this.networkLocationListener);
  }
  
  private void updateRows()
  {
    int i = 2;
    int j = this.rowCount;
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
    if (this.currentType == 0)
    {
      k = this.rowCount;
      this.rowCount = (k + 1);
      this.nightThemeRow = k;
      k = this.rowCount;
      this.rowCount = (k + 1);
      this.newThemeRow = k;
      k = this.rowCount;
      this.rowCount = (k + 1);
      this.newThemeInfoRow = k;
      this.themeStartRow = this.rowCount;
      this.rowCount += Theme.themes.size();
      this.themeEndRow = this.rowCount;
      k = this.rowCount;
      this.rowCount = (k + 1);
      this.themeInfoRow = k;
      if (this.listAdapter != null)
      {
        if ((this.currentType != 0) && (this.previousUpdatedType != -1)) {
          break label613;
        }
        this.listAdapter.notifyDataSetChanged();
      }
    }
    label611:
    label613:
    int m;
    label834:
    label882:
    do
    {
      do
      {
        for (;;)
        {
          if (this.currentType == 1)
          {
            this.previousByLocation = Theme.autoNightScheduleByLocation;
            this.previousUpdatedType = Theme.selectedAutoNightType;
          }
          return;
          k = this.rowCount;
          this.rowCount = (k + 1);
          this.nightDisabledRow = k;
          k = this.rowCount;
          this.rowCount = (k + 1);
          this.nightScheduledRow = k;
          k = this.rowCount;
          this.rowCount = (k + 1);
          this.nightAutomaticRow = k;
          k = this.rowCount;
          this.rowCount = (k + 1);
          this.nightTypeInfoRow = k;
          if (Theme.selectedAutoNightType == 1)
          {
            k = this.rowCount;
            this.rowCount = (k + 1);
            this.scheduleHeaderRow = k;
            k = this.rowCount;
            this.rowCount = (k + 1);
            this.scheduleLocationRow = k;
            if (Theme.autoNightScheduleByLocation)
            {
              k = this.rowCount;
              this.rowCount = (k + 1);
              this.scheduleUpdateLocationRow = k;
              k = this.rowCount;
              this.rowCount = (k + 1);
              this.scheduleLocationInfoRow = k;
            }
          }
          for (;;)
          {
            if (Theme.selectedAutoNightType == 0) {
              break label611;
            }
            k = this.rowCount;
            this.rowCount = (k + 1);
            this.preferedHeaderRow = k;
            this.themeStartRow = this.rowCount;
            this.rowCount += Theme.themes.size();
            this.themeEndRow = this.rowCount;
            k = this.rowCount;
            this.rowCount = (k + 1);
            this.themeInfoRow = k;
            break;
            k = this.rowCount;
            this.rowCount = (k + 1);
            this.scheduleFromRow = k;
            k = this.rowCount;
            this.rowCount = (k + 1);
            this.scheduleToRow = k;
            k = this.rowCount;
            this.rowCount = (k + 1);
            this.scheduleFromToInfoRow = k;
            continue;
            if (Theme.selectedAutoNightType == 2)
            {
              k = this.rowCount;
              this.rowCount = (k + 1);
              this.automaticHeaderRow = k;
              k = this.rowCount;
              this.rowCount = (k + 1);
              this.automaticBrightnessRow = k;
              k = this.rowCount;
              this.rowCount = (k + 1);
              this.automaticBrightnessInfoRow = k;
            }
          }
          break;
          m = this.nightTypeInfoRow + 1;
          if (this.previousUpdatedType == Theme.selectedAutoNightType) {
            break label882;
          }
          k = 0;
          while (k < 3)
          {
            localObject = (RecyclerListView.Holder)this.listView.findViewHolderForAdapterPosition(k);
            if (localObject == null)
            {
              k++;
            }
            else
            {
              localObject = (ThemeTypeCell)((RecyclerListView.Holder)localObject).itemView;
              if (k == Theme.selectedAutoNightType) {}
              for (boolean bool = true;; bool = false)
              {
                ((ThemeTypeCell)localObject).setTypeChecked(bool);
                break;
              }
            }
          }
          if (Theme.selectedAutoNightType == 0)
          {
            this.listAdapter.notifyItemRangeRemoved(m, j - m);
          }
          else if (Theme.selectedAutoNightType == 1)
          {
            if (this.previousUpdatedType == 0)
            {
              this.listAdapter.notifyItemRangeInserted(m, this.rowCount - m);
            }
            else if (this.previousUpdatedType == 2)
            {
              this.listAdapter.notifyItemRangeRemoved(m, 3);
              localObject = this.listAdapter;
              if (Theme.autoNightScheduleByLocation) {}
              for (k = 4;; k = 5)
              {
                ((ListAdapter)localObject).notifyItemRangeInserted(m, k);
                break;
              }
            }
          }
          else if (Theme.selectedAutoNightType == 2)
          {
            if (this.previousUpdatedType != 0) {
              break label834;
            }
            this.listAdapter.notifyItemRangeInserted(m, this.rowCount - m);
          }
        }
      } while (this.previousUpdatedType != 1);
      localObject = this.listAdapter;
      if (Theme.autoNightScheduleByLocation) {}
      for (k = 4;; k = 5)
      {
        ((ListAdapter)localObject).notifyItemRangeRemoved(m, k);
        this.listAdapter.notifyItemRangeInserted(m, 3);
        break;
      }
    } while (this.previousByLocation == Theme.autoNightScheduleByLocation);
    Object localObject = this.listAdapter;
    if (Theme.autoNightScheduleByLocation)
    {
      k = 3;
      label906:
      ((ListAdapter)localObject).notifyItemRangeRemoved(m + 2, k);
      localObject = this.listAdapter;
      if (!Theme.autoNightScheduleByLocation) {
        break label948;
      }
    }
    label948:
    for (int k = i;; k = 3)
    {
      ((ListAdapter)localObject).notifyItemRangeInserted(m + 2, k);
      break;
      k = 2;
      break label906;
    }
  }
  
  private void updateSunTime(Location paramLocation, boolean paramBoolean)
  {
    LocationManager localLocationManager = (LocationManager)ApplicationLoader.applicationContext.getSystemService("location");
    Object localObject;
    if (Build.VERSION.SDK_INT >= 23)
    {
      localObject = getParentActivity();
      if ((localObject != null) && (((Activity)localObject).checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0)) {
        ((Activity)localObject).requestPermissions(new String[] { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" }, 2);
      }
    }
    for (;;)
    {
      return;
      if (getParentActivity() != null)
      {
        if (!getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
          continue;
        }
        try
        {
          if (!((LocationManager)ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps"))
          {
            AlertDialog.Builder localBuilder = new org/telegram/ui/ActionBar/AlertDialog$Builder;
            localBuilder.<init>(getParentActivity());
            localBuilder.setTitle(LocaleController.getString("AppName", NUM));
            localBuilder.setMessage(LocaleController.getString("GpsDisabledAlert", NUM));
            String str = LocaleController.getString("ConnectingToProxyEnable", NUM);
            localObject = new org/telegram/ui/ThemeActivity$3;
            ((3)localObject).<init>(this);
            localBuilder.setPositiveButton(str, (DialogInterface.OnClickListener)localObject);
            localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(localBuilder.create());
          }
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
        }
      }
    }
    for (;;)
    {
      try
      {
        localLocation = localLocationManager.getLastKnownLocation("gps");
        if (localLocation != null) {
          continue;
        }
        paramLocation = localLocation;
        localLocation = localLocationManager.getLastKnownLocation("network");
        paramLocation = localLocation;
      }
      catch (Exception localException2)
      {
        Location localLocation;
        FileLog.e(localException2);
        continue;
      }
      if ((paramLocation == null) || (paramBoolean))
      {
        startLocationUpdate();
        if (paramLocation == null) {
          break;
        }
      }
      Theme.autoNightLocationLatitude = paramLocation.getLatitude();
      Theme.autoNightLocationLongitude = paramLocation.getLongitude();
      paramLocation = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
      Theme.autoNightSunriseTime = paramLocation[0];
      Theme.autoNightSunsetTime = paramLocation[1];
      Theme.autoNightCityName = null;
      paramLocation = Calendar.getInstance();
      paramLocation.setTimeInMillis(System.currentTimeMillis());
      Theme.autoNightLastSunCheckDay = paramLocation.get(5);
      Utilities.globalQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            final Object localObject1 = new android/location/Geocoder;
            ((Geocoder)localObject1).<init>(ApplicationLoader.applicationContext, Locale.getDefault());
            localObject1 = ((Geocoder)localObject1).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (((List)localObject1).size() > 0) {}
            for (localObject1 = ((Address)((List)localObject1).get(0)).getLocality();; localObject1 = null)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  Theme.autoNightCityName = localObject1;
                  if (Theme.autoNightCityName == null) {
                    Theme.autoNightCityName = String.format("(%.06f, %.06f)", new Object[] { Double.valueOf(Theme.autoNightLocationLatitude), Double.valueOf(Theme.autoNightLocationLongitude) });
                  }
                  Theme.saveAutoNightThemeConfig();
                  if (ThemeActivity.this.listView != null)
                  {
                    RecyclerListView.Holder localHolder = (RecyclerListView.Holder)ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.scheduleUpdateLocationRow);
                    if ((localHolder != null) && ((localHolder.itemView instanceof TextSettingsCell))) {
                      ((TextSettingsCell)localHolder.itemView).setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
                    }
                  }
                }
              });
              return;
            }
          }
          catch (Exception localException)
          {
            for (;;)
            {
              Object localObject2 = null;
            }
          }
        }
      });
      paramLocation = (RecyclerListView.Holder)this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
      if ((paramLocation != null) && ((paramLocation.itemView instanceof TextInfoPrivacyCell))) {
        ((TextInfoPrivacyCell)paramLocation.itemView).setText(getLocationSunString());
      }
      if ((!Theme.autoNightScheduleByLocation) || (Theme.selectedAutoNightType != 1)) {
        break;
      }
      Theme.checkAutoNightThemeConditions();
      break;
      paramLocation = localLocation;
      if (localLocation == null)
      {
        paramLocation = localLocation;
        localLocation = localLocationManager.getLastKnownLocation("passive");
        paramLocation = localLocation;
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(false);
    if (this.currentType == 0) {
      this.actionBar.setTitle(LocaleController.getString("Theme", NUM));
    }
    for (;;)
    {
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            ThemeActivity.this.finishFragment();
          }
        }
      });
      this.listAdapter = new ListAdapter(paramContext);
      FrameLayout localFrameLayout = new FrameLayout(paramContext);
      localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      this.fragmentView = localFrameLayout;
      this.listView = new RecyclerListView(paramContext);
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setAdapter(this.listAdapter);
      ((DefaultItemAnimator)this.listView.getItemAnimator()).setDelayAnimations(false);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(final View paramAnonymousView, final int paramAnonymousInt)
        {
          if (paramAnonymousInt == ThemeActivity.this.newThemeRow) {
            if (ThemeActivity.this.getParentActivity() != null) {}
          }
          for (;;)
          {
            return;
            paramAnonymousView = new EditTextBoldCursor(ThemeActivity.this.getParentActivity());
            paramAnonymousView.setBackgroundDrawable(Theme.createEditTextDrawable(ThemeActivity.this.getParentActivity(), true));
            final Object localObject = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
            ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("NewTheme", NUM));
            ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int) {}
            });
            LinearLayout localLinearLayout = new LinearLayout(ThemeActivity.this.getParentActivity());
            localLinearLayout.setOrientation(1);
            ((AlertDialog.Builder)localObject).setView(localLinearLayout);
            TextView localTextView = new TextView(ThemeActivity.this.getParentActivity());
            localTextView.setText(LocaleController.formatString("EnterThemeName", NUM, new Object[0]));
            localTextView.setTextSize(16.0F);
            localTextView.setPadding(AndroidUtilities.dp(23.0F), AndroidUtilities.dp(12.0F), AndroidUtilities.dp(23.0F), AndroidUtilities.dp(6.0F));
            localTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            localLinearLayout.addView(localTextView, LayoutHelper.createLinear(-1, -2));
            paramAnonymousView.setTextSize(1, 16.0F);
            paramAnonymousView.setTextColor(Theme.getColor("dialogTextBlack"));
            paramAnonymousView.setMaxLines(1);
            paramAnonymousView.setLines(1);
            paramAnonymousView.setInputType(16385);
            paramAnonymousView.setGravity(51);
            paramAnonymousView.setSingleLine(true);
            paramAnonymousView.setImeOptions(6);
            paramAnonymousView.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            paramAnonymousView.setCursorSize(AndroidUtilities.dp(20.0F));
            paramAnonymousView.setCursorWidth(1.5F);
            paramAnonymousView.setPadding(0, AndroidUtilities.dp(4.0F), 0, 0);
            localLinearLayout.addView(paramAnonymousView, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
            paramAnonymousView.setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
              public boolean onEditorAction(TextView paramAnonymous2TextView, int paramAnonymous2Int, KeyEvent paramAnonymous2KeyEvent)
              {
                AndroidUtilities.hideKeyboard(paramAnonymous2TextView);
                return false;
              }
            });
            localObject = ((AlertDialog.Builder)localObject).create();
            ((AlertDialog)localObject).setOnShowListener(new DialogInterface.OnShowListener()
            {
              public void onShow(DialogInterface paramAnonymous2DialogInterface)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    ThemeActivity.2.3.this.val$editText.requestFocus();
                    AndroidUtilities.showKeyboard(ThemeActivity.2.3.this.val$editText);
                  }
                });
              }
            });
            ThemeActivity.this.showDialog((Dialog)localObject);
            ((AlertDialog)localObject).getButton(-1).setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymous2View)
              {
                if (paramAnonymousView.length() == 0)
                {
                  paramAnonymous2View = (Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator");
                  if (paramAnonymous2View != null) {
                    paramAnonymous2View.vibrate(200L);
                  }
                  AndroidUtilities.shakeView(paramAnonymousView, 2.0F, 0);
                }
                for (;;)
                {
                  return;
                  ThemeEditorView localThemeEditorView = new ThemeEditorView();
                  paramAnonymous2View = paramAnonymousView.getText().toString() + ".attheme";
                  localThemeEditorView.show(ThemeActivity.this.getParentActivity(), paramAnonymous2View);
                  Theme.saveCurrentTheme(paramAnonymous2View, true);
                  ThemeActivity.this.updateRows();
                  localObject.dismiss();
                  paramAnonymous2View = MessagesController.getGlobalMainSettings();
                  if (!paramAnonymous2View.getBoolean("themehint", false))
                  {
                    paramAnonymous2View.edit().putBoolean("themehint", true).commit();
                    try
                    {
                      Toast.makeText(ThemeActivity.this.getParentActivity(), LocaleController.getString("CreateNewThemeHelp", NUM), 1).show();
                    }
                    catch (Exception paramAnonymous2View)
                    {
                      FileLog.e(paramAnonymous2View);
                    }
                  }
                }
              }
            });
            continue;
            int i;
            if ((paramAnonymousInt >= ThemeActivity.this.themeStartRow) && (paramAnonymousInt < ThemeActivity.this.themeEndRow))
            {
              paramAnonymousInt -= ThemeActivity.this.themeStartRow;
              if ((paramAnonymousInt >= 0) && (paramAnonymousInt < Theme.themes.size()))
              {
                paramAnonymousView = (Theme.ThemeInfo)Theme.themes.get(paramAnonymousInt);
                if (ThemeActivity.this.currentType == 0)
                {
                  Theme.applyTheme(paramAnonymousView);
                  if (ThemeActivity.this.parentLayout != null) {
                    ThemeActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
                  }
                  ThemeActivity.this.finishFragment();
                }
                else
                {
                  Theme.setCurrentNightTheme(paramAnonymousView);
                  i = ThemeActivity.this.listView.getChildCount();
                  for (paramAnonymousInt = 0; paramAnonymousInt < i; paramAnonymousInt++)
                  {
                    paramAnonymousView = ThemeActivity.this.listView.getChildAt(paramAnonymousInt);
                    if ((paramAnonymousView instanceof ThemeCell)) {
                      ((ThemeCell)paramAnonymousView).updateCurrentThemeCheck();
                    }
                  }
                }
              }
            }
            else if (paramAnonymousInt == ThemeActivity.this.nightThemeRow)
            {
              ThemeActivity.this.presentFragment(new ThemeActivity(1));
            }
            else if (paramAnonymousInt == ThemeActivity.this.nightDisabledRow)
            {
              Theme.selectedAutoNightType = 0;
              ThemeActivity.this.updateRows();
              Theme.checkAutoNightThemeConditions();
            }
            else if (paramAnonymousInt == ThemeActivity.this.nightScheduledRow)
            {
              Theme.selectedAutoNightType = 1;
              if (Theme.autoNightScheduleByLocation) {
                ThemeActivity.this.updateSunTime(null, true);
              }
              ThemeActivity.this.updateRows();
              Theme.checkAutoNightThemeConditions();
            }
            else if (paramAnonymousInt == ThemeActivity.this.nightAutomaticRow)
            {
              Theme.selectedAutoNightType = 2;
              ThemeActivity.this.updateRows();
              Theme.checkAutoNightThemeConditions();
            }
            else
            {
              if (paramAnonymousInt == ThemeActivity.this.scheduleLocationRow)
              {
                if (!Theme.autoNightScheduleByLocation) {}
                for (boolean bool = true;; bool = false)
                {
                  Theme.autoNightScheduleByLocation = bool;
                  ((TextCheckCell)paramAnonymousView).setChecked(Theme.autoNightScheduleByLocation);
                  ThemeActivity.this.updateRows();
                  if (Theme.autoNightScheduleByLocation) {
                    ThemeActivity.this.updateSunTime(null, true);
                  }
                  Theme.checkAutoNightThemeConditions();
                  break;
                }
              }
              if ((paramAnonymousInt == ThemeActivity.this.scheduleFromRow) || (paramAnonymousInt == ThemeActivity.this.scheduleToRow))
              {
                if (ThemeActivity.this.getParentActivity() != null)
                {
                  if (paramAnonymousInt == ThemeActivity.this.scheduleFromRow) {
                    i = Theme.autoNightDayStartTime / 60;
                  }
                  for (int j = Theme.autoNightDayStartTime - i * 60;; j = Theme.autoNightDayEndTime - i * 60)
                  {
                    paramAnonymousView = (TextSettingsCell)paramAnonymousView;
                    paramAnonymousView = new TimePickerDialog(ThemeActivity.this.getParentActivity(), new TimePickerDialog.OnTimeSetListener()
                    {
                      public void onTimeSet(TimePicker paramAnonymous2TimePicker, int paramAnonymous2Int1, int paramAnonymous2Int2)
                      {
                        int i = paramAnonymous2Int1 * 60 + paramAnonymous2Int2;
                        if (paramAnonymousInt == ThemeActivity.this.scheduleFromRow)
                        {
                          Theme.autoNightDayStartTime = i;
                          paramAnonymousView.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format("%02d:%02d", new Object[] { Integer.valueOf(paramAnonymous2Int1), Integer.valueOf(paramAnonymous2Int2) }), true);
                        }
                        for (;;)
                        {
                          return;
                          Theme.autoNightDayEndTime = i;
                          paramAnonymousView.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format("%02d:%02d", new Object[] { Integer.valueOf(paramAnonymous2Int1), Integer.valueOf(paramAnonymous2Int2) }), true);
                        }
                      }
                    }, i, j, true);
                    ThemeActivity.this.showDialog(paramAnonymousView);
                    break;
                    i = Theme.autoNightDayEndTime / 60;
                  }
                }
              }
              else if (paramAnonymousInt == ThemeActivity.this.scheduleUpdateLocationRow) {
                ThemeActivity.this.updateSunTime(null, true);
              }
            }
          }
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", NUM));
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.locationPermissionGranted) {
      updateSunTime(null, true);
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, ThemeCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, 0, new Class[] { ThemeCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { ThemeCell.class }, new String[] { "checkImage" }, null, null, null, "featuredStickers_addedIcon"), new ThemeDescription(this.listView, 0, new Class[] { ThemeCell.class }, new String[] { "optionsButton" }, null, null, null, "stickers_menu"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"), new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[] { BrightnessControlCell.class }, new String[] { "leftImageView" }, null, null, null, "profile_actionIcon"), new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[] { BrightnessControlCell.class }, new String[] { "rightImageView" }, null, null, null, "profile_actionIcon"), new ThemeDescription(this.listView, 0, new Class[] { BrightnessControlCell.class }, new String[] { "seekBarView" }, null, null, null, "player_progressBackground"), new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[] { BrightnessControlCell.class }, new String[] { "seekBarView" }, null, null, null, "player_progress"), new ThemeDescription(this.listView, 0, new Class[] { ThemeTypeCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { ThemeTypeCell.class }, new String[] { "checkImage" }, null, null, null, "featuredStickers_addedIcon") };
  }
  
  public boolean onFragmentCreate()
  {
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    stopLocationUpdate();
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
    Theme.saveAutoNightThemeConfig();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  private class GpsLocationListener
    implements LocationListener
  {
    private GpsLocationListener() {}
    
    public void onLocationChanged(Location paramLocation)
    {
      if (paramLocation == null) {}
      for (;;)
      {
        return;
        ThemeActivity.this.stopLocationUpdate();
        ThemeActivity.this.updateSunTime(paramLocation, false);
      }
    }
    
    public void onProviderDisabled(String paramString) {}
    
    public void onProviderEnabled(String paramString) {}
    
    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
  }
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return ThemeActivity.this.rowCount;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt == ThemeActivity.this.newThemeRow) || (paramInt == ThemeActivity.this.nightThemeRow) || (paramInt == ThemeActivity.this.scheduleFromRow) || (paramInt == ThemeActivity.this.scheduleToRow) || (paramInt == ThemeActivity.this.scheduleUpdateLocationRow)) {
        paramInt = 1;
      }
      for (;;)
      {
        return paramInt;
        if ((paramInt == ThemeActivity.this.newThemeInfoRow) || (paramInt == ThemeActivity.this.automaticBrightnessInfoRow) || (paramInt == ThemeActivity.this.scheduleLocationInfoRow)) {
          paramInt = 2;
        } else if ((paramInt == ThemeActivity.this.themeInfoRow) || (paramInt == ThemeActivity.this.nightTypeInfoRow) || (paramInt == ThemeActivity.this.scheduleFromToInfoRow)) {
          paramInt = 3;
        } else if ((paramInt == ThemeActivity.this.nightDisabledRow) || (paramInt == ThemeActivity.this.nightScheduledRow) || (paramInt == ThemeActivity.this.nightAutomaticRow)) {
          paramInt = 4;
        } else if ((paramInt == ThemeActivity.this.scheduleHeaderRow) || (paramInt == ThemeActivity.this.automaticHeaderRow) || (paramInt == ThemeActivity.this.preferedHeaderRow)) {
          paramInt = 5;
        } else if (paramInt == ThemeActivity.this.automaticBrightnessRow) {
          paramInt = 6;
        } else if (paramInt == ThemeActivity.this.scheduleLocationRow) {
          paramInt = 7;
        } else {
          paramInt = 0;
        }
      }
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool1 = true;
      int i = paramViewHolder.getItemViewType();
      boolean bool2 = bool1;
      if (i != 0)
      {
        bool2 = bool1;
        if (i != 1)
        {
          bool2 = bool1;
          if (i != 4) {
            if (i != 7) {
              break label42;
            }
          }
        }
      }
      label42:
      for (bool2 = bool1;; bool2 = false) {
        return bool2;
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = true;
      boolean bool4 = true;
      switch (paramViewHolder.getItemViewType())
      {
      }
      for (;;)
      {
        return;
        paramInt -= ThemeActivity.this.themeStartRow;
        Object localObject = (Theme.ThemeInfo)Theme.themes.get(paramInt);
        paramViewHolder = (ThemeCell)paramViewHolder.itemView;
        if (paramInt != Theme.themes.size() - 1) {}
        for (;;)
        {
          paramViewHolder.setTheme((Theme.ThemeInfo)localObject, bool4);
          break;
          bool4 = false;
        }
        paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
        if (paramInt == ThemeActivity.this.newThemeRow)
        {
          paramViewHolder.setText(LocaleController.getString("CreateNewTheme", NUM), false);
        }
        else if (paramInt == ThemeActivity.this.nightThemeRow)
        {
          if ((Theme.selectedAutoNightType == 0) || (Theme.getCurrentNightTheme() == null)) {
            paramViewHolder.setText(LocaleController.getString("AutoNightTheme", NUM), true);
          } else {
            paramViewHolder.setTextAndValue(LocaleController.getString("AutoNightTheme", NUM), Theme.getCurrentNightThemeName(), true);
          }
        }
        else
        {
          int i;
          if (paramInt == ThemeActivity.this.scheduleFromRow)
          {
            i = Theme.autoNightDayStartTime / 60;
            paramInt = Theme.autoNightDayStartTime;
            paramViewHolder.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format("%02d:%02d", new Object[] { Integer.valueOf(i), Integer.valueOf(paramInt - i * 60) }), true);
          }
          else if (paramInt == ThemeActivity.this.scheduleToRow)
          {
            paramInt = Theme.autoNightDayEndTime / 60;
            i = Theme.autoNightDayEndTime;
            paramViewHolder.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format("%02d:%02d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i - paramInt * 60) }), false);
          }
          else if (paramInt == ThemeActivity.this.scheduleUpdateLocationRow)
          {
            paramViewHolder.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
            continue;
            paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
            if (paramInt == ThemeActivity.this.newThemeInfoRow)
            {
              paramViewHolder.setText(LocaleController.getString("CreateNewThemeInfo", NUM));
            }
            else if (paramInt == ThemeActivity.this.automaticBrightnessInfoRow)
            {
              paramViewHolder.setText(LocaleController.formatString("AutoNightBrightnessInfo", NUM, new Object[] { Integer.valueOf((int)(100.0F * Theme.autoNightBrighnessThreshold)) }));
            }
            else if (paramInt == ThemeActivity.this.scheduleLocationInfoRow)
            {
              paramViewHolder.setText(ThemeActivity.this.getLocationSunString());
              continue;
              paramViewHolder = (ThemeTypeCell)paramViewHolder.itemView;
              if (paramInt == ThemeActivity.this.nightDisabledRow)
              {
                localObject = LocaleController.getString("AutoNightDisabled", NUM);
                bool4 = bool2;
                if (Theme.selectedAutoNightType == 0) {
                  bool4 = true;
                }
                paramViewHolder.setValue((String)localObject, bool4, true);
              }
              else if (paramInt == ThemeActivity.this.nightScheduledRow)
              {
                localObject = LocaleController.getString("AutoNightScheduled", NUM);
                bool4 = bool1;
                if (Theme.selectedAutoNightType == 1) {
                  bool4 = true;
                }
                paramViewHolder.setValue((String)localObject, bool4, true);
              }
              else if (paramInt == ThemeActivity.this.nightAutomaticRow)
              {
                localObject = LocaleController.getString("AutoNightAutomatic", NUM);
                if (Theme.selectedAutoNightType == 2) {}
                for (bool4 = bool3;; bool4 = false)
                {
                  paramViewHolder.setValue((String)localObject, bool4, false);
                  break;
                }
                paramViewHolder = (HeaderCell)paramViewHolder.itemView;
                if (paramInt == ThemeActivity.this.scheduleHeaderRow)
                {
                  paramViewHolder.setText(LocaleController.getString("AutoNightSchedule", NUM));
                }
                else if (paramInt == ThemeActivity.this.automaticHeaderRow)
                {
                  paramViewHolder.setText(LocaleController.getString("AutoNightBrightness", NUM));
                }
                else if (paramInt == ThemeActivity.this.preferedHeaderRow)
                {
                  paramViewHolder.setText(LocaleController.getString("AutoNightPreferred", NUM));
                  continue;
                  ((BrightnessControlCell)paramViewHolder.itemView).setProgress(Theme.autoNightBrighnessThreshold);
                  continue;
                  paramViewHolder = (TextCheckCell)paramViewHolder.itemView;
                  if (paramInt == ThemeActivity.this.scheduleLocationRow) {
                    paramViewHolder.setTextAndCheck(LocaleController.getString("AutoNightLocation", NUM), Theme.autoNightScheduleByLocation, true);
                  }
                }
              }
            }
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      boolean bool = true;
      switch (paramInt)
      {
      default: 
        paramViewGroup = new TextCheckCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
      for (;;)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = this.mContext;
        if (ThemeActivity.this.currentType == 1) {}
        for (;;)
        {
          ThemeCell localThemeCell = new ThemeCell(paramViewGroup, bool);
          localThemeCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
          paramViewGroup = localThemeCell;
          if (ThemeActivity.this.currentType != 0) {
            break;
          }
          ((ThemeCell)localThemeCell).setOnOptionsClick(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              final Theme.ThemeInfo localThemeInfo = ((ThemeCell)paramAnonymousView.getParent()).getCurrentThemeInfo();
              if (ThemeActivity.this.getParentActivity() == null) {
                return;
              }
              BottomSheet.Builder localBuilder = new BottomSheet.Builder(ThemeActivity.this.getParentActivity());
              if (localThemeInfo.pathToFile == null)
              {
                paramAnonymousView = new CharSequence[1];
                paramAnonymousView[0] = LocaleController.getString("ShareFile", NUM);
              }
              for (;;)
              {
                localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
                {
                  /* Error */
                  public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                  {
                    // Byte code:
                    //   0: iload_2
                    //   1: ifne +448 -> 449
                    //   4: aload_0
                    //   5: getfield 28	org/telegram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
                    //   8: getfield 41	org/telegram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
                    //   11: ifnonnull +266 -> 277
                    //   14: aload_0
                    //   15: getfield 28	org/telegram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
                    //   18: getfield 44	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
                    //   21: ifnonnull +256 -> 277
                    //   24: new 46	java/lang/StringBuilder
                    //   27: dup
                    //   28: invokespecial 47	java/lang/StringBuilder:<init>	()V
                    //   31: astore_3
                    //   32: invokestatic 53	org/telegram/ui/ActionBar/Theme:getDefaultColors	()Ljava/util/HashMap;
                    //   35: invokevirtual 59	java/util/HashMap:entrySet	()Ljava/util/Set;
                    //   38: invokeinterface 65 1 0
                    //   43: astore 4
                    //   45: aload 4
                    //   47: invokeinterface 71 1 0
                    //   52: ifeq +50 -> 102
                    //   55: aload 4
                    //   57: invokeinterface 75 1 0
                    //   62: checkcast 77	java/util/Map$Entry
                    //   65: astore_1
                    //   66: aload_3
                    //   67: aload_1
                    //   68: invokeinterface 80 1 0
                    //   73: checkcast 82	java/lang/String
                    //   76: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                    //   79: ldc 88
                    //   81: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                    //   84: aload_1
                    //   85: invokeinterface 91 1 0
                    //   90: invokevirtual 94	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
                    //   93: ldc 96
                    //   95: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
                    //   98: pop
                    //   99: goto -54 -> 45
                    //   102: new 98	java/io/File
                    //   105: dup
                    //   106: invokestatic 104	org/telegram/messenger/ApplicationLoader:getFilesDirFixed	()Ljava/io/File;
                    //   109: ldc 106
                    //   111: invokespecial 109	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
                    //   114: astore 5
                    //   116: aconst_null
                    //   117: astore 6
                    //   119: aconst_null
                    //   120: astore 7
                    //   122: aload 6
                    //   124: astore_1
                    //   125: new 111	java/io/FileOutputStream
                    //   128: astore 4
                    //   130: aload 6
                    //   132: astore_1
                    //   133: aload 4
                    //   135: aload 5
                    //   137: invokespecial 114	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
                    //   140: aload 4
                    //   142: aload_3
                    //   143: invokevirtual 118	java/lang/StringBuilder:toString	()Ljava/lang/String;
                    //   146: invokevirtual 122	java/lang/String:getBytes	()[B
                    //   149: invokevirtual 126	java/io/FileOutputStream:write	([B)V
                    //   152: aload 4
                    //   154: ifnull +8 -> 162
                    //   157: aload 4
                    //   159: invokevirtual 129	java/io/FileOutputStream:close	()V
                    //   162: aload 5
                    //   164: astore_1
                    //   165: new 98	java/io/File
                    //   168: dup
                    //   169: iconst_4
                    //   170: invokestatic 135	org/telegram/messenger/FileLoader:getDirectory	(I)Ljava/io/File;
                    //   173: aload_1
                    //   174: invokevirtual 138	java/io/File:getName	()Ljava/lang/String;
                    //   177: invokespecial 109	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
                    //   180: astore 4
                    //   182: aload_1
                    //   183: aload 4
                    //   185: invokestatic 144	org/telegram/messenger/AndroidUtilities:copyFile	(Ljava/io/File;Ljava/io/File;)Z
                    //   188: istore 8
                    //   190: iload 8
                    //   192: ifne +127 -> 319
                    //   195: return
                    //   196: astore_1
                    //   197: aload_1
                    //   198: invokestatic 150	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
                    //   201: aload 5
                    //   203: astore_1
                    //   204: goto -39 -> 165
                    //   207: astore 6
                    //   209: aload 7
                    //   211: astore 4
                    //   213: aload 4
                    //   215: astore_1
                    //   216: aload 6
                    //   218: invokestatic 150	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
                    //   221: aload 5
                    //   223: astore_1
                    //   224: aload 4
                    //   226: ifnull -61 -> 165
                    //   229: aload 4
                    //   231: invokevirtual 129	java/io/FileOutputStream:close	()V
                    //   234: aload 5
                    //   236: astore_1
                    //   237: goto -72 -> 165
                    //   240: astore_1
                    //   241: aload_1
                    //   242: invokestatic 150	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
                    //   245: aload 5
                    //   247: astore_1
                    //   248: goto -83 -> 165
                    //   251: astore 4
                    //   253: aload_1
                    //   254: astore 6
                    //   256: aload 6
                    //   258: ifnull +8 -> 266
                    //   261: aload 6
                    //   263: invokevirtual 129	java/io/FileOutputStream:close	()V
                    //   266: aload 4
                    //   268: athrow
                    //   269: astore_1
                    //   270: aload_1
                    //   271: invokestatic 150	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
                    //   274: goto -8 -> 266
                    //   277: aload_0
                    //   278: getfield 28	org/telegram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
                    //   281: getfield 44	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
                    //   284: ifnull +17 -> 301
                    //   287: aload_0
                    //   288: getfield 28	org/telegram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
                    //   291: getfield 44	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
                    //   294: invokestatic 154	org/telegram/ui/ActionBar/Theme:getAssetFile	(Ljava/lang/String;)Ljava/io/File;
                    //   297: astore_1
                    //   298: goto -133 -> 165
                    //   301: new 98	java/io/File
                    //   304: dup
                    //   305: aload_0
                    //   306: getfield 28	org/telegram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
                    //   309: getfield 41	org/telegram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
                    //   312: invokespecial 157	java/io/File:<init>	(Ljava/lang/String;)V
                    //   315: astore_1
                    //   316: goto -151 -> 165
                    //   319: new 159	android/content/Intent
                    //   322: astore_1
                    //   323: aload_1
                    //   324: ldc -95
                    //   326: invokespecial 162	android/content/Intent:<init>	(Ljava/lang/String;)V
                    //   329: aload_1
                    //   330: ldc -92
                    //   332: invokevirtual 168	android/content/Intent:setType	(Ljava/lang/String;)Landroid/content/Intent;
                    //   335: pop
                    //   336: getstatic 174	android/os/Build$VERSION:SDK_INT	I
                    //   339: istore_2
                    //   340: iload_2
                    //   341: bipush 24
                    //   343: if_icmplt +91 -> 434
                    //   346: aload_1
                    //   347: ldc -80
                    //   349: aload_0
                    //   350: getfield 26	org/telegram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/telegram/ui/ThemeActivity$ListAdapter$1;
                    //   353: getfield 180	org/telegram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/telegram/ui/ThemeActivity$ListAdapter;
                    //   356: getfield 184	org/telegram/ui/ThemeActivity$ListAdapter:this$0	Lorg/telegram/ui/ThemeActivity;
                    //   359: invokevirtual 188	org/telegram/ui/ThemeActivity:getParentActivity	()Landroid/app/Activity;
                    //   362: ldc -66
                    //   364: aload 4
                    //   366: invokestatic 196	android/support/v4/content/FileProvider:getUriForFile	(Landroid/content/Context;Ljava/lang/String;Ljava/io/File;)Landroid/net/Uri;
                    //   369: invokevirtual 200	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
                    //   372: pop
                    //   373: aload_1
                    //   374: iconst_1
                    //   375: invokevirtual 204	android/content/Intent:setFlags	(I)Landroid/content/Intent;
                    //   378: pop
                    //   379: aload_0
                    //   380: getfield 26	org/telegram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/telegram/ui/ThemeActivity$ListAdapter$1;
                    //   383: getfield 180	org/telegram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/telegram/ui/ThemeActivity$ListAdapter;
                    //   386: getfield 184	org/telegram/ui/ThemeActivity$ListAdapter:this$0	Lorg/telegram/ui/ThemeActivity;
                    //   389: aload_1
                    //   390: ldc -50
                    //   392: ldc -49
                    //   394: invokestatic 213	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
                    //   397: invokestatic 217	android/content/Intent:createChooser	(Landroid/content/Intent;Ljava/lang/CharSequence;)Landroid/content/Intent;
                    //   400: sipush 500
                    //   403: invokevirtual 221	org/telegram/ui/ThemeActivity:startActivityForResult	(Landroid/content/Intent;I)V
                    //   406: goto -211 -> 195
                    //   409: astore_1
                    //   410: aload_1
                    //   411: invokestatic 150	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
                    //   414: goto -219 -> 195
                    //   417: astore 6
                    //   419: aload_1
                    //   420: ldc -80
                    //   422: aload 4
                    //   424: invokestatic 227	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
                    //   427: invokevirtual 200	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
                    //   430: pop
                    //   431: goto -52 -> 379
                    //   434: aload_1
                    //   435: ldc -80
                    //   437: aload 4
                    //   439: invokestatic 227	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
                    //   442: invokevirtual 200	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
                    //   445: pop
                    //   446: goto -67 -> 379
                    //   449: iload_2
                    //   450: iconst_1
                    //   451: if_icmpne +77 -> 528
                    //   454: aload_0
                    //   455: getfield 26	org/telegram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/telegram/ui/ThemeActivity$ListAdapter$1;
                    //   458: getfield 180	org/telegram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/telegram/ui/ThemeActivity$ListAdapter;
                    //   461: getfield 184	org/telegram/ui/ThemeActivity$ListAdapter:this$0	Lorg/telegram/ui/ThemeActivity;
                    //   464: invokestatic 231	org/telegram/ui/ThemeActivity:access$2000	(Lorg/telegram/ui/ThemeActivity;)Lorg/telegram/ui/ActionBar/ActionBarLayout;
                    //   467: ifnull -272 -> 195
                    //   470: aload_0
                    //   471: getfield 28	org/telegram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
                    //   474: invokestatic 235	org/telegram/ui/ActionBar/Theme:applyTheme	(Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;)V
                    //   477: aload_0
                    //   478: getfield 26	org/telegram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/telegram/ui/ThemeActivity$ListAdapter$1;
                    //   481: getfield 180	org/telegram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/telegram/ui/ThemeActivity$ListAdapter;
                    //   484: getfield 184	org/telegram/ui/ThemeActivity$ListAdapter:this$0	Lorg/telegram/ui/ThemeActivity;
                    //   487: invokestatic 238	org/telegram/ui/ThemeActivity:access$2100	(Lorg/telegram/ui/ThemeActivity;)Lorg/telegram/ui/ActionBar/ActionBarLayout;
                    //   490: iconst_1
                    //   491: iconst_1
                    //   492: invokevirtual 244	org/telegram/ui/ActionBar/ActionBarLayout:rebuildAllFragmentViews	(ZZ)V
                    //   495: new 246	org/telegram/ui/Components/ThemeEditorView
                    //   498: dup
                    //   499: invokespecial 247	org/telegram/ui/Components/ThemeEditorView:<init>	()V
                    //   502: aload_0
                    //   503: getfield 26	org/telegram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/telegram/ui/ThemeActivity$ListAdapter$1;
                    //   506: getfield 180	org/telegram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/telegram/ui/ThemeActivity$ListAdapter;
                    //   509: getfield 184	org/telegram/ui/ThemeActivity$ListAdapter:this$0	Lorg/telegram/ui/ThemeActivity;
                    //   512: invokevirtual 188	org/telegram/ui/ThemeActivity:getParentActivity	()Landroid/app/Activity;
                    //   515: aload_0
                    //   516: getfield 28	org/telegram/ui/ThemeActivity$ListAdapter$1$1:val$themeInfo	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
                    //   519: getfield 250	org/telegram/ui/ActionBar/Theme$ThemeInfo:name	Ljava/lang/String;
                    //   522: invokevirtual 254	org/telegram/ui/Components/ThemeEditorView:show	(Landroid/app/Activity;Ljava/lang/String;)V
                    //   525: goto -330 -> 195
                    //   528: aload_0
                    //   529: getfield 26	org/telegram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/telegram/ui/ThemeActivity$ListAdapter$1;
                    //   532: getfield 180	org/telegram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/telegram/ui/ThemeActivity$ListAdapter;
                    //   535: getfield 184	org/telegram/ui/ThemeActivity$ListAdapter:this$0	Lorg/telegram/ui/ThemeActivity;
                    //   538: invokevirtual 188	org/telegram/ui/ThemeActivity:getParentActivity	()Landroid/app/Activity;
                    //   541: ifnull -346 -> 195
                    //   544: new 256	org/telegram/ui/ActionBar/AlertDialog$Builder
                    //   547: dup
                    //   548: aload_0
                    //   549: getfield 26	org/telegram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/telegram/ui/ThemeActivity$ListAdapter$1;
                    //   552: getfield 180	org/telegram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/telegram/ui/ThemeActivity$ListAdapter;
                    //   555: getfield 184	org/telegram/ui/ThemeActivity$ListAdapter:this$0	Lorg/telegram/ui/ThemeActivity;
                    //   558: invokevirtual 188	org/telegram/ui/ThemeActivity:getParentActivity	()Landroid/app/Activity;
                    //   561: invokespecial 259	org/telegram/ui/ActionBar/AlertDialog$Builder:<init>	(Landroid/content/Context;)V
                    //   564: astore_1
                    //   565: aload_1
                    //   566: ldc_w 261
                    //   569: ldc_w 262
                    //   572: invokestatic 213	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
                    //   575: invokevirtual 266	org/telegram/ui/ActionBar/AlertDialog$Builder:setMessage	(Ljava/lang/CharSequence;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
                    //   578: pop
                    //   579: aload_1
                    //   580: ldc_w 268
                    //   583: ldc_w 269
                    //   586: invokestatic 213	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
                    //   589: invokevirtual 272	org/telegram/ui/ActionBar/AlertDialog$Builder:setTitle	(Ljava/lang/CharSequence;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
                    //   592: pop
                    //   593: aload_1
                    //   594: ldc_w 274
                    //   597: ldc_w 275
                    //   600: invokestatic 213	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
                    //   603: new 18	org/telegram/ui/ThemeActivity$ListAdapter$1$1$1
                    //   606: dup
                    //   607: aload_0
                    //   608: invokespecial 278	org/telegram/ui/ThemeActivity$ListAdapter$1$1$1:<init>	(Lorg/telegram/ui/ThemeActivity$ListAdapter$1$1;)V
                    //   611: invokevirtual 282	org/telegram/ui/ActionBar/AlertDialog$Builder:setPositiveButton	(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
                    //   614: pop
                    //   615: aload_1
                    //   616: ldc_w 284
                    //   619: ldc_w 285
                    //   622: invokestatic 213	org/telegram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
                    //   625: aconst_null
                    //   626: invokevirtual 288	org/telegram/ui/ActionBar/AlertDialog$Builder:setNegativeButton	(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Lorg/telegram/ui/ActionBar/AlertDialog$Builder;
                    //   629: pop
                    //   630: aload_0
                    //   631: getfield 26	org/telegram/ui/ThemeActivity$ListAdapter$1$1:this$2	Lorg/telegram/ui/ThemeActivity$ListAdapter$1;
                    //   634: getfield 180	org/telegram/ui/ThemeActivity$ListAdapter$1:this$1	Lorg/telegram/ui/ThemeActivity$ListAdapter;
                    //   637: getfield 184	org/telegram/ui/ThemeActivity$ListAdapter:this$0	Lorg/telegram/ui/ThemeActivity;
                    //   640: aload_1
                    //   641: invokevirtual 292	org/telegram/ui/ActionBar/AlertDialog$Builder:create	()Lorg/telegram/ui/ActionBar/AlertDialog;
                    //   644: invokevirtual 296	org/telegram/ui/ThemeActivity:showDialog	(Landroid/app/Dialog;)Landroid/app/Dialog;
                    //   647: pop
                    //   648: goto -453 -> 195
                    //   651: astore_1
                    //   652: aload 4
                    //   654: astore 6
                    //   656: aload_1
                    //   657: astore 4
                    //   659: goto -403 -> 256
                    //   662: astore 6
                    //   664: goto -451 -> 213
                    // Local variable table:
                    //   start	length	slot	name	signature
                    //   0	667	0	this	1
                    //   0	667	1	paramAnonymous2DialogInterface	DialogInterface
                    //   0	667	2	paramAnonymous2Int	int
                    //   31	112	3	localStringBuilder	StringBuilder
                    //   43	187	4	localObject1	Object
                    //   251	402	4	localFile1	java.io.File
                    //   657	1	4	localDialogInterface1	DialogInterface
                    //   114	132	5	localFile2	java.io.File
                    //   117	14	6	localObject2	Object
                    //   207	10	6	localException1	Exception
                    //   254	8	6	localDialogInterface2	DialogInterface
                    //   417	1	6	localException2	Exception
                    //   654	1	6	localFile3	java.io.File
                    //   662	1	6	localException3	Exception
                    //   120	90	7	localObject3	Object
                    //   188	3	8	bool	boolean
                    // Exception table:
                    //   from	to	target	type
                    //   157	162	196	java/lang/Exception
                    //   125	130	207	java/lang/Exception
                    //   133	140	207	java/lang/Exception
                    //   229	234	240	java/lang/Exception
                    //   125	130	251	finally
                    //   133	140	251	finally
                    //   216	221	251	finally
                    //   261	266	269	java/lang/Exception
                    //   182	190	409	java/lang/Exception
                    //   319	340	409	java/lang/Exception
                    //   379	406	409	java/lang/Exception
                    //   419	431	409	java/lang/Exception
                    //   434	446	409	java/lang/Exception
                    //   346	379	417	java/lang/Exception
                    //   140	152	651	finally
                    //   140	152	662	java/lang/Exception
                  }
                });
                ThemeActivity.this.showDialog(localBuilder.create());
                break;
                paramAnonymousView = new CharSequence[3];
                paramAnonymousView[0] = LocaleController.getString("ShareFile", NUM);
                paramAnonymousView[1] = LocaleController.getString("Edit", NUM);
                paramAnonymousView[2] = LocaleController.getString("Delete", NUM);
              }
            }
          });
          paramViewGroup = localThemeCell;
          break;
          bool = false;
        }
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
        continue;
        paramViewGroup = new ThemeTypeCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new BrightnessControlCell(this.mContext)
        {
          protected void didChangedValue(float paramAnonymousFloat)
          {
            int i = (int)(Theme.autoNightBrighnessThreshold * 100.0F);
            int j = (int)(paramAnonymousFloat * 100.0F);
            Theme.autoNightBrighnessThreshold = paramAnonymousFloat;
            if (i != j)
            {
              RecyclerListView.Holder localHolder = (RecyclerListView.Holder)ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.automaticBrightnessInfoRow);
              if (localHolder != null) {
                ((TextInfoPrivacyCell)localHolder.itemView).setText(LocaleController.formatString("AutoNightBrightnessInfo", NUM, new Object[] { Integer.valueOf((int)(Theme.autoNightBrighnessThreshold * 100.0F)) }));
              }
              Theme.checkAutoNightThemeConditions(true);
            }
          }
        };
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
    
    public void onViewAttachedToWindow(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getItemViewType();
      boolean bool;
      if (i == 4)
      {
        ThemeTypeCell localThemeTypeCell = (ThemeTypeCell)paramViewHolder.itemView;
        if (paramViewHolder.getAdapterPosition() == Theme.selectedAutoNightType)
        {
          bool = true;
          localThemeTypeCell.setTypeChecked(bool);
        }
      }
      for (;;)
      {
        if ((i != 2) && (i != 3)) {
          paramViewHolder.itemView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        return;
        bool = false;
        break;
        if (i == 0) {
          ((ThemeCell)paramViewHolder.itemView).updateCurrentThemeCheck();
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ThemeActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */