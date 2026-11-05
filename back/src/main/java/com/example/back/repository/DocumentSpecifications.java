package com.example.back.repository;


import com.example.back.entities.DocsGetRequest;
import com.example.back.entities.Document;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class DocumentSpecifications {

    public static Specification<Document> withFilters(DocsGetRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.title() != null && !request.title().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + request.title().toLowerCase() + "%"
                ));
            }

            if (request.author() != null && !request.author().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("author")),
                        "%" + request.author().toLowerCase() + "%"
                ));
            }

            if (request.year() != null) {
                predicates.add(criteriaBuilder.equal(root.get("year"), request.year()));
            }

            if (request.description() != null && !request.description().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + request.description().toLowerCase() + "%"
                ));
            }

            if (request.type() != null && !request.type().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("type"), request.type()));
            }

            if (request.language() != null && !request.language().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("language"), request.language()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
