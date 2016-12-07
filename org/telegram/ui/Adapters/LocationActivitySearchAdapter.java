package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Cells.LocationCell;

public class LocationActivitySearchAdapter extends BaseLocationAdapter {
    private Context mContext;

    public LocationActivitySearchAdapter(Context context) {
        this.mContext = context;
    }

    public int getCount() {
        return this.places.size();
    }

    public boolean isEmpty() {
        return this.places.isEmpty();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = new LocationCell(this.mContext);
        }
        ((LocationCell) view).setLocation((TL_messageMediaVenue) this.places.get(i), (String) this.iconUrls.get(i), i != this.places.size() + -1);
        return view;
    }

    public TL_messageMediaVenue getItem(int i) {
        if (i < 0 || i >= this.places.size()) {
            return null;
        }
        return (TL_messageMediaVenue) this.places.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public int getViewTypeCount() {
        return 4;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int position) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }
}
