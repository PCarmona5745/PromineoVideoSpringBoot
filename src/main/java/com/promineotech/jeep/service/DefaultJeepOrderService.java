package com.promineotech.jeep.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.promineotech.jeep.dao.JeepOrderDao;
import com.promineotech.jeep.entity.Color;
import com.promineotech.jeep.entity.Customer;
import com.promineotech.jeep.entity.Engine;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.Option;
import com.promineotech.jeep.entity.Order;
import com.promineotech.jeep.entity.OrderRequest;
import com.promineotech.jeep.entity.Tire;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class DefaultJeepOrderService implements JeepOrderService {

  @Autowired
  private JeepOrderDao jeepOrderDao;

  @Transactional
  @Override
  public Order createOrder(OrderRequest orderRequest) {

    log.debug("Create order with order request = {}", orderRequest);

    Customer customer = jeepOrderDao.fetchCustomer(orderRequest.getCustomer())
        .orElseThrow(() -> new NoSuchElementException(
            "Customer with ID=" + orderRequest.getCustomer() + " was not found."));


    Jeep jeep = jeepOrderDao
        .fetchModel(orderRequest.getModel(), orderRequest.getTrim(), orderRequest.getDoors())
        .orElseThrow(() -> new NoSuchElementException(
            "Jeep Model with ID=" + orderRequest.getModel() + ", trim=" + orderRequest.getTrim()
                + ", doors=" + orderRequest.getDoors() + " was not found."));


    Color color = jeepOrderDao.fetchColor(orderRequest.getColor())
        .orElseThrow(() -> new NoSuchElementException(
            "Color with ID=" + orderRequest.getColor() + " was not found."));

    Engine engine = jeepOrderDao.fetchEngine(orderRequest.getEngine())
        .orElseThrow(() -> new NoSuchElementException(
            "Engine with ID=" + orderRequest.getEngine() + " was not found."));

    Tire tire =
        jeepOrderDao.fetchTire(orderRequest.getTire()).orElseThrow(() -> new NoSuchElementException(
            "Tire with ID=" + orderRequest.getTire() + " was not found."));



    List<Option> options = getOption(orderRequest);
    BigDecimal price =
        jeep.getBasePrice().add(color.getPrice()).add(engine.getPrice()).add(tire.getPrice());

    for (Option option : options) {
      price = price.add(option.getPrice());
    }


    return jeepOrderDao.saveOrder(customer, jeep, color, engine, tire, price, options);
  }

  private List<Option> getOption(OrderRequest orderRequest) {
    return jeepOrderDao.fetchOptions(orderRequest.getOptions());

  }

}
