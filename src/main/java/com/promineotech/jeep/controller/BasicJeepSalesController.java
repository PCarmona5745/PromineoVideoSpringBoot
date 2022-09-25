package com.promineotech.jeep.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.service.JeepSalesService;
import lombok.extern.slf4j.Slf4j;

//Tells spring boot that this class is a controller that implements the JeepSalesController. has to go in the implementing class, not on the interface.
@RestController
@Slf4j //logger from lombok
public class BasicJeepSalesController implements JeepSalesController {

  @Autowired
  private JeepSalesService jeepSalesService;
  
  @Override
  public List<Jeep> fetchJeeps(JeepModel model, String trim) {
    //posts to the log, model and trim that was requested. logs whenever the method above is called.
    log.debug("model={}, trim={}", model, trim);
    return jeepSalesService.fetchJeeps(model, trim);
  }

}
