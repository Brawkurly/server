package com.brawkurly.moneytoring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/kamis")
public class KamisController {

    @GetMapping
    public String callApi() {
        StringBuffer result = new StringBuffer();
        try {
            String apiUrl = "http://www.kamis.co.kr/service/price/xml.do?action=dailySalesList&p_cert_key=4dbe24c1-28fe-4c88-aa03-9fe7c8f36a98&p_cert_id=heo3793&p_returntype=json";
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String returnLine;
            while((returnLine = bufferedReader.readLine()) != null) {
                result.append(returnLine + "\n");
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result + "";
    }
}
