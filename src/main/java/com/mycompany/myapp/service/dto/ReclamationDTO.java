package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Reclamation} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReclamationDTO implements Serializable {

    private Long id;

    private Instant createdIn;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedIn() {
        return createdIn;
    }

    public void setCreatedIn(Instant createdIn) {
        this.createdIn = createdIn;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReclamationDTO)) {
            return false;
        }

        ReclamationDTO reclamationDTO = (ReclamationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, reclamationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReclamationDTO{" +
            "id=" + getId() +
            ", createdIn='" + getCreatedIn() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
