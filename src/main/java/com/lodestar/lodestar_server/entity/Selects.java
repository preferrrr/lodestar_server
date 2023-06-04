package com.lodestar.lodestar_server.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "selects")
public class Selects {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selects_id")
    Long id;

    @OneToOne
    @JoinColumn(name = "career_id", nullable = false)
    Career career;

    @Column(nullable = false)
    String selected;

}
