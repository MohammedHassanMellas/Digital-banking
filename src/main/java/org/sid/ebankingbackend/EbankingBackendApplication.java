package org.sid.ebankingbackend;

import org.sid.ebankingbackend.dtos.BankAccountDTO;
import org.sid.ebankingbackend.dtos.CurrentBankAccountDTO;
import org.sid.ebankingbackend.dtos.CustomerDTO;
import org.sid.ebankingbackend.dtos.SavingBankAccountDTO;
import org.sid.ebankingbackend.entities.*;
import org.sid.ebankingbackend.enums.AccountStatus;
import org.sid.ebankingbackend.enums.OperationType;
import org.sid.ebankingbackend.exceptions.BalanceNotSufficientException;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;
import org.sid.ebankingbackend.repositories.AccountOperationRepository;
import org.sid.ebankingbackend.repositories.BankAccountRepository;
import org.sid.ebankingbackend.repositories.CustomerRepository;
import org.sid.ebankingbackend.services.BankAccountService;
import org.sid.ebankingbackend.services.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

  public static void main(String[] args) {

    SpringApplication.run(EbankingBackendApplication.class, args);
  }
  @Bean
  CommandLineRunner commandLineRunner(
    BankAccountService bankAccountService){
    return args -> {
 Stream.of("Hassan","Imane","Mohamed").forEach(name->{
   CustomerDTO customer=new CustomerDTO();
   customer.setName(name);
   customer.setEmail(name+"@gmail.com");
   bankAccountService.saveCustomer(customer);
   });
   bankAccountService.listCustomers().forEach(customer -> {
     try {
       bankAccountService.saveCurrentBankAccount(Math.random()*9000,9000, customer.getId());
       bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5, customer.getId());
     } catch (CustomerNotFoundException e) {
       e.printStackTrace();
     }
   });
      List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
      for (BankAccountDTO bankAccount:bankAccounts){
        for (int i = 0; i < 10; i++) {
          String accountId;
          if (bankAccount instanceof SavingBankAccountDTO){
            accountId=((SavingBankAccountDTO) bankAccount).getId();
          } else {
            accountId=((CurrentBankAccountDTO) bankAccount).getId();
          }
          bankAccountService.credit(accountId, 10000+Math.random()*12000,"Credit");
          bankAccountService.debit(accountId,1000+Math.random()*9000,"Debit");
        }
      }
    };
  }

  //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                        BankAccountRepository bankAccountRepository,
                        AccountOperationRepository accountOperationRepository){
    return args-> {
      Stream.of("MOHAMMED","KARIM","SALAH").forEach(name->{
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(name+"@gmail.com");
        customerRepository.save(c);
      });
      customerRepository.findAll().forEach(customer -> {
        CurrentAccount CA = new CurrentAccount();
        CA.setId(UUID.randomUUID().toString());
        CA.setBalance(Math.random()*90000);
        CA.setCreatedAt(new Date());
        CA.setStatus(AccountStatus.CREATED);
        CA.setCustomer(customer);
        CA.setOverDraft(9000);
        bankAccountRepository.save(CA);

          SavingAccount SA = new SavingAccount();
          SA.setId(UUID.randomUUID().toString());
          SA.setBalance(Math.random()*90000);
          SA.setCreatedAt(new Date());
          SA.setInterestRate(5.5);
          SA.setStatus(AccountStatus.CREATED);
          SA.setCustomer(customer);
          bankAccountRepository.save(SA);

      });
      bankAccountRepository.findAll().forEach(acc->{
        for (int i = 0; i<10 ; i++) {
          AccountOperation accountOperation = new AccountOperation();
          accountOperation.setOperationDate(new Date());
          accountOperation.setAmount(Math.random() * 12000);
          accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
          accountOperation.setBankAccount(acc);
        accountOperationRepository.save(accountOperation);
        }

      });
    };
}
}
