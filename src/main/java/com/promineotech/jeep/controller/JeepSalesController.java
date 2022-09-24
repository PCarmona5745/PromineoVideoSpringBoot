package com.promineotech.jeep.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

//any uri that has /jeeps after the port number, will get mapped to this class.
@RequestMapping("/jeeps")
@OpenAPIDefinition(info = @Info(title = "Jeep Sales Service"),
    servers = {@Server(url = "http://localhost:8080", description = "Local server.")})
//this is just an interface that must be implemented!
public interface JeepSalesController {

  //Annotations for OpenAPI vvvvvvvvvvv
  @Operation(summary = "Returns a list of Jeeps",
      description = "Returns a list of Jeeps given an optional model and/or trim",
      responses = {
          @ApiResponse(responseCode = "200", description = "A list of Jeeps is returned",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = Jeep.class))),
          @ApiResponse(responseCode = "400", description = "The request parameters are invalid",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(responseCode = "404",
              description = "No Jeeps were found with the input criteria",
              content = @Content(mediaType = "application/json")),
          @ApiResponse(responseCode = "500", description = "An unplanned error occured.",
              content = @Content(mediaType = "application/json"))},
      parameters = {
          @Parameter(name = "model", allowEmptyValue = false, required = false, description = "The model name (i.e. 'WRANGLER')"),
          @Parameter(name = "trim", allowEmptyValue = false, required = false, description = "The trim model name (i.e. 'Sport')")
      })
  //map GET request at /jeeps to the fetchJeeps method
  @GetMapping
  //will return OK (200) when successful
  @ResponseStatus(code = HttpStatus.OK)
  //These request parameter annotations are for Spring, not OpenAPI
  List<Jeep> fetchJeeps(@RequestParam(required = false) JeepModel model, @RequestParam(required = false) String trim);
}
