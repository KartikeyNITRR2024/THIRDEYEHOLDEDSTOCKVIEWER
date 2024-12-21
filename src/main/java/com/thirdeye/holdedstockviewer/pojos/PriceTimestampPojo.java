package com.thirdeye.holdedstockviewer.pojos;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PriceTimestampPojo {
   Timestamp time;
   Double price;
}
