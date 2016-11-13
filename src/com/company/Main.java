package com.company;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) {
        ArrayList<String> ListOfTowns = new ArrayList<String>(read("D:\\1/a.txt"));
        String URL;
        String JSON;
        for (int i = 0; i < ListOfTowns.size(); i++) {
            try {
                URL = URLMaker(ListOfTowns.get(i));
                JSON=getJSON(URL);
                JSONFilter(JSON);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }






    public static ArrayList< String> read(String URL) {

        File myFile=new File(URL);
        BufferedReader reader= null;
        try {
            reader = new BufferedReader(new FileReader(myFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        ArrayList<String> listOfTowns = new ArrayList<String>();
        try {
            while((line=reader.readLine())!=null)listOfTowns.add(line);
        } catch (IOException e) {
            System.out.println("Что-то сломалось ,сэр");
        }
        return listOfTowns;
    }

    public static String URLMaker(String Town) throws Exception {
        String URL = "https://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=";
        return URL+Town;
    }


    public static String getJSON(String url) {
        HttpURLConnection httpURLConnection = null;
        try {
            URL url1 = new URL(url);
            httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-length", "0");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setConnectTimeout(40000);
            httpURLConnection.setReadTimeout(40000);
            httpURLConnection.connect();
            int status = httpURLConnection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line+"\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
            }

        } catch (MalformedURLException ex) {
            System.out.println("URla сломалась ,сэр");
        } catch (IOException ex) {
            System.out.println("Что-то сломалось ,сэр");
        } finally {
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Exception ex) {
                    System.out.println("Дверь Потока сломалось ,сэр. не могу закрыть");
                }
            }
        }
        return null;
    }

    protected static void JSONFilter(String strJson) {
        JSONObject dataJsonObj = null;

        try {
            dataJsonObj = new JSONObject(strJson);
            JSONArray base = dataJsonObj.getJSONArray("results");
            String corTown ="Координаты города ";
            for (int i = 0; i < base.length(); i++) {
                JSONObject jsonObject = base.getJSONObject(i);

                JSONArray address_components = jsonObject.getJSONArray("address_components");
                JSONObject Town = address_components.getJSONObject(0);


                JSONObject geometry = jsonObject.getJSONObject("geometry");
                JSONObject viewport = geometry.getJSONObject("viewport");
                JSONObject northeast = viewport.getJSONObject("northeast");

                System.out.println(corTown+Town.get("long_name")+" в десятичных градусах");
                System.out.println("Широта:"+northeast.get("lat"));
                System.out.println("Долгота:"+northeast.get("lng"));
                JSONObject southwest = viewport.getJSONObject("southwest");

                System.out.println(corTown+Town.get("long_name")+" в градусах и десятичных минутах");
                System.out.println("Широта:"+southwest.get("lat"));
                System.out.println("Долгота:"+southwest.get("lng"));
                System.out.println();
            }

        } catch (JSONException e) {
            System.out.println("JSONa сломалась ,сэр");
        }

    }

}
