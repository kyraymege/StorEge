package com.kyraymege.StorEge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kyraymege.StorEge.domain.RequestContext;
import com.kyraymege.StorEge.exceptions.APIException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
public abstract class Auditable {
    @Id
    @SequenceGenerator(name = "primary_key_sequence", sequenceName = "primary_key_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_key_sequence")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private String referenceId = new AlternativeJdkIdGenerator().generateId().toString();
    @NotNull
    private Long createdBy;
    @NotNull
    private Long updatedBy;
    @NotNull
    @CreatedDate
    @Column(name = "created_at" , nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @CreatedDate
    @Column(name = "updated_at" , nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        var userId = 0L; //RequestContext.getUserId();
//        if (userId == null) {throw new APIException("You must need user id to create a record.");}
        setCreatedBy(userId);
        setUpdatedBy(userId);
        setCreatedAt(now());
        setUpdatedAt(now());
    }

    @PreUpdate
    public void preUpdate() {
        var userId = 0L; //RequestContext.getUserId();
//        if (userId == null) {throw new APIException("You must need user id to update a record.");}
        setUpdatedBy(userId);
        setUpdatedAt(now());
    }
}


