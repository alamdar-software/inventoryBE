package com.inventory.project.daoImpl;

import com.inventory.project.dao.IncomingStockDao;
import com.inventory.project.model.IncomingStock;
import com.inventory.project.model.ReportSearch;
import com.inventory.project.model.Search;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class IncomingStockdaoImpl implements IncomingStockDao {
    @Autowired
    EntityManager em;

    @Override
    public List<IncomingStock> search(Search search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IncomingStock> cq = cb.createQuery(IncomingStock.class);
        Root<IncomingStock> root = cq.from(IncomingStock.class);

        List<Predicate> predicates = getPredicates(root, cb, search);
        cq.select(root).where(cb.and(predicates.toArray(new Predicate[0] ))).distinct(true);

        Query query = em.createQuery(cq);

        return query.getResultList();
    }

    @Override
    public Long searchCount(Search search) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<IncomingStock> root = cq.from(IncomingStock.class);

        List<Predicate> predicates = getPredicates(root, cb, search);
        cq.select(cb.countDistinct(root)).where(predicates.toArray(new Predicate[0]));


        Query query = em.createQuery(cq);
        try {
            return (long) query.getSingleResult();
        } catch (Exception e) {

        }
        return (long) 0;
    }

    public List<Predicate> getPredicates(Root<IncomingStock> root, CriteriaBuilder cb, Search search) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        List<Predicate> predicates = new ArrayList<>();

        if (search.getLocationId() != null) {
            predicates.add(cb.equal(root.get("location").get("id"), search.getLocationId()));
        }

        if (search.getItemId() != null) {
            predicates.add(cb.equal(root.get("item").get("id"), search.getItemId()));
        }

        if (search.getDate() != null) {
            predicates.add(cb.equal(root.get("date"), search.getDate()));
        }

        if(search.getPurchaseOrder()!=null) {
            if(!search.getPurchaseOrder().isEmpty()) {
                predicates.add(cb.equal(root.get("purchaseOrder"), search.getPurchaseOrder()));
            }
        }
//        if(search.getEntityModel()!=null) {
//            predicates.add(cb.equal(root.get("entityModel"), search.getEntityModel()));
//        }
//

        return predicates;
    }

    @Override
    public List<IncomingStock> reportSearch(ReportSearch search) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IncomingStock> cq = cb.createQuery(IncomingStock.class);
        Root<IncomingStock> root = cq.from(IncomingStock.class);

        List<Predicate> predicates = getReportPredicates(root, cb, search);
        cq.select(root).where(cb.and(predicates.toArray(new Predicate[0] ))).distinct(true);

        Query query = em.createQuery(cq);

        return query.getResultList();
    }

    private List<Predicate> getReportPredicates(Root<IncomingStock> root, CriteriaBuilder cb, ReportSearch search) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        List<Predicate> predicates = new ArrayList<>();

        if (search.getLocation() != null) {
            predicates.add(cb.equal(root.get("location"), search.getLocation()));
        }

        if (search.getItem() != null) {
            predicates.add(cb.equal(root.get("item"), search.getItem()));
        }

        if(search.getFromDate()!= null && search.getToDate()==null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), search.getFromDate()));
        }

        if(search.getFromDate()== null && search.getToDate()!=null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("date"), search.getToDate()));
        }
        if(search.getStatus()!= null) {
            predicates.add(cb.equal(root.get("status"), search.getStatus()));
        }

        if(search.getFromDate()!= null && search.getToDate() !=null ) {

            Predicate date =  cb.between(root.get("date"), search.getFromDate(), search.getToDate());
            predicates.add(date);

        }
        if(search.getEntityModel()!=null) {
            predicates.add(cb.equal(root.get("entityModel"), search.getEntityModel()));
        }

        return predicates;
    }


}
