package by.kos.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid={insert_key_here}}&lang=ru&units=metric";

    private EditText editTextCity;
    public TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextCity = findViewById(R.id.editTextCity);
        textViewWeather = findViewById(R.id.textViewWeather);

    }

    public void onClickShowWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        if(!city.isEmpty()){
            DownloadDataTask task = new DownloadDataTask();
            String url = String.format(WEATHER_URL, city);
            task.execute(url);
        }
    }


    class DownloadDataTask extends AsyncTask<String, Void, String> {

        private HttpsURLConnection urlConnection = null;
        public String result = null;


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject baseJSONObject = new JSONObject(s);
                JSONObject main = baseJSONObject.getJSONObject("main");
                JSONArray weather = baseJSONObject.getJSONArray("weather");
                JSONObject weather_desc = (JSONObject) weather.get(0);
                String city = baseJSONObject.getString("name");
                int temperature = Math.round( Float.parseFloat(main.getString("temp")));
                String humidity = main.getString("humidity");
                String description = weather_desc.getString("description");
                result = String.format("%s \nТемпература: %s \nВлажность: %s %% \nНа улице: %s",
                        city, temperature, humidity, description);
                textViewWeather.setText(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inReader = new InputStreamReader(in);
                BufferedReader bReader = new BufferedReader(inReader);
                String line = bReader.readLine();
                while (line != null){
                    stringBuilder.append(line);
                    line = bReader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
            }

            return stringBuilder.toString();


        }
    }
}