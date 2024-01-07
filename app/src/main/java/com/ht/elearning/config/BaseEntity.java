package com.ht.elearning.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@MappedSuperclass
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
@Data
public class BaseEntity {
    @CreationTimestamp
    @JsonProperty("created_at")
    private Date createdAt;

    @UpdateTimestamp
    @JsonProperty("updated_at")
    private Date updatedAt;
}
