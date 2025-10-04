package com.manawallpapers.entity;

import jakarta.persistence.*;
        import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mini_sub_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiniSubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private SubCategory subCategory;

    @OneToMany(mappedBy = "miniSubCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Wallpaper> wallpapers;
}


