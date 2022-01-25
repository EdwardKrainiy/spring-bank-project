package com.itech.model.entity;

import com.itech.model.enumeration.Currency;
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
 * Basic Account class.
 *
 * @author Edvard Krainiy on 12/16/2021
 */
@Entity
@Getter
@Setter
@Table(name = JpaMappingDetails.ACCOUNTS_TABLE)
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = JpaMappingDetails.ID, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = JpaMappingDetails.USER_ID)
  private User user;

  @Column(name = JpaMappingDetails.AMOUNT)
  private double amount;

  @Column(name = JpaMappingDetails.CURRENCY)
  @Enumerated(EnumType.STRING)
  private Currency currency;

  @Column(name = JpaMappingDetails.ACCOUNT_NUMBER)
  private String accountNumber;
}
