package com.rumesh.stockexchange.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/company") //port defined in application.properties(8081)
public class CompanyController {
    @Autowired // not necessary to define
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }
//get a list of all the companies
    @GetMapping("/get-all")
    public ResponseEntity<List<Company>> getAllCompany(){
        List<Company> companies = companyService.findAll();
        return new ResponseEntity<>(companies, HttpStatus.ACCEPTED);
    }
//get a particular company details using company id
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") Integer id){
        Company company = (Company) companyService.findById(id);
        return new ResponseEntity<>(company, HttpStatus.ACCEPTED);
    }

    //update details of an existing company
    @PutMapping("/update/{id}")
    public ResponseEntity<Company> updateCompany(@RequestBody Company company) {
        Company updateCompany = companyService.update(company);
        return new ResponseEntity<>(updateCompany, HttpStatus.OK);
    }
    //delete a company by id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Integer id) {
        companyService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    //getting the company by company symbol(for transaction table)
    @GetMapping("/get-by-symbol/{symbol}")
    public ResponseEntity<Company> getBySymbol(@PathVariable String symbol) {
    return companyService.findBySymbol(symbol);
    }
    //method to update share prices from alpha vantage
    @GetMapping("/update-share-price")
    public ResponseEntity<HttpStatus> updateSharePrice() throws Exception {
        return companyService.updateSharePrice();

    }

}