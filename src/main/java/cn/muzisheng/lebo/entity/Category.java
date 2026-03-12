package cn.muzisheng.lebo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "category")
@Table(name = "category")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
}
