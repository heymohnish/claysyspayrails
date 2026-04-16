package coop.constellation.connectorservices.claysyspayrails.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.connectorservices.common.workflow.ConnectorResponse;
import com.xtensifi.connectorservices.common.workflow.ConnectorState;
import com.xtensifi.dspco.ConnectorMessage;

import coop.constellation.connectorservices.claysyspayrails.models.Transaction;
import coop.constellation.connectorservices.claysyspayrails.models.TransactionFilter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExternalCallMethodHandler extends HandlerBase implements ClaysysPayrailsHandlerLogic {

    private final ConnectorLogging logger;

    @Override
    public String generateResponse(final Map<String, String> parms, ConnectorState connectorState)
            throws IOException, ParseException {
        List<ConnectorResponse> connectorResponseList = connectorState.getConnectorResponseList().getResponses();

        // This is how you capture the response
        String resp = "{\"response\": 1}";
        for (ConnectorResponse connectorResponse : connectorResponseList) {

            // This is how you retrieve the name of the connector
            String name = connectorResponse.getConnectorRequestData().getConnectorName();
            logger.info(connectorState.getConnectorMessage(), name);

            // This is how you capture the response
            String data = connectorResponse.getResponse();

            // Parse the response how ever you see fit
            resp = "{\"response\": " + data + "}";
            logger.info(connectorState.getConnectorMessage(), resp);
        }

        // This is required, and is how you set the response for a workflow method
        connectorState.setResponse(resp);
        return resp;
    }

    @Override
    public String generateResponse(Map<String, String> parms, String userId, ConnectorMessage connectorMessage)
            throws IOException, ParseException {

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Transaction> transactions = readTransactionsFromFile();
            TransactionFilter filter = buildFilter(parms);
            List<Transaction> filtered = applyFilters(transactions, filter);
            filtered = applySorting(filtered, filter);
            filtered = applyPagination(filtered, filter);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("count", filtered.size());
            response.put("data", filtered);
            return mapper.writeValueAsString(response);

        } catch (IOException e) {
            throw new IOException("Failed to parse transaction JSON", e);

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while generating response", e);
        }
    }

    public List<Transaction> readTransactionsFromFile() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("transaction.json");

        if (inputStream == null) {
            throw new RuntimeException("transaction.json file not found in resources");
        }

        return mapper.readValue(inputStream, new TypeReference<List<Transaction>>() {
        });
    }

    public TransactionFilter buildFilter(Map<String, String> parms) {

        TransactionFilter filter = new TransactionFilter();

        if (parms == null || parms.isEmpty()) {
            return filter;
        }

        try {

            if (parms.get("fromDate") != null && !parms.get("fromDate").isBlank()) {
                filter.setFromDate(LocalDate.parse(parms.get("fromDate")));
            }

            if (parms.get("toDate") != null && !parms.get("toDate").isBlank()) {
                filter.setToDate(LocalDate.parse(parms.get("toDate")));
            }

            if (parms.get("minAmount") != null && !parms.get("minAmount").isBlank()) {
                filter.setMinAmount(new BigDecimal(parms.get("minAmount")));
            }

            if (parms.get("maxAmount") != null && !parms.get("maxAmount").isBlank()) {
                filter.setMaxAmount(new BigDecimal(parms.get("maxAmount")));
            }

            filter.setStatus(parms.get("status"));
            filter.setKeyword(parms.get("keyword"));
            filter.setSortBy(parms.get("sortBy"));
            filter.setSortOrder(parms.get("sortOrder"));

            if (parms.get("page") != null && !parms.get("page").isBlank()) {
                filter.setPage(Integer.parseInt(parms.get("page")));
            }

            if (parms.get("size") != null && !parms.get("size").isBlank()) {
                filter.setSize(Integer.parseInt(parms.get("size")));
            }

            return filter;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric filter value (page/size/minAmount/maxAmount)", e);

        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd", e);

        } catch (Exception e) {
            throw new RuntimeException("Error while building transaction filter", e);
        }
    }

    public List<Transaction> applyFilters(List<Transaction> transactions, TransactionFilter filter) {

        return transactions.stream()
                // Date filter
                .filter(t -> {
                    if (filter.getFromDate() == null && filter.getToDate() == null) {
                        return true;
                    }

                    LocalDate txnDate = OffsetDateTime.parse(t.getDateTimePosted()).toLocalDate();

                    if (filter.getFromDate() != null && txnDate.isBefore(filter.getFromDate())) {
                        return false;
                    }
                    if (filter.getToDate() != null && txnDate.isAfter(filter.getToDate())) {
                        return false;
                    }

                    return true;
                })
                // Amount filter
                .filter(t -> {
                    BigDecimal amount = new BigDecimal(t.getAmount().getValue());

                    if (filter.getMinAmount() != null && amount.compareTo(filter.getMinAmount()) < 0) {
                        return false;
                    }
                    if (filter.getMaxAmount() != null && amount.compareTo(filter.getMaxAmount()) > 0) {
                        return false;
                    }

                    return true;
                })
                // Status filter
                .filter(t -> {
                    if (filter.getStatus() == null || filter.getStatus().isBlank()) {
                        return true;
                    }
                    return t.getStatus().equalsIgnoreCase(filter.getStatus());
                })
                // Keyword filter
                .filter(t -> {
                    if (filter.getKeyword() == null || filter.getKeyword().isBlank()) {
                        return true;
                    }
                    return t.getDescription().toLowerCase().contains(filter.getKeyword().toLowerCase());
                })
                .collect(Collectors.toList());
    }

    public List<Transaction> applySorting(List<Transaction> list, TransactionFilter filter) {

        if (filter.getSortBy() == null) {
            return list;
        }

        Comparator<Transaction> comparator;

        switch (filter.getSortBy()) {

            case "amount":
                comparator = Comparator.comparing(t -> new BigDecimal(t.getAmount().getValue()));
                break;

            case "dateTimePosted":
                comparator = Comparator.comparing(t
                        -> OffsetDateTime.parse(t.getDateTimePosted()).toLocalDate());
                break;

            default:
                return list;
        }

        if ("desc".equalsIgnoreCase(filter.getSortOrder())) {
            comparator = comparator.reversed();
        }

        return list.stream().sorted(comparator).collect(Collectors.toList());
    }

    public List<Transaction> applyPagination(List<Transaction> list, TransactionFilter filter) {

        int page = filter.getPage();
        int size = filter.getSize();

        int start = (page - 1) * size;
        int end = Math.min(start + size, list.size());

        if (start >= list.size()) {
            return new ArrayList<>();
        }

        return list.subList(start, end);
    }
}
