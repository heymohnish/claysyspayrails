package coop.constellation.connectorservices.claysyspayrails.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
@Data
public class TransactionFilter {

    private LocalDate fromDate;
    private LocalDate toDate;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    private String status;
    private String keyword;

    private String sortBy;
    private String sortOrder;

    private int page = 1;
    private int size = 10;
}
