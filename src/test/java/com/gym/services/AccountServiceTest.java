package com.gym.services;

import com.gym.dto.AccountDTO;
import com.gym.dto.response.AccountDetailsDTO;
import com.gym.dto.response.AccountPurchaseDTO;
import com.gym.entities.Account;
import com.gym.entities.Rank;
import com.gym.entities.Subscription;
import com.gym.enums.ERank;
import com.gym.repositories.AccountRepository;
import com.gym.repositories.RankRepository;
import com.gym.security.entities.UserEntity;
import com.gym.services.SubscriptionService;
import com.gym.services.impl.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.gym.enums.ERank.BRONZE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private AccountService accountService;

    public AccountServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAccount() {
        UserEntity user = new UserEntity();
        String document = "123456789";

        RankRepository rankRepository = mock(RankRepository.class);

        AccountService accountService = new AccountService(accountRepository, rankRepository, subscriptionService);

        Rank rank = new Rank();
        rank.setName(BRONZE);
        when(rankRepository.findByName(BRONZE)).thenReturn(Optional.of(rank));

        Account createdAccount = accountService.createAccount(user, document);

        assertEquals(user, createdAccount.getUser());
        assertEquals(document, createdAccount.getDocument());
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(subscriptionService, times(1)).createSubscription(any(Account.class));
    }


    @Test
    public void testGetAccountDetails() {
        String username = "testUser";
        UserEntity user = new UserEntity();
        Account account = new Account();
        account.setId(1L);
        account.setUser(user);
        when(accountRepository.findByUserUsername(username)).thenReturn(Optional.of(account));

        AccountDTO accountDetails = accountService.getAccountDetails(username);

        assertEquals(account.getId(), accountDetails.getId());
        assertEquals(user.getUsername(), accountDetails.getUsername());
    }

    @Test
    public void testAccountDetailsById() {
        MockitoAnnotations.initMocks(this);

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testUser");

        Account account = new Account();
        account.setId(1L);
        account.setUser(user);
        account.setDocument("123456789");
        account.setCreditBalance(BigDecimal.valueOf(1000.0));
        Rank rank = new Rank();
        rank.setName(ERank.BRONZE);
        account.setRank(rank);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountDetailsDTO accountDetailsDTO = accountService.getAccountDetailsById(1L);

        assertEquals(1L, accountDetailsDTO.getId());
        assertEquals(1L, accountDetailsDTO.getUserId());
        assertEquals("123456789", accountDetailsDTO.getDocument());
        assertEquals(BigDecimal.valueOf(1000.0), accountDetailsDTO.getCreditBalance());
        assertEquals("BRONZE", accountDetailsDTO.getRank());
    }


    @Test
    public void testAccountById() {
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Account retrievedAccount = accountService.getAccountById(accountId);

        assertEquals(account, retrievedAccount);
    }

    @Test
    public void testGetAccountCreditBalance_AccountExists() {
        Long accountId = 123L;
        double expectedBalance = 1000.0;

        AccountService accountServiceMock = mock(AccountService.class);

        AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();
        accountDetailsDTO.setCreditBalance(BigDecimal.valueOf(expectedBalance));

        when(accountServiceMock.getAccountDetailsById(accountId)).thenReturn(accountDetailsDTO);

        BigDecimal actualBalance = accountServiceMock.getAccountDetailsById(accountId).getCreditBalance();

        assertEquals(BigDecimal.valueOf(expectedBalance), actualBalance);
    }

    @Test
    public void testGetAccountCreditBalance_AccountNotExists() {
        Long accountId = 123L;

        Account account = new Account();
        account.setId(accountId);
        when(accountRepository.existsById(accountId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.getAccountCreditBalance(account);
        });

        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    public void testSustractFromCreditBalance() {
        Long accountId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000.0);
        BigDecimal amountToSubtract = BigDecimal.valueOf(500.0);
        BigDecimal expectedBalanceAfterSubtraction = initialBalance.subtract(amountToSubtract);
        Account account = new Account();
        account.setId(accountId);
        account.setCreditBalance(initialBalance);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.sustractFromCreditBalance(account, amountToSubtract);

        assertEquals(expectedBalanceAfterSubtraction, account.getCreditBalance());
    }

    @Test
    public void testDeleteAccountByUserId() {
        Long userId = 1L;
        Long accountId = 2L;
        Account account = new Account();
        account.setId(accountId);

        when(accountRepository.findByUserId(userId)).thenReturn(Optional.of(account));

        Subscription subscription = new Subscription();
        subscription.setId(1L);
        when(subscriptionService.getSubscriptionByAccountId(accountId)).thenReturn(Optional.of(subscription));

        accountService.deleteAccountByUserId(userId);

        verify(subscriptionService, times(1)).getSubscriptionByAccountId(accountId);
        verify(subscriptionService, times(1)).deleteSubscriptionById(subscription.getId());
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    public void testUpdateSubscription() {
        Account account = new Account();

        accountService.updateSubscription(account);

        verify(accountRepository, times(1)).save(account);
    }

    /*@Test
    public void testAccountFromToken() {
        JwtUtils jwtUtilsMock = Mockito.mock(JwtUtils.class);
        when(jwtUtilsMock.getUsernameFromToken("validToken")).thenReturn("username");

        AccountService accountService = new AccountService(jwtUtilsMock);

        String username = String.valueOf(accountService.getAccountFromToken("validToken"));

        assertEquals("username", username);
    }*/

    @Test
    public void testGetAllAccountsWithPurchasesDTO() {
        Account account = new Account();
        account.setId(1L);
        account.setUser(new UserEntity());
        account.setCreditBalance(BigDecimal.valueOf(1000.0));
        account.setRank(null);

        when(accountRepository.findAll()).thenReturn(Collections.singletonList(account));

        List<AccountPurchaseDTO> accountPurchaseDTOList = accountService.getAllAccountsWithPurchasesDTO();

        assertEquals(1, accountPurchaseDTOList.size());

        assertNull(accountPurchaseDTOList.get(0).getRank());
    }
}



