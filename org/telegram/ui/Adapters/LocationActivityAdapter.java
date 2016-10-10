package org.telegram.ui.Adapters;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;

public class LocationActivityAdapter
  extends BaseLocationAdapter
{
  private Location customLocation;
  private Location gpsLocation;
  private Context mContext;
  private int overScrollHeight;
  private SendLocationCell sendLocationCell;
  
  public LocationActivityAdapter(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private void updateCell()
  {
    if (this.sendLocationCell != null)
    {
      if (this.customLocation != null) {
        this.sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", 2131166244), String.format(Locale.US, "(%f,%f)", new Object[] { Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude()) }));
      }
    }
    else {
      return;
    }
    if (this.gpsLocation != null)
    {
      this.sendLocationCell.setText(LocaleController.getString("SendLocation", 2131166240), LocaleController.formatString("AccurateTo", 2131165207, new Object[] { LocaleController.formatPluralString("Meters", (int)this.gpsLocation.getAccuracy()) }));
      return;
    }
    this.sendLocationCell.setText(LocaleController.getString("SendLocation", 2131166240), LocaleController.getString("Loading", 2131165834));
  }
  
  public boolean areAllItemsEnabled()
  {
    return false;
  }
  
  public int getCount()
  {
    if ((this.searching) || ((!this.searching) && (this.places.isEmpty()))) {
      return 4;
    }
    int j = this.places.size();
    if (this.places.isEmpty()) {}
    for (int i = 0;; i = 1) {
      return i + (j + 3);
    }
  }
  
  public TLRPC.TL_messageMediaVenue getItem(int paramInt)
  {
    if ((paramInt > 2) && (paramInt < this.places.size() + 3)) {
      return (TLRPC.TL_messageMediaVenue)this.places.get(paramInt - 3);
    }
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    int i = 1;
    if (paramInt == 0) {
      i = 0;
    }
    while (paramInt == 1) {
      return i;
    }
    if (paramInt == 2) {
      return 2;
    }
    if ((this.searching) || ((!this.searching) && (this.places.isEmpty()))) {
      return 4;
    }
    if (paramInt == this.places.size() + 3) {
      return 5;
    }
    return 3;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    if (paramInt == 0)
    {
      paramViewGroup = paramView;
      if (paramView == null) {
        paramViewGroup = new EmptyCell(this.mContext);
      }
      ((EmptyCell)paramViewGroup).setHeight(this.overScrollHeight);
    }
    for (;;)
    {
      return paramViewGroup;
      if (paramInt == 1)
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new SendLocationCell(this.mContext);
        }
        this.sendLocationCell = ((SendLocationCell)paramViewGroup);
        updateCell();
        return paramViewGroup;
      }
      if (paramInt == 2)
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new GreySectionCell(this.mContext);
        }
        ((GreySectionCell)paramViewGroup).setText(LocaleController.getString("NearbyPlaces", 2131165909));
      }
      else if ((this.searching) || ((!this.searching) && (this.places.isEmpty())))
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new LocationLoadingCell(this.mContext);
        }
        ((LocationLoadingCell)paramViewGroup).setLoading(this.searching);
      }
      else if (paramInt == this.places.size() + 3)
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new LocationPoweredCell(this.mContext);
        }
      }
      else
      {
        paramViewGroup = paramView;
        if (paramView == null) {
          paramViewGroup = new LocationCell(this.mContext);
        }
        ((LocationCell)paramViewGroup).setLocation((TLRPC.TL_messageMediaVenue)this.places.get(paramInt - 3), (String)this.iconUrls.get(paramInt - 3), true);
      }
    }
  }
  
  public int getViewTypeCount()
  {
    return 6;
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  public boolean isEmpty()
  {
    return false;
  }
  
  public boolean isEnabled(int paramInt)
  {
    return (paramInt != 2) && (paramInt != 0) && ((paramInt != 3) || ((!this.searching) && ((this.searching) || (!this.places.isEmpty())))) && (paramInt != this.places.size() + 3);
  }
  
  public void setCustomLocation(Location paramLocation)
  {
    this.customLocation = paramLocation;
    updateCell();
  }
  
  public void setGpsLocation(Location paramLocation)
  {
    this.gpsLocation = paramLocation;
    updateCell();
  }
  
  public void setOverScrollHeight(int paramInt)
  {
    this.overScrollHeight = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/LocationActivityAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */