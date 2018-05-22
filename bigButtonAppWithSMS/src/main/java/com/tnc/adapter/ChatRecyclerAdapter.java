package com.tnc.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.tnc.R;
import com.tnc.model.Chat;

import java.util.Collections;
import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 10/16/2016 , 10:36 AM
 * Project: FirebaseChat
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    private List<Chat> mChats;
    private Activity mContext;

    public ChatRecyclerAdapter(Activity mContext, List<Chat> chats) {
        mChats = chats;
        this.mContext = mContext;
    }

    public void add(Chat chat) {
        mChats.add(chat);
        notifyItemInserted(mChats.size() - 1);
    }

    public void update(Chat chat) {
        int inextUPDATED = Collections.binarySearch(mChats, chat);
        if (inextUPDATED >= 0) {
            ((Chat) mChats.get(inextUPDATED)).setPhotoURL_a(chat.getPhotoURL_a());
            notifyItemChanged(inextUPDATED);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new MyChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new OtherChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (TextUtils.equals(mChats.get(position).senderId,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureMyChatViewHolder((MyChatViewHolder) holder, position);
        } else {
            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
        }
    }

    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        String alphabet = chat.displayName.substring(0, 1);

        myChatViewHolder.txtUserAlphabet.setText(alphabet);

        try {
            if (chat.photoURL_a != null && !chat.photoURL_a.isEmpty()) {
                Glide.with(mContext).load(Uri.parse(chat.photoURL_a
                )).into(myChatViewHolder.mImagePosted);

                myChatViewHolder.mImagePosted.setVisibility(View.VISIBLE);
                myChatViewHolder.txtChatMessage.setVisibility(View.GONE);

            } else {
                myChatViewHolder.txtChatMessage.setText(chat.text);
                myChatViewHolder.mImagePosted.setVisibility(View.GONE);
                myChatViewHolder.txtChatMessage.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
        Chat chat = mChats.get(position);

        String alphabet = chat.displayName.substring(0, 1);

        otherChatViewHolder.txtUserAlphabet.setText(alphabet);
        if (chat.photoURL_a!=null && !chat.photoURL_a.isEmpty()){
            Glide.with(mContext).load(Uri.parse(chat.photoURL_a
            )).into(otherChatViewHolder.mImagePosted);

            otherChatViewHolder.mImagePosted.setVisibility(View.VISIBLE);
            otherChatViewHolder.txtChatMessage.setVisibility(View.GONE);

        }else {
            otherChatViewHolder.txtChatMessage.setText(chat.text);
            otherChatViewHolder.mImagePosted.setVisibility(View.GONE);
            otherChatViewHolder.txtChatMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mChats != null) {
            return mChats.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChats.get(position).senderId,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet;
        private ImageView mImagePosted;

        public MyChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
//            mImagePosted = (ImageView) itemView.findViewById(R.id.image_posted);
        }
    }

    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        private TextView txtChatMessage, txtUserAlphabet;
        private ImageView mImagePosted;

        public OtherChatViewHolder(View itemView) {
            super(itemView);
            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
//            mImagePosted = (ImageView) itemView.findViewById(R.id.image_posted);
        }
    }
}
