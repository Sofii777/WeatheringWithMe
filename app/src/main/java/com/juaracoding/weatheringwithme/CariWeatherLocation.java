package com.juaracoding.weatheringwithme;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.juaracoding.weatheringwithme.model.WeatherModel;
import com.juaracoding.weatheringwithme.service.APIClient;
import com.juaracoding.weatheringwithme.service.APIInterfacesRest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CariWeatherLocation extends AppCompatActivity {
    EditText txtCariKota;
    Button btnSearch;
    RecyclerView lstCuaca;
    TextView txtKota, txtTemp, txtSunrise, txtSunset, txtPresure, txtClaudiness, txtHumadity, txtGeocords, txtWind;
    ImageView imgview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_weather_location);
        txtCariKota = findViewById(R.id.txtCariKota);
        btnSearch = findViewById(R.id.btnSearch);
        lstCuaca = findViewById(R.id.lstCuaca);
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

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callWeatherByCity(txtCariKota.getText().toString());
            }
        });
    }
    APIInterfacesRest apiInterface;
    ProgressDialog progressDialog;
    public void callWeatherByCity(final String kota){

        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(CariWeatherLocation.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();
        Call<WeatherModel> call3 = apiInterface.getWeatherByCity(kota,"6c57819f3114a6213bf6a1a0290c4f2c");
        call3.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                progressDialog.dismiss();
                WeatherModel dataWeather = response.body();

                if (dataWeather !=null) {

                    txtKota.setText(dataWeather.getName());
                    txtTemp.setText(new DecimalFormat("##.##").format(dataWeather.getMain().getTemp() - 273.15)+"° C");
                    //txtWind.setText(dataWeather.getWind().getDeg().toString()+"m/s");
                    txtWind.setText(dataWeather.getWind().getSpeed().toString()+" m/s");
                    txtHumadity.setText(dataWeather.getMain().getHumidity().toString()+"%");
                    txtPresure.setText(dataWeather.getMain().getPressure().toString()+" hpa");
                    txtGeocords.setText(dataWeather.getCoord().getLat().toString()+dataWeather.getCoord().getLon().toString());
                    txtClaudiness.setText(dataWeather.getClouds().getAll().toString());
//                    txtSunrise.setText(dataWeather.getSys().getSunrise().toString());
//                    txtSunset.setText(dataWeather.getSys().getSunset().toString());
                    txtSunrise.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dataWeather.getSys().getSunrise() * 1000)));
                    txtSunset.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(dataWeather.getSys().getSunset() * 1000)));



                    String image = "http://openweathermap.org/img/wn/"+ dataWeather.getWeather().get(0).getIcon()+"@2x.png";
                    Picasso.get().load(image).into(imgview);




                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(CariWeatherLocation.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(CariWeatherLocation.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Maaf koneksi bermasalah",Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });




    }
}
