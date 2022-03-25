package com.rumesh.stockexchange.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CompanyService {

    @Autowired
    private final CompanyRepo companyRepo;

    public CompanyService(CompanyRepo companyRepo) {

        this.companyRepo = companyRepo;
    }

    public List<Company> findAll() {

        return companyRepo.findAll();
    }

    public Object findById(Integer id) {
        return companyRepo.findCompanyById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company by id" + id + " was not found"));
    }

    public Company update(Company company) {
        return companyRepo.save(company);
    }

    public void deleteById(Integer id) {
        companyRepo.deleteById(Math.toIntExact(id));
    }

    public ResponseEntity<Company> findBySymbol(String symbol) {

        Optional<Company> items = companyRepo.findCompanyBySymbol(symbol);
        if (items.isPresent()) {
            return new ResponseEntity<>(items.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    public ResponseEntity<HttpStatus> updateSharePrice() {
        updateSharePriceEvent();
        return new ResponseEntity<>(HttpStatus.OK);

    }
    // method2 java.net.http.HttpClient
//    public void updateSharePriceEvent() throws Exception{
//        List<Company> companies = new ArrayList<>(companyRepo.findAll());
//        for (Company company : companies) {
//            String companySymbol = company.getSymbol();
//            {
//                String urlString = quoteURL(companySymbol);
//
//                HttpClient client = HttpClient.newHttpClient();
//                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
//                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                        .thenApply(HttpResponse::body)
//                        .thenApply(updateSharePrice()::parse)
//                        .join();
//            }
//            public static String parse(String responseBody) {
//                JSONArray quotes = new JSONArray(responseBody);
//             //   JSONObject quote = quotes.getJSONObject()
//                float quote
//            }
//        }
// }

    //    private String quoteURL(String companySymbol) {
//        return "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + companySymbol +
//                "&interval=15min&apikey=TDPJMOR8QZZJBTAY";
//    }
    public void updateSharePriceEvent() {
        List<Company> companies = new ArrayList<>(companyRepo.findAll());
        for (Company company : companies) {
            String companySymbol = company.getSymbol();
            {
                try {
                    String urlString = quoteURL(companySymbol);
                    String results = httpRequest(urlString);

                    JSONObject sharePriceData = getSharePriceData(results);
                    company.setPrice(Float.parseFloat(String.valueOf(sharePriceData.get("sharePrice"))));
                    company.setLastUpdated(String.valueOf(sharePriceData.get("date")));
                } catch (Exception err) {
                    System.out.println("Exception" + err);
                }
            }
            companyRepo.saveAll(companies);
        }

    }

    private String httpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connURL = (HttpURLConnection) url.openConnection();
        connURL.setRequestMethod("GET");
      //  System.out.println("\nMaking HTTP call: " + urlString + "\n");
        connURL.connect();

        BufferedReader ins = new BufferedReader(new InputStreamReader(connURL.getInputStream()));
        String inString;
        StringBuilder sb = new StringBuilder();
        while ((inString = ins.readLine()) != null) {
            sb.append(inString);
        }
        ins.close();
        connURL.disconnect();

     //   System.out.println("\nResponse: " + sb + "\n");
        return sb.toString();
    }

    private String quoteURL (String companySymbol){
        return "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + companySymbol +
                "&interval=5min&apikey=TDPJMOR8QZZJBTAY";
    }

    private JSONObject getSharePriceData(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        String date = (String) ((JSONObject) json.get("Meta Data")).get("3. Last Refreshed");
        JSONObject sharePrice = (JSONObject) ((JSONObject) json.get("Time Series (5min)")).get(date);

        JSONObject data = new JSONObject();
        data.put("date", date);
        data.put("sharePrice", sharePrice.get("1. open"));
        return data;
    }
}