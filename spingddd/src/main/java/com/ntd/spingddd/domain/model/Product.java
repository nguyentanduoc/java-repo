package com.ntd.spingddd.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Product implements Serializable {
  private Long id;
  private String name;
  private BigDecimal price;
}
