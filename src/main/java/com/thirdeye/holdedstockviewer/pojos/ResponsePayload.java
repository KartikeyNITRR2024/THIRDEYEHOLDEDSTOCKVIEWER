package com.thirdeye.holdedstockviewer.pojos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePayload {
	Timestamp nextIterationTime;
	Boolean updateData;
	List<Long> stockId;
}
