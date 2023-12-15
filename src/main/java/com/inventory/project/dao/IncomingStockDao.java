package com.inventory.project.dao;

import com.inventory.project.model.IncomingStock;
import com.inventory.project.model.ReportSearch;
import com.inventory.project.model.Search;

import java.util.List;

public interface IncomingStockDao {
    List<IncomingStock> search(Search search);
    Long searchCount(Search search);
    List<IncomingStock> reportSearch(ReportSearch search);

}
