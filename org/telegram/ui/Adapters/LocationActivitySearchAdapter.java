package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Components.RecyclerListView.Holder;

public class LocationActivitySearchAdapter
  extends BaseLocationAdapter
{
  private Context mContext;
  
  public LocationActivitySearchAdapter(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  public TLRPC.TL_messageMediaVenue getItem(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.places.size())) {}
    for (TLRPC.TL_messageMediaVenue localTL_messageMediaVenue = (TLRPC.TL_messageMediaVenue)this.places.get(paramInt);; localTL_messageMediaVenue = null) {
      return localTL_messageMediaVenue;
    }
  }
  
  public int getItemCount()
  {
    return this.places.size();
  }
  
  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    return true;
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    paramViewHolder = (LocationCell)paramViewHolder.itemView;
    TLRPC.TL_messageMediaVenue localTL_messageMediaVenue = (TLRPC.TL_messageMediaVenue)this.places.get(paramInt);
    String str = (String)this.iconUrls.get(paramInt);
    if (paramInt != this.places.size() - 1) {}
    for (boolean bool = true;; bool = false)
    {
      paramViewHolder.setLocation(localTL_messageMediaVenue, str, bool);
      return;
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    return new RecyclerListView.Holder(new LocationCell(this.mContext));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/LocationActivitySearchAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */