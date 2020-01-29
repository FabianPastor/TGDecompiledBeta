package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Components.RecyclerListView;

public class LocationActivitySearchAdapter extends BaseLocationAdapter {
    private Context mContext;

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    public LocationActivitySearchAdapter(Context context) {
        this.mContext = context;
    }

    public int getItemCount() {
        return this.places.size();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RecyclerListView.Holder(new LocationCell(this.mContext, false));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        LocationCell locationCell = (LocationCell) viewHolder.itemView;
        TLRPC.TL_messageMediaVenue tL_messageMediaVenue = this.places.get(i);
        String str = this.iconUrls.get(i);
        boolean z = true;
        if (i == this.places.size() - 1) {
            z = false;
        }
        locationCell.setLocation(tL_messageMediaVenue, str, i, z);
    }

    public TLRPC.TL_messageMediaVenue getItem(int i) {
        if (i < 0 || i >= this.places.size()) {
            return null;
        }
        return this.places.get(i);
    }
}
