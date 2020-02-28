package com.juaracoding.weatheringwithme;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.juaracoding.weatheringwithme.adapter.AdapterListSimple;
import com.juaracoding.weatheringwithme.forecast.ForcastModel;
import com.juaracoding.weatheringwithme.service.APIClient;
import com.juaracoding.weatheringwithme.service.APIInterfacesRest;
import com.robin.locationgetter.EasyLocation;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CariForecastCity extends AppCompatActivity {
    RecyclerView lstForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_forecast_city);
        lstForecast =findViewById(R.id.lstForecast);

        new EasyLocation(CariForecastCity.this, new EasyLocation.EasyLocationCallBack() {
            @Override
            public void permissionDenied() {

            }

            @Override
            public void locationSettingFailed() {

            }

            @Override
            public void getLocation(Location location) {

                callWeatherBasedLocation(location.getLatitude(),location.getLongitude());
            }
        });
    }
    APIInterfacesRest apiInterface;
    ProgressDialog progressDialog;
    public void callWeatherBasedLocation(Double lat, Double lon ){

        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(CariForecastCity.this);
        progressDialog.setTitle("Loading");
//        progressDialog.show();
        Call<ForcastModel> call3 = apiInterface.getForecastBasedLocation(lat,lon,"6c57819f3114a6213bf6a1a0290c4f2c");
        call3.enqueue(new Callback<ForcastModel>() {
            @Override
            public void onResponse(Call<ForcastModel> call, Response<ForcastModel> response) {
                progressDialog.dismiss();
                ForcastModel dataWeather = response.body();

                if (dataWeather !=null) {



                    AdapterListSimple adapter = new AdapterListSimple(CariForecastCity.this,dataWeather.getList(),dataWeather.getCity().getName());

                    lstForecast.setLayoutManager(new LinearLayoutManager(CariForecastCity.this));
                    lstForecast.setItemAnimator(new DefaultItemAnimator());
                    lstForecast.setAdapter(adapter);




                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(CariForecastCity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(CariForecastCity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ForcastModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Maaf koneksi bermasalah",Toast.LENGTH_LONG).show();
                call.cancel();

            }


        });




    }
}
