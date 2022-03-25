package com.rumesh.stockexchange.transaction;

import com.rumesh.stockexchange.company.Company;
import com.rumesh.stockexchange.company.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class TradeService {

    @Autowired
    private CompanyRepo companyRepo;

    @Transactional
    public ResponseEntity<HttpStatus> trade(Trade item) {
        try {
            Optional<Company> companyOptional = companyRepo.findCompanyBySymbol(item.getCompanySymbol());
            if (companyOptional.isPresent()) {
                Company company = companyOptional.get();
                Float currentShares = company.getTotalShares();

                if ("BUY".equals(item.getType())) {
                    currentShares = currentShares - item.getNumShares();
                }
                else if ("SELL".equals(item.getType())) {
                    currentShares = currentShares + item.getNumShares();
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

               company.setTotalShares(currentShares);
                companyRepo.save(company);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

