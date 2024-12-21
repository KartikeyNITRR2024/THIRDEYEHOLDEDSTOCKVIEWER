package com.thirdeye.holdedstockviewer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "holdedStockStatus")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class HoldedStockStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long holdedStockStatusId;

    @Column(name = "statusprice", nullable = false)
    private Double statusPrice;

    @Column(name = "statusid", nullable = false)
    private Long statusId;

    @ManyToOne
	@JoinColumn(name = "holded_stock_id", referencedColumnName = "holdedStockId")
	private HoldedStock holdedstock;
	
	@OneToOne(mappedBy = "current")
	private HoldedStock currentHoldedStock;
}

