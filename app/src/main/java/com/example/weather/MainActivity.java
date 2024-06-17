package com.example.weather;

// Импорты необходимых классов и пакетов
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

// Класс MainActivity наследуется от AppCompatActivity, что позволяет использовать более современные функции и совместимость с предыдущими версиями Android
public class MainActivity extends AppCompatActivity {

    // Объявление переменных для элементов пользовательского интерфейса (TextView и Button)
    private TextView temperatureTextView;
    private TextView windSpeedTextView;
    private TextView maxTempTextView;
    private TextView minTempTextView;
    private TextView humidityTextView;
    private TextView weatherDescriptionTextView;
    private TextView errorTextView;
    private Button backButton;

    // Константы для базового URL API OpenWeatherMap и API ключа
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "ee9978598167a936d8ccf3e21b4aa2f1";
    // Объявление переменных для EditText (поле ввода города) и кнопки поиска
    private EditText cityEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов пользовательского интерфейса
        cityEditText = findViewById(R.id.city_edit_text);
        searchButton = findViewById(R.id.search_button);
        temperatureTextView = findViewById(R.id.temperature_text_view);
        windSpeedTextView = findViewById(R.id.wind_speed_text_view);
        maxTempTextView = findViewById(R.id.max_temp_text_view);
        minTempTextView = findViewById(R.id.min_temp_text_view);
        humidityTextView = findViewById(R.id.humidity_text_view);
        weatherDescriptionTextView = findViewById(R.id.weather_description_text_view);
        errorTextView = findViewById(R.id.error_text_view);
        backButton = findViewById(R.id.back_button); // Инициализация кнопки backButton
        // Обработчик события для кнопки "Вернуться к поиску города"
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Восстанавливаем начальное состояние экрана
                cityEditText.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.VISIBLE);
                temperatureTextView.setVisibility(View.GONE);
                windSpeedTextView.setVisibility(View.GONE);
                maxTempTextView.setVisibility(View.GONE);
                minTempTextView.setVisibility(View.GONE);
                humidityTextView.setVisibility(View.GONE);
                weatherDescriptionTextView.setVisibility(View.GONE);
                errorTextView.setVisibility(View.GONE);
                // Скрываем кнопку "Вернуться к поиску города"
                backButton.setVisibility(View.GONE);
            }
        });

        // Скрываем TextView и кнопку "Вернуться к поиску города" при запуске приложения
        temperatureTextView.setVisibility(View.GONE);
        windSpeedTextView.setVisibility(View.GONE);
        maxTempTextView.setVisibility(View.GONE);
        minTempTextView.setVisibility(View.GONE);
        humidityTextView.setVisibility(View.GONE);
        weatherDescriptionTextView.setVisibility(View.GONE);
        errorTextView.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        // Обработчик события для кнопки поиска
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String cityName = cityEditText.getText().toString().trim();
                if (cityName.isEmpty()) {
                    // Показываем сообщение, если поле ввода города пустое
                    Toast.makeText(MainActivity.this, "Вы не ввели город!", Toast.LENGTH_SHORT).show();
                } else {
                    // Скрываем поле ввода города и кнопку поиска
                    cityEditText.setVisibility(View.GONE);
                    searchButton.setVisibility(View.GONE);
                    // Вызываем метод для получения данных о погоде
                    fetchForecastData(cityName);
                }
            }
        });
    }
    // Метод для получения данных о погоде с помощью OpenWeatherMap API
    private void fetchForecastData(String cityName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Кодируем название города для использования в URL
                    String encodedCityName = URLEncoder.encode(cityName, "UTF-8");
                    // Формируем URL-строку для запроса к API OpenWeatherMap
                    String urlString = BASE_URL + "weather?q=" + encodedCityName + "&appid=" + API_KEY + "&units=metric";
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    // Получаем код ответа от сервера
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Если ответ успешный, читаем данные из входящего потока
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        String jsonString = stringBuilder.toString();
                        JSONObject jsonObject = new JSONObject(jsonString);

                        // Обновляем пользовательский интерфейс
                        updateUI(jsonObject);
                    } else {
                        // Если город не найден, показываем сообщение об ошибке и кнопку "Вернуться к поиску города"
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                errorTextView.setVisibility(View.VISIBLE);
                                errorTextView.setText("Город не найден. Попробуйте снова.");
                                backButton.setVisibility(View.VISIBLE); // Показываем кнопку "Вернуться к поиску города"
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    // Если произошла ошибка, показываем сообщение об ошибке и кнопку "Вернуться к поиску города"
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            errorTextView.setVisibility(View.VISIBLE);
                            errorTextView.setText("Ошибка получения данных. Попробуйте снова.");
                            backButton.setVisibility(View.VISIBLE); // Показываем кнопку "Вернуться к поиску города"
                        }
                    });
                }
            }
        }).start();
    }

    private void updateUI(JSONObject jsonObject) {
        try {
            // Получаем название города из JSON-объекта
            String cityName = jsonObject.getString("name");

            // Получаем необходимые данные из JSON-объекта
            double temperature = jsonObject.getJSONObject("main").getDouble("temp");
            double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
            double maxTemp = jsonObject.getJSONObject("main").getDouble("temp_max");
            double minTemp = jsonObject.getJSONObject("main").getDouble("temp_min");
            int humidity = jsonObject.getJSONObject("main").getInt("humidity");
            String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

            // Показываем оповещение о температуре
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (temperature >= 18) {
                        Toast.makeText(MainActivity.this, "На улице жарко. Бегом на шашлычки! ", Toast.LENGTH_SHORT).show();
                    } else if (temperature >= 10) {
                        Toast.makeText(MainActivity.this, "На улице умеренная температура. Как на счёт прогуляться?", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "На улице холодно. Одевайтесь тепло, и согревайтесь кофейком/чайком!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Обновляем текст в TextView
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Устанавливаем текст температуры в TextView с использованием формата строки
                    temperatureTextView.setText(String.format("Температура в %s: %.1f°C", cityName, temperature));

                    // Устанавливаем текст скорости ветра в TextView с использованием формата строки
                    windSpeedTextView.setText(String.format("Скорость ветра: %.1f м/с", windSpeed));

                    // Устанавливаем текст максимальной температуры в TextView с использованием формата строки
                    maxTempTextView.setText(String.format("Максимальная температура: %.1f°C", maxTemp));

                    // Устанавливаем текст минимальной температуры в TextView с использованием формата строки
                    minTempTextView.setText(String.format("Минимальная температура: %.1f°C", minTemp));

                    // Устанавливаем текст влажности в TextView с использованием формата строки
                    humidityTextView.setText(String.format("Влажность: %d%%", humidity));

                    // Устанавливаем текст описания погоды в TextView с использованием формата строки
                    weatherDescriptionTextView.setText(String.format("Описание: %s", weatherDescription));

                    // Делаем видимыми все TextView с информацией о погоде
                    temperatureTextView.setVisibility(View.VISIBLE);
                    windSpeedTextView.setVisibility(View.VISIBLE);
                    maxTempTextView.setVisibility(View.VISIBLE);
                    minTempTextView.setVisibility(View.VISIBLE);
                    humidityTextView.setVisibility(View.VISIBLE);
                    weatherDescriptionTextView.setVisibility(View.VISIBLE);

                    // Скрываем TextView с ошибкой
                    errorTextView.setVisibility(View.GONE);

                    // Делаем видимой кнопку "Вернуться к поиску города"
                    backButton.setVisibility(View.VISIBLE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}