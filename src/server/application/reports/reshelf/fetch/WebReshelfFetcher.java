//package com.syos.application.reports.reshelf.fetch;
//
//import com.syos.application.reports.data.ReshelfReportDTO;
//import com.syos.data.dao.interfaces.WebInventoryDao;
//
//import java.util.List;
//
//public class WebReshelfFetcher implements ReshelfFetchStrategy {
//
//    private final WebInventoryDao webInventoryDao;
//
//    public WebReshelfFetcher(WebInventoryDao webInventoryDao) {
//        this.webInventoryDao = webInventoryDao;
//    }
//
//    @Override
//    public List<ReshelfReportDTO> fetch() {
//        return webInventoryDao.findReshelvedItems().stream()
//                .map(entry -> new ReshelfReportDTO(
//                        entry.getItemCode(),
//                        entry.getItemName(),
//                        "Web",
//                        entry.getQuantity(),
//                        entry.getMovedDate().toString()
//                ))
//                .toList();
//    }
//}