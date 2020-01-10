package com.db.awmd.challenge.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.locks.Lock;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  
  public boolean depositAmount(String accountId, int amount){
	  if(amount <= 0 ) {
		  throw new RuntimeException("Amount should be greater than 0");
	  }
      Account account = getAccount(accountId);
      Lock accountWriteLock = account.lock.writeLock();
      accountWriteLock.lock();
      account.addAmount(new BigDecimal(amount));
      accountWriteLock.unlock();
      return true;
  }
  
  public boolean withdrawAmount(String accountId, int amount){
	  if(amount <= 0) {
		  throw new RuntimeException("InSufficient Funds");
	  }
      Account account = getAccount(accountId);
      Lock accountWriteLock = account.lock.writeLock();
      accountWriteLock.lock();
      if(account.getBalance().intValue() < amount){
    	  accountWriteLock.unlock();
          return false;
      }
      account.subtractAmount(new BigDecimal(amount));
      accountWriteLock.unlock();
      return true;
  }
  
  public BigDecimal checkBalance(String accountId){
      Account account = getAccount(accountId);
      Lock accountReadLock = account.lock.readLock();
      accountReadLock.lock();
      BigDecimal balance = account.getBalance();
      accountReadLock.unlock();
      balance = balance.setScale(2, RoundingMode.HALF_EVEN);
      return balance;
  }
  
  public boolean transferAmount_FromAccountId_To_ToAccountId(String fromAccountId, int amount, String toAccountId) throws Exception {
     
	  if(fromAccountId == null && toAccountId == null) {
		  throw new RuntimeException("Invalid Request");
	  } 
	  if(fromAccountId.equals(toAccountId)) {
		  throw new RuntimeException("FromAccount id and ToAccount Id should not be same.");
	  }
	  if(amount <= 0) {
		  throw new RuntimeException("Amount should be greater than zero");
	  }
	  
	  Account fromAccount = getAccount(fromAccountId);
      Account toAccount = getAccount(toAccountId);
      Lock fromAccountTransferLock = fromAccount.transferLock;
      Lock toAccountTransferLock = toAccount.transferLock;
      
      while (true) {
          if (fromAccountTransferLock.tryLock()) {
              fromAccount.lock.writeLock();
              if (toAccountTransferLock.tryLock()) {
                  try {
                      withdrawAmount(fromAccountId, amount);
                      depositAmount(toAccountId, amount);
                      break;
                  } catch (Exception e) {
                      return false;
                  } finally {
                      toAccountTransferLock.unlock();
                  }
              }
              fromAccountTransferLock.unlock();
              Thread.sleep(1000);
          }
      }
	  
      return true;
  }
}
