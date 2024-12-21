package com.thirdeye.holdedstockviewer.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "holdedStock")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class HoldedStock {
    
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "holded_stock_seq")
    @SequenceGenerator(name = "holded_stock_seq", sequenceName = "holded_stock_seq", allocationSize = 1)
    private Long holdedStockId;

    @Column(name = "stockid", nullable = false)
    private Long stockId;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "buyingpriceofsinglestock", nullable = false)
    private Double buyingPriceOfSingleStock;

    @Column(name = "buyingtime", nullable = false)
    private Timestamp buyingTime;

    @Column(name = "buyingtotalprice", nullable = false)
    private Double buyingTotalPrice;

    @Column(name = "noofstock", nullable = false)
    private Long noOfStock;

    @Column(name = "holding", nullable = false)
    private Integer holding;

    @Column(name = "sellingpriceofsinglestoke")
    private Double sellingPriceOfSingleStoke;

    @Column(name = "sellingtime")
    private Timestamp sellingTime;

    @Column(name = "sellingtotalprice")
    private Double sellingTotalPrice;
    
    @Column(name = "type")
    private Integer type;

//    @OneToMany(mappedBy = "holdedStock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<HoldedStockStatus> allStatus;
    
    @OneToMany(mappedBy = "holdedstock", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<HoldedStockStatus> allStatus = new ArrayList<>();
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "holded_stock_status_id", referencedColumnName = "holdedStockStatusId")
	private HoldedStockStatus current;


}
