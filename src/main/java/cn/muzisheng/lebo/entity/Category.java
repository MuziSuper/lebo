package cn.muzisheng.lebo.entity;

import jakarta.persistence.*;

@Entity(name = "category")
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
}
