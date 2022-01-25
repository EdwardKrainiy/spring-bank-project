package com.itech.model.entity;

import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Status;
import com.itech.utils.literal.JpaMappingDetails;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Basic CreationRequest class.
 *
 * @author Edvard Krainiy on 12/30/2021
 */
@Entity
@Getter
@Setter
@Table(name = JpaMappingDetails.CREATION_REQUEST_TABLE)
@NoArgsConstructor
@AllArgsConstructor
public class CreationRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = JpaMappingDetails.ID, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = JpaMappingDetails.USER_ID)
  private User user;

  @Column(name = JpaMappingDetails.PAYLOAD)
  private String payload;

  @Column(name = JpaMappingDetails.STATUS)
  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(name = JpaMappingDetails.CREATED_ID)
  private Long createdId;

  @Column(name = JpaMappingDetails.ISSUED_AT)
  private LocalDateTime issuedAt;

  @Column(name = JpaMappingDetails.CREATION_TYPE)
  @Enumerated(EnumType.STRING)
  private CreationType creationType;
}
