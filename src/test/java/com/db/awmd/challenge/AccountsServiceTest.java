package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);
    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }
  
  

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }
  
  @Test
  public void deposit_failsOnZeroDepositAmount() throws Exception {
	Account account = new Account("Id-124", new BigDecimal(0));
	this.accountsService.createAccount(account);
    try {
      this.accountsService.depositAmount(account.getAccountId(), account.getBalance().intValue());
      fail("Deposit Amount should not be zero");
    } catch (RuntimeException ex) {
      assertThat(ex.getMessage()).isEqualTo("Amount should be greater than 0");
    }
  }
  
  @Test
  public void depositAmount() throws Exception {
    
	Account account = new Account("Id-125", new BigDecimal(1000));
	this.accountsService.createAccount(account);
	boolean isDeposit = this.accountsService.depositAmount(account.getAccountId(), account.getBalance().intValue());
    assertThat(true).isEqualTo(isDeposit);
  }
  
  @Test
  public void withdrawAmount_failsWhenInsufficientFunds() throws Exception {
	  Account account = new Account("Id-126", new BigDecimal(0));
		this.accountsService.createAccount(account);
	    try {
	      this.accountsService.withdrawAmount(account.getAccountId(), account.getBalance().intValue());
	      fail("InSufficient Funds");
	    } catch (RuntimeException ex) {
	      assertThat(ex.getMessage()).isEqualTo("InSufficient Funds");
	    } 
  }
  
  @Test
  public void withdrawAmount() throws Exception {
    
	Account account = new Account("Id-127", new BigDecimal(1000));
	this.accountsService.createAccount(account);
	boolean isWithdrawn = this.accountsService.withdrawAmount(account.getAccountId(), account.getBalance().intValue());
    assertThat(true).isEqualTo(isWithdrawn);
  }
  
  @Test
  public void getBalance() {
    
	Account account = new Account("Id-128", new BigDecimal(1000));
	this.accountsService.createAccount(account);
	BigDecimal balanceAmount = this.accountsService.checkBalance(account.getAccountId());
    assertThat(balanceAmount.doubleValue()).isEqualTo(1000.00);
  }
  
  @Test
  public void transferAmount_failsWhenFromIdAndToIdIsSame() throws Exception {
	  Account accountFromId = new Account("Id-129", new BigDecimal(1000));
	  Account accountToAccountId = new Account("Id-129", new BigDecimal(2000));
		this.accountsService.createAccount(accountFromId);
	    try {
	      this.accountsService.transferAmount_FromAccountId_To_ToAccountId(
	    		  accountFromId.getAccountId().toString(), 
	    		  accountFromId.getBalance().intValue(), 
	    		  accountToAccountId.getAccountId().toString());
	      fail("FromAccount id and ToAccount Id should not be same.");
	    } catch (RuntimeException ex) {
	      assertThat(ex.getMessage()).isEqualTo("FromAccount id and ToAccount Id should not be same.");
	    } 
  }
  
  @Test
  public void transferAmount_failsWhenInsufficientFunds() throws Exception {
	  Account accountFromId = new Account("Id-130", new BigDecimal(5000));
	  Account accountToAccountId = new Account("Id-131", new BigDecimal(2000));
		this.accountsService.createAccount(accountFromId);
		this.accountsService.createAccount(accountToAccountId);
	    
	     boolean isTransferred = this.accountsService.transferAmount_FromAccountId_To_ToAccountId(
	    		  accountFromId.getAccountId().toString(), 
	    		  accountFromId.getBalance().intValue(), 
	    		  accountToAccountId.getAccountId().toString());
	    	   
	      assertThat(true).isEqualTo(isTransferred); 
  } 
}
