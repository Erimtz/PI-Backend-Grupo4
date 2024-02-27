package com.gym.dto;

import com.gym.entities.Product;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestImageDTO {

    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String url;
    @NotNull
    private Product product;
}
