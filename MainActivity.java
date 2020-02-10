package com.cole.weatherapp;

//Written By Cole Bligh

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Location Variables
    LocationManager locationManager;
    LocationListener locationListener;
    Location current;
    LatLng currentLatLng;
    boolean first = true;

    //Weather Variables
    TextView currentResultTextView;
    Map<String, String> countries;
    TextView cityTextView;
    ImageView weatherImageView;
    EditText zipcodeEditText;
    EditText countryEditText;
    LinearLayout hourlyWeather;

    public void dropDown(View view){
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(zipcodeEditText.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(countryEditText.getWindowToken(), 0);
    }

    public void findWeather(View view){
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(zipcodeEditText.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(countryEditText.getWindowToken(), 0);
        getFiveDay(zipcodeEditText.getText().toString(), countryEditText.getText().toString());
        zipcodeEditText.setText(null);
        countryEditText.setText(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getCountryCodes();
        hourlyWeather = (LinearLayout) findViewById(R.id.hourlyWeather);
        zipcodeEditText = (EditText) findViewById(R.id.zipcodeEditText);
        countryEditText = (EditText) findViewById(R.id.countryEditText);
        cityTextView = (TextView) findViewById(R.id.cityTextView);
        weatherImageView = (ImageView) findViewById(R.id.weatherImageView);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {


            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //if device is Running SDK <23
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            current = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            currentLatLng = new LatLng(current.getLatitude(), current.getLongitude());
        }
        getCurrentWeather();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAdresses = geocoder.getFromLocation(current.getLatitude(), current.getLongitude(), 1);
            String z = "";
            if (listAdresses.get(0).getPostalCode() != null && listAdresses.get(0).getCountryName() != null){
                getFiveDay(listAdresses.get(0).getPostalCode(), listAdresses.get(0).getCountryName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //Location Code
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }




    //Weather Code
    public void getCurrentWeather(){
        currentResultTextView = (TextView) findViewById(R.id.currentResultTextView);

        try {
            CurrentDownloadTask task = new CurrentDownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?lat=" + currentLatLng.latitude + "&lon=" + currentLatLng.longitude + "&units=imperial&APPID=86e93d414066a98c9e1495d885f628bd");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
        }
    }

    public void getCountryCodes(){
        countries = new HashMap<>();
        for(String iso : Locale.getISOCountries()){
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(), iso);
        }
    }

    public void getFiveDay(String zipcode, String countryName){
        if(countryName.equals("")){
            countryName = "United States";
        }
        String countryCode = countries.get(countryName);
        hourlyWeather.setTranslationY(-2000f);
        try {
            FiveDayDownloadTask task = new FiveDayDownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/forecast/?zip=" + zipcode + "," + countryCode + "&units=imperial&APPID=86e93d414066a98c9e1495d885f628bd");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
        }

        hourlyWeather.animate().translationYBy(2000f).setDuration(2000);
    }

    public void displayImage(String weather, ImageView setImage){
        if(weather.equals("Clouds")){
            setImage.setImageResource(R.drawable.clouds);
        } else if(weather.equals("Rain") || weather.equals("Drizzle")){
            setImage.setImageResource(R.drawable.rain);
        } else if(weather.equals("Snow")){
            setImage.setImageResource(R.drawable.snow);
        } else if(weather.equals("Thunderstorm")){
            setImage.setImageResource(R.drawable.thunderstorm);
        } else if(weather.equals("Clear")){
            setImage.setImageResource(R.drawable.clear);
        } else if(weather.equals("Mist")){
            setImage.setImageResource(R.drawable.mist);
        } else if(weather.equals("Smoke")){
            setImage.setImageResource(R.drawable.smoke);
        } else if(weather.equals("Haze") || weather.equals("Fog")){
            setImage.setImageResource(R.drawable.fog);
        } else if(weather.equals("Dust") || weather.equals("Sand")){
            setImage.setImageResource(R.drawable.dust);
        } else if(weather.equals("Ash")){
            setImage.setImageResource(R.drawable.ash);
        } else if(weather.equals("Tornado")){
            setImage.setImageResource(R.drawable.tornado);
        } else if(weather.equals("Squall")){
            setImage.setImageResource(R.drawable.squall);
        }
    }

    public class CurrentDownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection connection = null;

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //update the UI
            super.onPostExecute(result);

            try {
                String message = "Current Weather:" + "\r\n";
                JSONObject jsonObject = new JSONObject(result);

                //display temperature
                double temperature = getTemp(jsonObject);
                message += Double.toString(temperature) + "°F\r\n";
                if(temperature < 90) {
                    if(temperature > 80){
                        Toast.makeText(getApplicationContext(), "It is a great day for the beach", Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(getApplicationContext(), "Today's weather is bearable.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Today's weather is very hot!", Toast.LENGTH_LONG).show();
                }


                //get weather
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);

                JSONObject jsonPart = arr.getJSONObject(0);
                String main = "";
                String description = "";
                main = jsonPart.getString("main");
                description = jsonPart.getString("description");

                displayImage(main, weatherImageView);

                if (!description.equals("")) {
                    message += description + "\r\n";
                }


                if (!message.equals("")) {
                    currentResultTextView.setText(message);
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
            }

        }

        public double getTemp(JSONObject jsonObject){
            String temp = null;
            double temperature = 0.0;
            try {
                temp = jsonObject.getString("main");
                JSONObject obj = new JSONObject(temp);
                temperature = obj.getDouble("temp");
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find temperature", Toast.LENGTH_LONG).show();
            }
            return temperature;
        }
    }

    public class FiveDayDownloadTask extends AsyncTask<String, Void, String>{
        //present high/low and weather description

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection connection = null;

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //update the UI
            super.onPostExecute(result);

            try {
                System.out.println(result);
                JSONObject jsonObject = new JSONObject(result);
                String c = jsonObject.getString("city");
                String list = jsonObject.getString("list");

                //Displaying the city name
                JSONObject city = new JSONObject(c);
                String cityName = city.getString("name") + " Five Day Forecast:";
                cityTextView.setText(cityName);

                JSONArray arr = new JSONArray(list);
                for(int i = 4; i < arr.length(); i += 8){
                    String message ="";
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main ="";
                    String description="";

                    //get the date
                    String date = "";
                    date = jsonPart.getString("dt_txt");
                    date = date.substring(0, 10);
                    if(!date.equals("")) {
                        message += date + "\r\n";
                    }

                    //get the temperature
                    main = jsonPart.getString("main");
                    JSONObject jsonMain = new JSONObject(main);
                    double temperature = 0.0;
                    temperature = jsonMain.getDouble("temp");
                    if(temperature != 0.0) {
                        message += Double.toString(temperature) + "°F\r\n";
                    }

                    //get the weather
                    String weather = "";
                    String weatherInfo = jsonPart.getString("weather");
                    JSONArray array = new JSONArray(weatherInfo);
                    Log.i("Weather Info", weatherInfo.toString());
                    weather = array.getString(0);
                    JSONObject obj = new JSONObject(weather);
                    String imageInfo = "";
                    imageInfo = obj.getString("main");
                    description = obj.getString("description");
                    message += description;

                    //set display
                    if(i == 4){
                        ImageView day1ImageView = (ImageView) findViewById(R.id.day1ImageView);
                        displayImage(imageInfo, day1ImageView);
                        TextView resultTextView1 = (TextView) findViewById(R.id.day1TextView);
                        resultTextView1.setText(message);
                    } else if(i == 12){
                        ImageView day2ImageView = (ImageView) findViewById(R.id.day2ImageView);
                        displayImage(imageInfo, day2ImageView);
                        TextView resultTextView2 = (TextView) findViewById(R.id.day2TextView);
                        resultTextView2.setText(message);
                    } else if(i == 20){
                        ImageView day3ImageView = (ImageView) findViewById(R.id.day3ImageView);
                        displayImage(imageInfo, day3ImageView);
                        TextView resultTextView3 = (TextView) findViewById(R.id.day3TextView);
                        resultTextView3.setText(message);
                    } else if(i == 28){
                        ImageView day4ImageView = (ImageView) findViewById(R.id.day4ImageView);
                        displayImage(imageInfo, day4ImageView);
                        TextView resultTextView4 = (TextView) findViewById(R.id.day4TextView);
                        resultTextView4.setText(message);
                    } else if(i == 36) {
                        ImageView day5ImageView = (ImageView) findViewById(R.id.day5ImageView);
                        displayImage(imageInfo, day5ImageView);
                        TextView resultTextView5 = (TextView) findViewById(R.id.day5TextView);
                        resultTextView5.setText(message);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
            }
        }
    }
}