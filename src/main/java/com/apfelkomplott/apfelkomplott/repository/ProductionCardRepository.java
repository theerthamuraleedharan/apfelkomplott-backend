package com.apfelkomplott.apfelkomplott.repository;

import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductionCardRepository {

  private final Map<String, ProductionCardDef> byId;
  private final List<ProductionCardDef> all;

  public ProductionCardRepository(ObjectMapper mapper) throws IOException {
    var is = new ClassPathResource("static/cards/production_cards.json").getInputStream();
    this.all = mapper.readValue(is, new TypeReference<List<ProductionCardDef>>() {});
    this.byId = all.stream().collect(Collectors.toMap(ProductionCardDef::getId, c -> c));
  }

  public List<ProductionCardDef> all() {
    return all;
  }

  public ProductionCardDef getById(String id) {
    ProductionCardDef card = byId.get(id);
    if (card == null) throw new IllegalArgumentException("Production card not found: " + id);
    return card;
  }
}
