package a4everstudent.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView textViewWhatsTheCity;
    EditText insertCityView;
    Button whatsTheWeatherButton;
    TextView showWeatherView;
    String city;
    String link;

    String message="";


    public void showWeather(View view){



        //to hide the keyboard, so we can see the result
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(insertCityView.getWindowToken(), 0);

        DownloadTask task = new DownloadTask();
        city = insertCityView.getText().toString();
        link = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&apikey="+ BuildConfig.OPEN_WEATHER_MAP_API_KEY;
        task.execute(link);

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        //can't update UI here
        @Override
        protected String doInBackground(String... urls) {

            String result ="";
            URL url;
            HttpURLConnection urlConnection;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){

                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;
            }
            catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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

                for(int i=0; i < arr.length(); i++){
                  JSONObject  jsonPart = arr.getJSONObject(i);

                    String main="";
                    String description = "";

                    //shows the name of the city before the weather info (also useful to clear textView of older searches)
                    message = city+"\n";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main != "" && description != ""){
                        message += main + ": " + description + "\r\n";
                    }

                }

                if (message != ""){
                    showWeatherView.setText(message);
                }

            }

            catch (JSONException e) {

                e.printStackTrace();

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
