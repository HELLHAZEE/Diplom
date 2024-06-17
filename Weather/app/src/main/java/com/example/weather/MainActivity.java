package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarException;

public class MainActivity extends AppCompatActivity {

    private TextView result_info;
    private TextView result_humidiat;
    private TextView result_pressure;
    private TextView result_wind, result_name;
    private EditText user_field;
    private Button main_btn, main_btn_return;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_field = findViewById(R.id.user_field);
        result_info = findViewById(R.id.result_info);
        result_humidiat = findViewById(R.id.result_humidiat);
        result_wind = findViewById(R.id.result_wind);
        result_pressure = findViewById(R.id.result_pressure);
        result_name = findViewById(R.id.result_name);
        main_btn = findViewById(R.id.main_btn);
        main_btn_return = findViewById(R.id.main_btn_return);
        main_btn_return.setVisibility(View.GONE);

        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user_field.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();
                }
                else {
                    String city = user_field.getText().toString();
                    String key = "3256d8a53d93811fc7342aa0d91609aa";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key;

                    new GetURLData().execute(url);
                }
            }
        });

        main_btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main_btn.setVisibility(View.VISIBLE);
                user_field.setVisibility(View.VISIBLE);
                main_btn_return.setVisibility(View.GONE);
                result_info.setVisibility(View.GONE);
                result_humidiat.setVisibility(View.GONE);
                result_pressure.setVisibility(View.GONE);
                result_wind.setVisibility(View.GONE);
                result_name.setVisibility(View.GONE);
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                main_btn.setVisibility(View.GONE);
                user_field.setVisibility(View.GONE);
                main_btn_return.setVisibility(View.VISIBLE);
                result_info.setVisibility(View.VISIBLE);
                result_humidiat.setVisibility(View.VISIBLE);
                result_pressure.setVisibility(View.VISIBLE);
                result_wind.setVisibility(View.VISIBLE);
                result_name.setVisibility(View.VISIBLE);
                result_name.setText(jsonObject.getString("name"));
                result_info.setText("Температура:\n" + jsonObject.getJSONObject("main").getDouble("temp"));
                result_humidiat.setText("Влажность:\n" + jsonObject.getJSONObject("main").getDouble("humidity"));
                result_wind.setText("Ветер:\n" + jsonObject.getJSONObject("wind").getDouble("speed"));
                result_pressure.setText("Давление:\n" + jsonObject.getJSONObject("main").getDouble("pressure"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
