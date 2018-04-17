package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Components.RecyclerListView.Holder;

public class LocationActivitySearchAdapter extends BaseLocationAdapter {
    private Context mContext;

    public LocationActivitySearchAdapter(Context context) {
        this.mContext = context;
    }

    public int getItemCount() {
        return this.places.size();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(new LocationCell(this.mContext));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationCell locationCell = (LocationCell) holder.itemView;
        TL_messageMediaVenue tL_messageMediaVenue = (TL_messageMediaVenue) this.places.get(position);
        String str = (String) this.iconUrls.get(position);
        boolean z = true;
        if (position == this.places.size() - 1) {
            z = false;
        }
        locationCell.setLocation(tL_messageMediaVenue, str, z);
    }

    public TL_messageMediaVenue getItem(int i) {
        if (i < 0 || i >= this.places.size()) {
            return null;
        }
        return (TL_messageMediaVenue) this.places.get(i);
    }

    public boolean isEnabled(ViewHolder holder) {
        return true;
    }
}
