package com.rumesh.stockexchange.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CompanyService {

    PrintWriter writerObj1 =null;

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
//
//            }
//        }
// }

   // String data = String.valueOf(alphaVantage.get("sharePrice"));

    public void updateSharePriceEvent() {

        List<Company> companies = new ArrayList<>(companyRepo.findAll());
        for (Company company : companies) {
            String companySymbol = company.getSymbol();
            {
                try {
                    String urlString = quoteURL(companySymbol);
                    String results = httpRequest(urlString);

                    JSONObject alphaVantage = getAlphavantageData(results);
                    company.setPrice(Float.parseFloat(String.valueOf(alphaVantage.get("sharePrice"))));
                    company.setLastUpdated(String.valueOf(alphaVantage.get("date")));

                    //write to text file
                    String price = String.valueOf(alphaVantage.get("sharePrice"));
                    String date = String.valueOf(alphaVantage.get("date"));
                  //  printTest(price, date);
                } catch (Exception err) {
                    System.out.println("Exception" + err);
                }
            }
            companyRepo.saveAll(companies);
            printTest(companies);
        }

    }

    private void printTest(List<Company> companies) {
        try {
            PrintWriter output = new PrintWriter("D:\\Projects\\output.txt");

            //output.printf("{"+"price:"+ price +"," + "date:"+ date +"}");
            output.printf(String.valueOf(companies));

            output.close();
        }
        catch(Exception e) {
            e.getStackTrace();
        }
    }

/*    private void printTest(String price, String date) {
        try {
            PrintWriter output = new PrintWriter("D:\\Projects\\output.txt");

            output.printf("{"+"price:"+ price +"," + "date:"+ date +"}");
            //output.printf(date);

            output.close();
        }
        catch(Exception e) {
            e.getStackTrace();
        }
    }

 */

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
                "&interval=5min&apikey=NRY4DZTJINT3LLUL"; //TDPJMOR8QZZJBTAY
    }

    private JSONObject getAlphavantageData(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        String date = (String) ((JSONObject) json.get("Meta Data")).get("3. Last Refreshed");
        String price = (String) ((JSONObject) json.get("Time Series (5min)")).get("4. close");
        JSONObject sharePrice = (JSONObject) ((JSONObject) json.get("Time Series (5min)")).get(date);
    System.out.println(price);
        JSONObject data = new JSONObject();

        data.put("date", date);
        data.put("sharePrice", sharePrice.get("4. close"));
        return data;

    }
}