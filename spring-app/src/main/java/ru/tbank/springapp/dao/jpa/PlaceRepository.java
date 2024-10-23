package ru.tbank.springapp.dao.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tbank.springapp.model.entities.PlaceEntity;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {

    @Query("FROM PlaceEntity p JOIN FETCH p.events WHERE p.id = :id")
    Optional<PlaceEntity> findById(@Param("id") long id);

    Optional<PlaceEntity> findBySlug(String slug);

    @Query(value = "UPDATE places SET name = :name WHERE slug = :slug", nativeQuery = true)
    @Modifying
    int updateBySlug(
            @Param("slug") String slug,
            @Param("name") String name
    );

    int deleteBySlug(String slug);

}
