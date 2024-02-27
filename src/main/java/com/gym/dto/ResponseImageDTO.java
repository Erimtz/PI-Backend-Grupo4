package com.gym.dto;

import com.gym.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseImageDTO {

    private Long id;
    private String title;
    private String url;
    private Long productId;
}
