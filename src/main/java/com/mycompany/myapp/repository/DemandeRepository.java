package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Demande;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Demande entity.
 */
@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long>, JpaSpecificationExecutor<Demande> {
    @Query("select demande from Demande demande where demande.user.login = ?#{principal.username}")
    List<Demande> findByUserIsCurrentUser();

    default Optional<Demande> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Demande> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Demande> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select demande from Demande demande left join fetch demande.user",
        countQuery = "select count(demande) from Demande demande"
    )
    Page<Demande> findAllWithToOneRelationships(Pageable pageable);

    @Query("select demande from Demande demande left join fetch demande.user")
    List<Demande> findAllWithToOneRelationships();

    @Query("select demande from Demande demande left join fetch demande.user where demande.id =:id")
    Optional<Demande> findOneWithToOneRelationships(@Param("id") Long id);
}
