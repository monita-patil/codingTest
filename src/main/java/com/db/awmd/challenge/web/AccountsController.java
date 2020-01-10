package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;

import java.math.BigDecimal;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
   // log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
   // log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }
 
  @PostMapping(path = "/depositAmount/{accountId}")
  public ResponseEntity<Object> depositAmount(@RequestBody Account account) {
	 try {
	     this.accountsService.depositAmount(account.getAccountId(), account.getBalance().intValue());
	 } catch(RuntimeException ex) {
		 return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	 }
	 return new ResponseEntity<>(HttpStatus.OK);
  }
  
  @PostMapping(path = "/withdrawAmount/{accountId}")
  public ResponseEntity<Object> withdrawAmount(@RequestBody String accountId, int balance) {
	  
	 try {
		this.accountsService.withdrawAmount(accountId, balance);
	 } catch (RuntimeException ex) {
		  return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	 }
	 return new ResponseEntity<>(HttpStatus.OK);
  }
  
  @GetMapping(path = "/checkBalance/{accountId}")
  public BigDecimal getBalance(@PathVariable String accountId) {
    return this.accountsService.checkBalance(accountId);
  }
  
  @PostMapping(path = "/transferAmount")
  public ResponseEntity<Object> transferAmount_FromAccountId_To_ToAccountId(@RequestBody Account account) throws Exception {
	 try {
		 this.accountsService.transferAmount_FromAccountId_To_ToAccountId(account.getAccountId(), 
			 account.getBalance().intValue(), account.getAccountId());
	 } catch (RuntimeException ex) {
	      return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	 return new ResponseEntity<>(HttpStatus.OK);
  } 

}
