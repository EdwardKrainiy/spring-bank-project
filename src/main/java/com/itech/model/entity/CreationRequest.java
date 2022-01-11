package com.itech.model.entity;

import com.itech.model.enumeration.CreationType;
import com.itech.model.enumeration.Status;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Basic CreationRequest class.
 *
 * @author Edvard Krainiy on 12/30/2021
 */

@Entity
@Getter
@Setter
@Table(name = "creation_request")
@NoArgsConstructor
@AllArgsConstructor
public class CreationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "payload")
    private String payload;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_id")
    private Long createdId;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "creation_type")
    @Enumerated(EnumType.STRING)
    private CreationType creationType;

}
