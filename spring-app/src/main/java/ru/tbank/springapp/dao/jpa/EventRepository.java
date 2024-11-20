package ru.tbank.springapp.dao.jpa;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.springapp.model.entities.EventEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    static Specification<EventEntity> buildSpecification(String name, Long placeId, LocalDate fromDate, LocalDate toDate) {
        List<Specification<EventEntity>> specs = new ArrayList<>();

        if (name != null) {
            Specification<EventEntity> spec = (root, query, criteriaBuilder)
                    -> criteriaBuilder.equal(root.get("name").as(String.class), name);
            specs.add(spec);
        }

        if (placeId != null) {
            Specification<EventEntity> spec = (root, query, criteriaBuilder)
                    -> criteriaBuilder.equal(root.get("place").get("id").as(String.class), placeId);
            specs.add(spec);
        }

        if (fromDate != null) {
            Specification<EventEntity> spec = (root, query, criteriaBuilder)
                    -> criteriaBuilder.greaterThanOrEqualTo(root.get("startDate").as(LocalDate.class), fromDate);
            specs.add(spec);
        }

        if (toDate != null) {
            Specification<EventEntity> spec = (root, query, criteriaBuilder)
                    -> criteriaBuilder.lessThanOrEqualTo(root.get("startDate").as(LocalDate.class), toDate);
            specs.add(spec);
        }

        return specs.stream().reduce(Specification::and).orElse(null);
    }

    List<EventEntity> findAll(Specification<EventEntity> spec);

    @Query(value = "UPDATE events SET date = :date, name = :name, slug = :slug, place_id = :placeId WHERE id = :id", nativeQuery = true)
    @Modifying
    int updateById(
            long id,
            @Param("date") LocalDate date,
            @Param("name") String name,
            @Param("slug") String slug,
            @Param("placeId") Long placeId
    );

    // Need annotations, because default implementation does not return number of affected rows
    @Query(value = "DELETE FROM events WHERE id = :id", nativeQuery = true)
    @Modifying
    int deleteById(long id);

}
