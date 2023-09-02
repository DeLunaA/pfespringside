package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Reclamation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Reclamation entity.
 */
@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    @Query("select reclamation from Reclamation reclamation where reclamation.user.login = ?#{principal.username}")
    List<Reclamation> findByUserIsCurrentUser();

    default Optional<Reclamation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Reclamation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Reclamation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select reclamation from Reclamation reclamation left join fetch reclamation.user",
        countQuery = "select count(reclamation) from Reclamation reclamation"
    )
    Page<Reclamation> findAllWithToOneRelationships(Pageable pageable);

    @Query("select reclamation from Reclamation reclamation left join fetch reclamation.user")
    List<Reclamation> findAllWithToOneRelationships();

    @Query("select reclamation from Reclamation reclamation left join fetch reclamation.user where reclamation.id =:id")
    Optional<Reclamation> findOneWithToOneRelationships(@Param("id") Long id);
}
