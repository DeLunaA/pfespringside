package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Demande} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.DemandeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /demandes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter sujet;

    private StringFilter description;

    private LongFilter userId;

    private Boolean distinct;

    public DemandeCriteria() {}

    public DemandeCriteria(DemandeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.sujet = other.sujet == null ? null : other.sujet.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DemandeCriteria copy() {
        return new DemandeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSujet() {
        return sujet;
    }

    public StringFilter sujet() {
        if (sujet == null) {
            sujet = new StringFilter();
        }
        return sujet;
    }

    public void setSujet(StringFilter sujet) {
        this.sujet = sujet;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DemandeCriteria that = (DemandeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(sujet, that.sujet) &&
            Objects.equals(description, that.description) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sujet, description, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (sujet != null ? "sujet=" + sujet + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
