package com.kuklin.aiconversationservice.entities;

import com.kuklin.aiconversationservice.models.enums.ProviderVariant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "models")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProviderVariant provider;
    private String modelName;
    private String description;
    private BigDecimal inputMultiplier;
    private BigDecimal outputMultiplier;
    private BigDecimal cachedMultiplier;

    @UpdateTimestamp
    private OffsetDateTime updated;
    @CreationTimestamp
    private OffsetDateTime created;
}
