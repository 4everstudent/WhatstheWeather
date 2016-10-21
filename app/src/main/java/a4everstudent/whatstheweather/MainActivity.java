package a4everstudent.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

// TODO: 21-10-2016 dig more on the errors when inserting bad city names -> check the Toasts

public class MainActivity extends AppCompatActivity {

    TextView textViewWhatsTheCity;
    EditText insertCityView;
    Button whatsTheWeatherButton;
    TextView showWeatherView;
    String city ="";
    String link= "";

    String message = "";


    public void showWeather(View view) {


        //to hide the keyboard, so we can see the result
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(insertCityView.getWindowToken(), 0);


        try {
            city = insertCityView.getText().toString();

            //encoding cityName to handle cases like spaces between words
            String encodedCityName = URLEncoder.encode(city, "UTF-8");

            DownloadTask task = new DownloadTask();

            link = "http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&apikey=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
            task.execute(link);

        } catch (UnsupportedEncodingException e) {
            Log.i("", "Couldn't find weather! Tell my programmer it's the encoder, please!");
            Toast.makeText(getApplicationContext(), "Couldn't find weather! Tell my programmer it's the encoder, please!", Toast.LENGTH_LONG);
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        String result = "";
        URL url;
        HttpURLConnection urlConnection;

        //can't update UI here
        @Override
        protected String doInBackground(String... urls) {


            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;
            } catch (Exception e) {


                //Toast.makeText(getApplicationContext(), "Couldn't find weather!", Toast.LENGTH_LONG).show();
            }


            return null;
        }

        //we can update UI here
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    //shows the name of the city before the weather info (also useful to clear textView of older searches)
                    message = city + "\n";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (main != "" && description != "") {
                        message += main + ": " + description + "\r\n";
                    }

                }

                if (message != "") {
                    showWeatherView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "no message for you", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {

                Toast.makeText(getApplicationContext(), "JSONException ups :S ", Toast.LENGTH_LONG).show();

            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewWhatsTheCity = (TextView) findViewById(R.id.textViewEnterCity);
        insertCityView = (EditText) findViewById(R.id.editTextCity);
        whatsTheWeatherButton = (Button) findViewById(R.id.weatherButton);
        showWeatherView = (TextView) findViewById(R.id.showWeatherView);


    }
}
