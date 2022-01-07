package com.itech.contoller;

import com.itech.model.dto.request.CreationRequestDto;
import com.itech.service.request.RequestService;
import com.itech.service.request.impl.RequestServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller with different endpoints to manipulate with CreationRequest objects.
 *
 * @author Edvard Krainiy on 01/05/2022
 */
@RestController
@Log4j2
@RequestMapping("api/transactions/creation-requests")
public class CreationRequestController {

    @Autowired
    private RequestService requestService;

    /**
     * getCreationRequestById endpoint. Returns CreationRequest by Id.
     *
     * @param creationRequestId Id of creationRequest we want to obtain.
     * @return CreationRequestDto Dto of obtained creationRequest.
     */

    @GetMapping("{id}")
    public ResponseEntity<CreationRequestDto> getCreationRequestById(@PathVariable("id") Long creationRequestId) {
        return ResponseEntity.ok(requestService.findCreationRequestById(creationRequestId));
    }
}
