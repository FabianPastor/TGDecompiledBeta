package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Components.RecyclerListView.Holder;

public class LocationActivitySearchAdapter extends BaseLocationAdapter {
    private Context mContext;

    public boolean isEnabled(ViewHolder viewHolder) {
        return true;
    }

    public LocationActivitySearchAdapter(Context context) {
        this.mContext = context;
    }

    public int getItemCount() {
        return this.places.size();
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new Holder(new LocationCell(this.mContext, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        LocationCell locationCell = (LocationCell) viewHolder.itemView;
        TL_messageMediaVenue tL_messageMediaVenue = (TL_messageMediaVenue) this.places.get(i);
        String str = (String) this.iconUrls.get(i);
        boolean z = true;
        if (i == this.places.size() - 1) {
            z = false;
        }
        locationCell.setLocation(tL_messageMediaVenue, str, i, z);
    }

    public TL_messageMediaVenue getItem(int i) {
        return (i < 0 || i >= this.places.size()) ? null : (TL_messageMediaVenue) this.places.get(i);
    }
}
