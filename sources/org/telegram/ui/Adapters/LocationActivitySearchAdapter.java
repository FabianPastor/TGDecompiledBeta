package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.RecyclerListView;

public class LocationActivitySearchAdapter extends BaseLocationAdapter {
    private FlickerLoadingView globalGradientView;
    private Context mContext;

    public LocationActivitySearchAdapter(Context context) {
        this.mContext = context;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.globalGradientView = flickerLoadingView;
        flickerLoadingView.setIsSingleCell(true);
    }

    public int getItemCount() {
        if (isSearching()) {
            return 3;
        }
        return this.places.size();
    }

    public boolean isEmpty() {
        return this.places.size() == 0;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerListView.Holder(new LocationCell(this.mContext, false, (Theme.ResourcesProvider) null));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TLRPC.TL_messageMediaVenue place = getItem(position);
        String iconUrl = (isSearching() || position < 0 || position >= this.iconUrls.size()) ? null : (String) this.iconUrls.get(position);
        LocationCell locationCell = (LocationCell) holder.itemView;
        boolean z = true;
        if (position == getItemCount() - 1) {
            z = false;
        }
        locationCell.setLocation(place, iconUrl, position, z);
    }

    public TLRPC.TL_messageMediaVenue getItem(int i) {
        if (!isSearching() && i >= 0 && i < this.places.size()) {
            return (TLRPC.TL_messageMediaVenue) this.places.get(i);
        }
        return null;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void notifyStartSearch(boolean wasSearching, int oldItemCount, boolean animated) {
        if (!wasSearching) {
            notifyDataSetChanged();
        }
    }
}
