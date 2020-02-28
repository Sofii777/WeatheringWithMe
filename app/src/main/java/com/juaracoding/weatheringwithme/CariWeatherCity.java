package com.juaracoding.weatheringwithme;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.juaracoding.weatheringwithme.model.WeatherModel;
import com.juaracoding.weatheringwithme.service.APIClient;
import com.juaracoding.weatheringwithme.service.APIInterfacesRest;
import com.robin.locationgetter.EasyLocation;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CariWeatherCity extends AppCompatActivity {
    TextView txtKota, txtTemp, txtSunrise, txtSunset, txtPresure, txtClaudiness, txtHumadity, txtGeocords, txtWind;
    ImageView imgview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_weather_city);

        txtKota = findViewById(R.id.isiKota);
        txtTemp = findViewById(R.id.isiTemp);
        txtClaudiness = findViewById(R.id.isiClaudiness);
        txtGeocords = findViewById(R.id.isiGeocords);
        txtHumadity = findViewById(R.id.isiHumadity);
        txtPresure = findViewById(R.id.isiPresure);
        txtSunrise = findViewById(R.id.isiSunrise);
        txtSunset = findViewById(R.id.isiSunset);
        txtWind = findViewById(R.id.isiWind);
        imgview = findViewById(R.id.imgview);
        new EasyLocation(CariWeatherCity.this, new EasyLocation.EasyLocationCallBack() {
            @Override
            public void permissionDenied() {

            }

            @Override
            public void locationSettingFailed() {

            }

            @Override
            public void getLocation(Location location) {

                callWeatherByCity(location.getLatitude(),location.getLongitude());
            }
        });


    }
    APIInterfacesRest apiInterface;
    ProgressDialog progressDialog;


    public void callWeatherByCity(Double lat, final Double lon) {

        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(CariWeatherCity.this);
        progressDialog.setTitle("Mohon Tunggu Sebentar");
     //   progressDialog.show();
        Call<WeatherModel> call3 = apiInterface.getWeatherBasedLocation(lat,lon, "6c57819f3114a6213bf6a1a0290c4f2c");
        call3.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel>call, Response<WeatherModel> response) {
                progressDialog.dismiss();
                WeatherModel dataWeather = response.body();
                //Toast.makeText(LoginActivity.this,userList.getToken().toString(),Toast.LENGTH_LONG).show();
                if (dataWeather != null) {

                    txtKota.setText(dataWeather.getName());
                    txtTemp.setText(new DecimalFormat("##.##").format(dataWeather.getMain().getTemp() - 273.15)+"° C");
                    //txtWind.setText(dataWeather.getWind().getDeg().toString()+"m/s");
                    txtWind.setText(dataWeather.getWind().getSpeed().toString()+" m/s");
                    txtHumadity.setText(dataWeather.getMain().getHumidity().toString()+"%");
                    txtPresure.setText(dataWeather.getMain().getPressure().toString()+" hpa");
                    txtGeocords.setText(dataWeather.getCoord().getLat().toString()+dataWeather.getCoord().getLon().toString());
                    txtClaudiness.setText(dataWeather.getClouds().getAll().toString());
                    txtSunrise.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dataWeather.getSys().getSunrise() * 1000)));
                    //txtSunrise.setText(dataWeather.getSys().getSunrise().toString());
                    txtSunset.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dataWeather.getSys().getSunset() * 1000)));
                    //txtSunset.setText(dataWeather.getSys().getSunset().toString());


                    String image = "http://openweathermap.org/img/wn/"+ dataWeather.getWeather().get(0).getIcon()+"@2x.png";
                    Picasso.get().load(image).into(imgview);


                } else {

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(CariWeatherCity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(CariWeatherCity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Yahh Koneksinya Bermasalah", Toast.LENGTH_LONG).show();
                call.cancel();

            }


        });


    }

}
