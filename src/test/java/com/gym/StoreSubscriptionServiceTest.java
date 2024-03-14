package com.gym.services;

import com.gym.dto.response.StoreSubscriptionResponseDTO;
import com.gym.entities.StoreSubscription;
import com.gym.repositories.StoreSubscriptionRepository;
import com.gym.services.impl.StoreSubscriptionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreSubscriptionServiceTest {

    @Mock
    private StoreSubscriptionRepository storeSubscriptionRepository;

    @InjectMocks
    private StoreSubscriptionServiceImpl storeSubscriptionService;

    @Test
    public void testGetAllStoreSubscriptions() {
        // Given
        List<StoreSubscription> subscriptions = new ArrayList<>();
        StoreSubscription subscription1 = StoreSubscription.builder()
                .id(1L)
                .name("Subscription 1")
                .price(20.0)
                .description("This is subscription 1 description.")
                .imageUrl("https://example.com/image1.jpg")
                .planType("Premium")
                .durationDays(30)
                .build();

        StoreSubscription subscription2 = StoreSubscription.builder()
                .id(2L)
                .name("Subscription 2")
                .price(30.0)
                .description("This is subscription 2 description.")
                .imageUrl("https://example.com/image2.jpg")
                .planType("Standard")
                .durationDays(60)
                .build();


        subscriptions.add(subscription1);
        subscriptions.add(subscription2);
        // Mock behavior of repository
        when(storeSubscriptionRepository.findAll()).thenReturn(subscriptions);

        // When
        List<StoreSubscriptionResponseDTO> result = storeSubscriptionService.getAllStoreSubscriptions();

        // Then
        assertEquals(subscriptions.size(), result.size());

        // Verificar que los objetos devueltos no sean nulos
        for (StoreSubscriptionResponseDTO dto : result) {
            assertNotNull(dto.getId());
            assertNotNull(dto.getName());
            assertNotNull(dto.getPrice());
            assertNotNull(dto.getDescription());
            assertNotNull(dto.getImageUrl());
            assertNotNull(dto.getPlanType());
            assertNotNull(dto.getDurationDays());
        }

        // Verificar que el método findAll() del repositorio se llame exactamente una vez
        verify(storeSubscriptionRepository, times(1)).findAll();
    }
}
