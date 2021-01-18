package com.ibring_driver.provider.food;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibring_driver.provider.Activity.Home;
import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Helper.SharedHelper;
import com.ibring_driver.provider.Models.FoodItemModel;
import com.ibring_driver.provider.Models.FoodModel;
import com.ibring_driver.provider.R;
import com.ibring_driver.provider.retrofit.RetrofitResponse;
import com.ibring_driver.provider.retrofit.RetrofitService;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class FoodServiceHistory extends AppCompatActivity implements RetrofitResponse
{
    RecyclerView recycler;
    TextView tvNoData;
    ImageView img_back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_service_history);
        findIds();
    }

    public void findIds()
    {
        recycler=(RecyclerView)findViewById(R.id.recycler);
        img_back=(ImageView) findViewById(R.id.img_back);
        tvNoData=(TextView) findViewById(R.id.tvNoData);

        img_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume()
    {
    super.onResume();
    if (SharedHelper.getKey(FoodServiceHistory .this,"service_types").equalsIgnoreCase("courier_service"))
    {

    }
    myorders();
    }

    public void setAdapter()
    {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        FoodOrderHistoryAdapter foodOrderHistoryAdapter=new FoodOrderHistoryAdapter(this,foodList);
        recycler.setAdapter(foodOrderHistoryAdapter);
    }

    public void myorders()
    {
        new RetrofitService(this, FoodServiceHistory.this, URLHelper.FOOD_HISTORY,
                500, 1,"1").callService(true);
    }

    public void myCourierorders()
    {
        new RetrofitService(this, FoodServiceHistory.this, URLHelper.FOOD_HISTORY,
                1500, 1,"1").callService(true);
    }

   /* @OnClick({R.id.img_back})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_back:
                Log.e("backClick ","backClick");
                onBackPressed();
                break;
        }
    }*/

    ArrayList<FoodModel> foodList=new ArrayList<>();

    @Override
    public void onResponse(int RequestCode, String response)
    {
        switch (RequestCode)
        {
            case 500:

                Log.e("Response ",response);

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success"))
                    {
                        tvNoData.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);
                        JSONArray result=jsonObject.getJSONArray("result");

                        if (result.length()>0)
                        {
                            foodList.clear();

                            for (int i = 0; i <result.length() ; i++)
                            {
                                FoodModel foodModel=new FoodModel();

                                JSONObject jsonObject1=result.getJSONObject(i);

                                foodModel.setId(jsonObject1.getString("id"));
                                foodModel.setRest_id(jsonObject1.getString("restaurant_id"));
                                foodModel.setDrop_address(jsonObject1.getString("location"));
                                foodModel.setDrop_lat(jsonObject1.getString("latitude"));
                                foodModel.setDrop_lng(jsonObject1.getString("longitude"));
                                foodModel.setPayment_mode(jsonObject1.getString("payment_mode"));
                                foodModel.setPrice(jsonObject1.getString("price"));
                                foodModel.setOrder_id(jsonObject1.getString("order_id"));
                                foodModel.setDelivery_boy_id(jsonObject1.getString("delivery_boy_id"));
                                foodModel.setStatus(jsonObject1.getString("status"));
                                foodModel.setDate_time(jsonObject1.getString("created_at"));

                                JSONObject restaurant=jsonObject1.getJSONObject("restaurant");
                                foodModel.setRest_name(restaurant.getString("title"));
                                foodModel.setRest_address(restaurant.getString("address"));
                                foodModel.setRest_image(restaurant.getString("image"));
                                foodModel.setRest_description(restaurant.getString("description"));
                                foodModel.setRest_lat(restaurant.getString("lattitude"));
                                foodModel.setRest_lng(restaurant.getString("longitude"));

                                JSONArray item=jsonObject1.getJSONArray("item");
                                ArrayList<FoodItemModel> list=new ArrayList<>();
                                if (item.length()>0)
                                {
                                    for (int j = 0; j < item.length(); j++)
                                    {
                                        JSONObject object=item.getJSONObject(j);
                                        FoodItemModel foodItemModel=new FoodItemModel();

                                        foodItemModel.setMenu_id(object.getString("menu_id"));
                                        foodItemModel.setQuantity(object.getString("quantity"));
                                        foodItemModel.setItemName(object.getString("item_name"));
                                        foodItemModel.setPrice(object.getString("price"));

                                        list.add(foodItemModel);
                                    }
                                }
                                foodModel.setList(list);
                                foodList.add(foodModel);
                            }

                            if (foodList.size()>0)
                            {
                                setAdapter();
                            }
                        }
                        else
                        {
                            tvNoData.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                        }


                    }
                    else
                    {
                        tvNoData.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                break;
        }
    }
}
