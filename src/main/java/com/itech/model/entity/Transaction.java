package com.itech.model.entity;

import com.itech.model.enumeration.Status;
import com.itech.utils.literal.JpaMappingDetails;
import java.time.LocalDateTime;
import java.util.Set;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Basic Transaction class.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Entity
@Getter
@Setter
@Table(name = JpaMappingDetails.TRANSACTIONS_TABLE)
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = JpaMappingDetails.ID, nullable = false)
  private Long id;

  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
  @JoinColumn(name = JpaMappingDetails.USER_ID)
  private User user;

  @Column(name = JpaMappingDetails.ISSUED_AT)
  private LocalDateTime issuedAt;

  @Column(name = JpaMappingDetails.STATUS)
  @Enumerated(EnumType.STRING)
  private Status status;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = JpaMappingDetails.TRANSACTION)
  private Set<Operation> operations;
}
