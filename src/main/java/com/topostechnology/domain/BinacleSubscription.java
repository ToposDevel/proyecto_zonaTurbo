package com.topostechnology.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "binacle_subscription")
@Getter
@Setter
public class BinacleSubscription {

    private static final long serialVersionUID = -3032596162471882160L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "internal_status", nullable = true)
    private String internalStatus;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "create_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BinacleSubscription)) return false;
        BinacleSubscription user = (BinacleSubscription) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

}
