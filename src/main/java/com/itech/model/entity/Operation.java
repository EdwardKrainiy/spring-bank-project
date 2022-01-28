package com.itech.model.entity;

import com.itech.model.enumeration.OperationType;
import com.itech.utils.literal.JpaMappingDetails;
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
 * Basic Operation class.
 *
 * @author Edvard Krainiy on 12/23/2021
 */
@Entity
@Getter
@Setter
@Table(name = "operation")
@NoArgsConstructor
@AllArgsConstructor
public class Operation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = JpaMappingDetails.ID, nullable = false)
  private Long id;

  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
  @JoinColumn(name = JpaMappingDetails.ACCOUNT_ID)
  private Account account;

  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
  @JoinColumn(name = JpaMappingDetails.TRANSACTION_ID)
  private Transaction transaction;

  @Column(name = JpaMappingDetails.AMOUNT)
  private double amount;

  @Column(name = JpaMappingDetails.OPERATION_TYPE)
  @Enumerated(EnumType.STRING)
  private OperationType operationType;
}
