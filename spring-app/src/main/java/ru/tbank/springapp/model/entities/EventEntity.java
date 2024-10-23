package ru.tbank.springapp.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "events")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date")
    private LocalDate startDate;

    @Column(name = "name")
    private String name;

    @Column(name = "slug")
    private String slug;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "place_id", referencedColumnName = "id", nullable = false)
    private PlaceEntity place;

    @Override
    public String toString() {
        return "EventEntity{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }

}
