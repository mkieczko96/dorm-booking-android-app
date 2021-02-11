package com.dorm.booker.api.data.models;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @Expose
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Expose
    @Column(name = "name")
    private String name;
}
