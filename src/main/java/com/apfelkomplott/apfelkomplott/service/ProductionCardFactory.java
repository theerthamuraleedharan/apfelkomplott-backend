package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import com.apfelkomplott.apfelkomplott.entity.cards.GenericProductionCard;
import org.springframework.stereotype.Service;

@Service
public class ProductionCardFactory {

    public ProductionCard create(ProductionCardDefinition def) {
        return new GenericProductionCard(
                def.getName(),
                def.getEconomyEffect(),
                def.getEnvironmentEffect(),
                def.getHealthEffect(),
                def.getStartRound(),
                def.getEndRound()
        );
    }
}
