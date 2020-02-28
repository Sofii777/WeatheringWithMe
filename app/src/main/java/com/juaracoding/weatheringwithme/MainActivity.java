package com.juaracoding.weatheringwithme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView txtKota, txtTemp, txtSunrise, txtSunset, txtPresure, txtClaudiness, txtHumadity, txtGeocords, txtWind;
    ImageView imgview;
ImageButton liniLocation, liniCity, liniForcastLocation, liniForcastCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        liniLocation = findViewById(R.id.imgLocation);
        liniCity = findViewById(R.id.imgCity);
        liniForcastCity = findViewById(R.id.imgForcastCity);
        liniForcastLocation = findViewById(R.id.imgForcastLocation);
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
        new EasyLocation(MainActivity.this, new EasyLocation.EasyLocationCallBack() {
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

        liniCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CariWeatherLocation.class);
                startActivity(intent);
            }
        });

        liniLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CariWeatherCity.class);
                startActivity(intent);
            }
        });

        liniForcastCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CariForecastLocation.class);
                startActivity(intent);
            }
        });

        liniForcastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CariForecastCity.class);
                startActivity(intent);
            }
        });
    }
    APIInterfacesRest apiInterface;
    ProgressDialog progressDialog;


    public void callWeatherByCity(Double lat, final Double lon) {

        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Mohon Tunggu Sebentar");
        progressDialog.show();
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

                    String image = "http://openweathermap.org/img/wn/"+ dataWeather.getWeather().get(0).getIcon()+"@2x.png";
                    Picasso.get().load(image).into(imgview);


                } else {

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(MainActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
