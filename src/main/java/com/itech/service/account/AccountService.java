package com.itech.service.account;

import com.itech.model.dto.account.AccountCreateDto;
import com.itech.model.dto.account.AccountDto;
import com.itech.model.dto.account.AccountUpdateDto;
import com.itech.model.dto.request.CreationRequestDto;
import java.util.List;

/**
 * AccountService interface. Provides us different methods to work with Account objects on Service
 * layer.
 *
 * @author Edvard Krainiy on 12/18/2021
 */
public interface AccountService {

  /**
   * findAllAccounts method. Finds all accounts from DB.
   *
   * @return ResponseEntity<List < AccountDto>> ResponseEntity with HTTP code and list of all found
   *     accounts.
   */
  List<AccountDto> findAllAccounts();

  /**
   * findAccountByAccountId. Finds account by id.
   *
   * @param accountId Id of account we need to find.
   * @return ResponseEntity<AccountDto> ResponseEntity with HTTP code and found account entity.
   */
  AccountDto findAccountByAccountId(Long accountId);

  /**
   * createAccount method. Creates account from JSON object in RequestBody and saves into DB.
   *
   * @param accountChangeDto Account transfer object, which we need to save. This one will be
   *     converted into Account object, passed some checks and will be saved on DB.
   * @return ResponseEntity<Long> ResponseEntity with HTTP code and id of created account.
   */
  Long createAccount(AccountCreateDto accountChangeDto);

  /**
   * deleteAccountByAccountId method. Deletes account by id.
   *
   * @param accountId Id of account we need to delete.
   */
  void deleteAccountByAccountId(Long accountId);

  /**
   * updateAccount method. Updates account by id and accountUpdateDto entity.
   *
   * @param accountUpdateDto Account transfer object, which we need to update. This one will be
   *     converted into Account object, passed some checks and will be updated on DB.
   * @param accountId Id of account we need to update.
   */
  AccountDto updateAccount(AccountUpdateDto accountUpdateDto, Long accountId);

  /**
   * findAccountCreationRequestById method. Finds CreationRequest with ACCOUNT CreationType by id
   * and maps to Dto;
   *
   * @param creationRequestId Id of CreationRequest.
   * @return CreationRequestDto Dto of found CreationRequest object.
   */
  CreationRequestDto findAccountCreationRequestById(Long creationRequestId);

  /**
   * findAccountCreationRequests method. Finds all CreationRequests with ACCOUNT CreationType and
   * maps to Dto;
   *
   * @return List<CreationRequestDto> List of all CreationRequest objects.
   */
  List<CreationRequestDto> findAccountCreationRequests();

  /**
   * approveAccountCreationRequest method. Approves CreationRequest and creates account based on
   * payload of CreationRequest, sets CreationRequest status to CREATED, then send email message.
   *
   * @param accountCreationRequestId Id of CreationRequest we need to approve.
   */
  void approveAccountCreationRequest(Long accountCreationRequestId);

  /**
   * rejectAccountCreationRequest method. Rejects CreationRequest, sets CreationRequest status to
   * REJECTED, then send email message.
   *
   * @param accountCreationRequestId Id of CreationRequest we need to reject.
   */
  void rejectAccountCreationRequest(Long accountCreationRequestId);

  /**
   * checkExpiredAccountCreationRequests method. Marks Request, created more than 4 hours ago, as
   * EXPIRED.
   */
  void checkExpiredAccountCreationRequests();
}
