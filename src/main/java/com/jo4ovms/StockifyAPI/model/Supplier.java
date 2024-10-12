package com.jo4ovms.StockifyAPI.model;


import br.com.caelum.stella.bean.validation.CNPJ;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_supplier")
public class Supplier  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Size(max = 15)
    @Column(nullable = false)
    private String phone;

    @NotBlank
    @Size(max = 100)
    @Email
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false)
    private String productType;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    @CNPJ
    private String cnpj;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    private List<Product> products;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
