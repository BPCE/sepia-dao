package fr.groupebpce.sepia.dao.showcase.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Embeddable
@Getter
@Setter
public class Address {

    @Column(name = "STREET")
    private String street;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CITY")
    private City city;

    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "FK_COUNTRY")
    private Country country;

}
