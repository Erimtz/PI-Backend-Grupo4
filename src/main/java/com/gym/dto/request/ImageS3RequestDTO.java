package com.gym.dto.request;

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
public class ImageS3RequestDTO {

    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String url;
    @NotNull
    private Long productId;
}
