package com.gym;

import com.gym.entities.Rank;
import com.gym.enums.ERank;
import com.gym.repositories.RankRepository;
import com.gym.services.impl.RankServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RankServiceTest {

    @Mock
    private RankRepository rankRepository;

    @InjectMocks
    private RankServiceImpl rankService;

    @Test
    public void testGetRankByName() {
        // Given
        ERank expectedRankName = ERank.BRONZE;
        Rank bronzeRank = Rank.builder()
                .id(1L)
                .name(expectedRankName)
                .build();

        when(rankRepository.findByName(expectedRankName)).thenReturn(Optional.of(bronzeRank));

        // When
        Optional<Rank> result = rankService.getRankByName(expectedRankName);

        // Then
        assertEquals(expectedRankName, result.get().getName());
    }

    @Test
    public void testGetNonExistentRankByName() {
        // Given
        ERank nonExistentRankName = ERank.PLATINUM;

        // Configurar el comportamiento del mock para devolver un Optional vacío
        when(rankRepository.findByName(nonExistentRankName)).thenReturn(Optional.empty());

        // When
        Optional<Rank> result = rankService.getRankByName(nonExistentRankName);

        // Then
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testGetRankById() {
        // Given
        Long rankId = 1L;
        ERank expectedRankName = ERank.BRONZE;
        Rank bronzeRank = Rank.builder()
                .id(rankId)
                .name(expectedRankName)
                .build();

        when(rankRepository.findById(rankId)).thenReturn(Optional.of(bronzeRank));

        // When
        Optional<Rank> result = rankService.getRankById(rankId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedRankName, result.get().getName());
    }

    @Test
    public void testHandleException() {
        // Given
        ERank expectedRankName = ERank.BRONZE;

        // Configurar el comportamiento del mock para lanzar una excepción
        when(rankRepository.findByName(expectedRankName)).thenThrow(new RuntimeException("Error fetching rank"));

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> rankService.getRankByName(expectedRankName));
        assertEquals("Error fetching rank", exception.getMessage());
    }

}
