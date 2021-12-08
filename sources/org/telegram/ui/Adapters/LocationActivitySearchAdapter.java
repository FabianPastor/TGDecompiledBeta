package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Components.RecyclerListView;

public class LocationActivitySearchAdapter extends BaseLocationAdapter {
    private Context mContext;

    public LocationActivitySearchAdapter(Context context) {
        this.mContext = context;
    }

    public int getItemCount() {
        return this.places.size();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerListView.Holder(new LocationCell(this.mContext, false, (Theme.ResourcesProvider) null));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LocationCell locationCell = (LocationCell) holder.itemView;
        TLRPC.TL_messageMediaVenue tL_messageMediaVenue = (TLRPC.TL_messageMediaVenue) this.places.get(position);
        String str = (String) this.iconUrls.get(position);
        boolean z = true;
        if (position == this.places.size() - 1) {
            z = false;
        }
        locationCell.setLocation(tL_messageMediaVenue, str, position, z);
    }

    public TLRPC.TL_messageMediaVenue getItem(int i) {
        if (i < 0 || i >= this.places.size()) {
            return null;
        }
        return (TLRPC.TL_messageMediaVenue) this.places.get(i);
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }
}
