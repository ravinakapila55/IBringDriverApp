package com.ibring_driver.provider.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ibring_driver.provider.R;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<ChatModel> list;

    public ChatAdapter(Context context, ArrayList<ChatModel> list)
    {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        Log.i("viewType", String.valueOf(viewType));
        boolean isCommentEnabled = false;
        if (viewType == ListType.SENDER_VIEW_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_chat_view_whatsapp, parent,
                    false);
            holder = new SendMessageViewHolder(view);
        } else if (viewType == ListType.RECEIVER_VIEW_TYPE)
        {
            //setMargins(mContext, 0,0,0,0);
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_chat_view_whatsapp, parent, false);
            holder = new ReceivedMessageViewHolder(view);
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position)
    {
        switch (list.get(position).getType()) {
            case 21:
                return ListType.SENDER_VIEW_TYPE;
            case 22:
                return ListType.RECEIVER_VIEW_TYPE;
            default:
                return -1;
        }
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder == null) return;
        if (holder instanceof SendMessageViewHolder) {

            ChatModel model = list.get(position);
            ((SendMessageViewHolder) holder).bind(model, position);
        }

        if (holder instanceof ReceivedMessageViewHolder) {

            ChatModel model = list.get(position);
            ((ReceivedMessageViewHolder) holder).bind(model, position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
