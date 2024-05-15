package com.kyraymege.StorEge.entity.concretes;

import com.fasterxml.jackson.annotation.*;
import com.kyraymege.StorEge.domain.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static jakarta.persistence.FetchType.EAGER;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credentials")
@JsonInclude(NON_DEFAULT)
public class Credential extends Auditable {
    private String password;
    @OneToOne(targetEntity = User.class, fetch = EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private User user;

    public Credential(User user, String password) {
        this.user = user;
        this.password = password;
    }
}
