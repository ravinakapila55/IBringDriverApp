package com.ibring_driver.provider.food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Models.FoodModel;
import com.ibring_driver.provider.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class FoodOrderHistoryAdapter extends RecyclerView.Adapter<FoodOrderHistoryAdapter.MyViewHolder>
{
    Context context;
    ArrayList<FoodModel> foodList;

    public FoodOrderHistoryAdapter(Context context, ArrayList<FoodModel> foodList)
    {
        this.context = context;
        this.foodList=foodList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(context).inflate(R.layout.custom_order_history,parent,false);
        return new FoodOrderHistoryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
            holder.tvName.setText(foodList.get(position).getRest_name());
            holder.tvOrderNumber.setText(foodList.get(position).getOrder_id());
            holder.tvAddress.setText(foodList.get(position).getRest_address());
            holder.tvPrice.setText("$ "+foodList.get(position).getPrice());


//            holder.tvPrice.setText(foodList.get(position).getList().size());
//            holder.tvDate.setText(foodList.get(position).getDate_time());

        String time="";
        Date date=null;
       SimpleDateFormat input=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
       SimpleDateFormat output=new SimpleDateFormat("MMM dd,yyyy");
//       SimpleDateFormat output=new SimpleDateFormat("hh:mm a");


                try
                {
                    date=input.parse(foodList.get(position).getDate_time());
                    time=output.format(date);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }


        holder.tvDate.setText(time);

        holder.tvStatus.setText(foodList.get(position).getStatus());


        Picasso.with(context).load(URLHelper.BASE_IMAGE_LOAD_URL_WITH_STORAGE+foodList.get(position).getRest_image()).
                placeholder(context.getResources().getDrawable(R.drawable.placeholder)).into(holder.ivOrder);

        if (foodList.get(position).getList().size()>0)
        {
            holder.recycler.setVisibility(View.VISIBLE);
            holder.recycler.setLayoutManager(new LinearLayoutManager(context));
            FoodItemsAdapter foodItemsAdapter=new FoodItemsAdapter(context,foodList.get(position).getList(),
                    FoodOrderHistoryAdapter.this);
            holder.recycler.setAdapter(foodItemsAdapter);
        }
        else
         {
            holder.recycler.setVisibility(View.GONE);
         }
    }

    @Override
    public int getItemCount()
    {
        return foodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ConstraintLayout cc;
        ImageView ivOrder;
        TextView tvName,tvAddress,tvPrice,tvItemsLabel,tvDateLabel,tvDate,tvStatus,tvView,tvOrderNumber;
        View viewww,viewww1;
        RecyclerView recycler;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            cc=(ConstraintLayout)itemView.findViewById(R.id.cc);
            ivOrder=(ImageView) itemView.findViewById(R.id.ivOrder);
            tvName=(TextView) itemView.findViewById(R.id.tvName);
            tvAddress=(TextView) itemView.findViewById(R.id.tvAddress);
            tvPrice=(TextView) itemView.findViewById(R.id.tvPrice);
            tvOrderNumber=(TextView) itemView.findViewById(R.id.tvOrderNumber);
            tvItemsLabel=(TextView) itemView.findViewById(R.id.tvItemsLabel);
            tvDateLabel=(TextView) itemView.findViewById(R.id.tvDateLabel);
            tvDate=(TextView) itemView.findViewById(R.id.tvDate);
            tvStatus=(TextView) itemView.findViewById(R.id.tvStatus);
            tvView=(TextView) itemView.findViewById(R.id.tvView);
            viewww1=(View) itemView.findViewById(R.id.viewww1);
            viewww=(View) itemView.findViewById(R.id.viewww);
            recycler=(RecyclerView) itemView.findViewById(R.id.recycler);

        }

    }

}
