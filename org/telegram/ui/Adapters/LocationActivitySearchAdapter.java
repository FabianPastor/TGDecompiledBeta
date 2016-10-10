package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Cells.LocationCell;

public class LocationActivitySearchAdapter
  extends BaseLocationAdapter
{
  private Context mContext;
  
  public LocationActivitySearchAdapter(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public boolean areAllItemsEnabled()
  {
    return false;
  }
  
  public int getCount()
  {
    return this.places.size();
  }
  
  public TLRPC.TL_messageMediaVenue getItem(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.places.size())) {
      return (TLRPC.TL_messageMediaVenue)this.places.get(paramInt);
    }
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    return 0;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    paramViewGroup = paramView;
    if (paramView == null) {
      paramViewGroup = new LocationCell(this.mContext);
    }
    paramView = (LocationCell)paramViewGroup;
    TLRPC.TL_messageMediaVenue localTL_messageMediaVenue = (TLRPC.TL_messageMediaVenue)this.places.get(paramInt);
    String str = (String)this.iconUrls.get(paramInt);
    if (paramInt != this.places.size() - 1) {}
    for (boolean bool = true;; bool = false)
    {
      paramView.setLocation(localTL_messageMediaVenue, str, bool);
      return paramViewGroup;
    }
  }
  
  public int getViewTypeCount()
  {
    return 4;
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  public boolean isEmpty()
  {
    return this.places.isEmpty();
  }
  
  public boolean isEnabled(int paramInt)
  {
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/LocationActivitySearchAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */