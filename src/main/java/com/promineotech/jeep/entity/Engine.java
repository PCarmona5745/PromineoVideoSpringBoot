package com.promineotech.jeep.entity;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Engine {
  private Long enginePK;
  private String engineId;
  private Float sizeInLiters;
  private String name;
  private FuelType fuelType;
  private Float mpgCity;
  private Float mpgHwy;
  private boolean hasStartStop;
  private String description;
  private BigDecimal price;
  
  Engine(){}
  
  Engine(Long enginePK, String engineId, Float sizeInLiters, String name, FuelType fuelType, Float mpgCity, Float mpgHwy, boolean hasStartStop, String description, BigDecimal price){
    this.enginePK = enginePK;
    this.engineId = engineId;
    this.sizeInLiters = sizeInLiters;
    this.name = name;
    this.fuelType = fuelType;
    this.mpgCity = mpgCity;
    this.mpgHwy = mpgHwy;
    this.hasStartStop = hasStartStop;
    this.description = description;
    this.price = price;
  }
}
