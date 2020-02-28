package com.juaracoding.weatheringwithme;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.juaracoding.weatheringwithme.adapter.AdapterListSimple;
import com.juaracoding.weatheringwithme.forecast.ForcastModel;
import com.juaracoding.weatheringwithme.service.APIClient;
import com.juaracoding.weatheringwithme.service.APIInterfacesRest;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CariForecastLocation extends AppCompatActivity {
    EditText txtKota;
    Button btnSearch;
    RecyclerView lstCuaca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_forecast_location);
        txtKota = findViewById(R.id.txtCariKota);
        btnSearch = findViewById(R.id.btnSearch);
        lstCuaca = findViewById(R.id.lstCuaca);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callForecastByCity(txtKota.getText().toString());
            }
        });

    }
    APIInterfacesRest apiInterface;
    ProgressDialog progressDialog;
    public void callForecastByCity(String kota){

        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(CariForecastLocation.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();
        Call<ForcastModel> call3 = apiInterface.getForcastByCity(kota,"6c57819f3114a6213bf6a1a0290c4f2c");
        call3.enqueue(new Callback<ForcastModel>() {
            @Override
            public void onResponse(Call<ForcastModel> call, Response<ForcastModel> response) {
                progressDialog.dismiss();
                ForcastModel dataWeather = response.body();
                //Toast.makeText(LoginActivity.this,userList.getToken().toString(),Toast.LENGTH_LONG).show();
                if (dataWeather !=null) {

                    //txtKota.setText(dataWeather.getName());
                    //txtTemperature.setText(new DecimalFormat("##.##").format(dataWeather.getMain().getTemp()-273.15));
                    AdapterListSimple adapter = new AdapterListSimple(CariForecastLocation.this,dataWeather.getList(),dataWeather.getCity().getName());
                    lstCuaca.setLayoutManager(new LinearLayoutManager(CariForecastLocation.this));
                    lstCuaca.setItemAnimator(new DefaultItemAnimator());
                    lstCuaca.setAdapter(adapter);




                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(CariForecastLocation.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(CariForecastLocation.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
