package com.apfelkomplott.apfelkomplott.cards;

import java.util.Set;

public final class ProductionCardIds {
    public static final String GROUP_WATER_MANAGEMENT = "WATER_MANAGEMENT";
    public static final String GROUP_SHADE_NETS = "SHADE_NETS";

    public static final String WATER_MANAGEMENT = "LT_WATER_MANAGEMENT";
    public static final String WATER_MANAGEMENT_PRIVATE_WELL = "LT_WATER_MANAGEMENT_PRIVATE_WELL";
    public static final String WATER_MANAGEMENT_ECO = "LT_WATER_MANAGEMENT_ECO";
    public static final String SHADE_NETS = "ST_USE_SHADE_NETS";

    public static final Set<String> WATER_MANAGEMENT_IDS = Set.of(
            WATER_MANAGEMENT,
            WATER_MANAGEMENT_PRIVATE_WELL,
            WATER_MANAGEMENT_ECO
    );
    public static final Set<String> SHADE_NET_IDS = Set.of(SHADE_NETS);

    private ProductionCardIds() {
    }

    public static boolean containsWaterManagementCard(Iterable<String> cardIds) {
        if (cardIds == null) {
            return false;
        }

        for (String cardId : cardIds) {
            if (WATER_MANAGEMENT_IDS.contains(cardId)) {
                return true;
            }
        }

        return false;
    }

    public static boolean containsShadeNetCard(Iterable<String> cardIds) {
        if (cardIds == null) {
            return false;
        }

        for (String cardId : cardIds) {
            if (SHADE_NET_IDS.contains(cardId)) {
                return true;
            }
        }

        return false;
    }

    public static Set<String> resolveGroup(String groupName) {
        if (GROUP_WATER_MANAGEMENT.equals(groupName)) {
            return WATER_MANAGEMENT_IDS;
        }

        if (GROUP_SHADE_NETS.equals(groupName)) {
            return SHADE_NET_IDS;
        }

        return Set.of();
    }
}
