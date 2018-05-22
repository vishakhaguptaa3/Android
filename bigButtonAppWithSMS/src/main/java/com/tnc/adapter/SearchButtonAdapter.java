package com.tnc.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tnc.R;
import com.tnc.bean.ContactTilesBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a3logics on 16/3/18.
 */

public class SearchButtonAdapter extends RecyclerView.Adapter<SearchButtonAdapter.MyViewHolder>
        implements Filterable {

    private Context context;
    private List<ContactTilesBean> listTiles;
    private List<ContactTilesBean> contactListFiltered;
    private ContactsAdapterListener listener;

    /**
     * Constaructor for an Adapter class
     * @param context
     * @param listTiles
     * @param listener
     */
    public SearchButtonAdapter(Context context, List<ContactTilesBean> listTiles, ContactsAdapterListener listener) {
        this.context             = context;
        this.listener            = listener;
        this.listTiles           = listTiles;
        this.contactListFiltered = listTiles;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_search_chat_button, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ContactTilesBean contact = contactListFiltered.get(position);
        holder.userName.setText(contact.getName());
        byte arrayImage[] = contact.getImage();
        if(arrayImage!=null && arrayImage.length>0){
            holder.userImage.setImageBitmap(BitmapFactory.decodeByteArray(arrayImage,0,arrayImage.length));
        }
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = listTiles;
                } else {
                    List<ContactTilesBean> filteredList = new ArrayList<>();
                    for (ContactTilesBean row : listTiles) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhoneNumber().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<ContactTilesBean>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public ImageView userImage;

        public MyViewHolder(View view) {
            super(view);
            userName  = view.findViewById(R.id.tvContactName);
            userImage = view.findViewById(R.id.imViewContactImage);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface ContactsAdapterListener {
        void onContactSelected(ContactTilesBean contact);
    }
}
