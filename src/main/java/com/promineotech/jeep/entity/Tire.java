package com.promineotech.jeep.entity;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tire {
  private Long tirePK;
  private String tireId;
  private String tireSize;
  private String manufacturer;
  private BigDecimal price;
  private int warrantyMiles;
  
  Tire(){}
  
  Tire(Long tirePK, String tireId, String tireSize, String manufacturer, BigDecimal price, int warrantyMiles){
    this.tirePK = tirePK;
    this.tireId = tireId;
    this.tireSize = tireSize;
    this.manufacturer = manufacturer;
    this.price = price;
    this.warrantyMiles = warrantyMiles;
  }
}
