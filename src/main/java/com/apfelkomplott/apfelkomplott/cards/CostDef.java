package com.apfelkomplott.apfelkomplott.cards;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import java.util.Map;
public class CostDef {

    private Integer fixed;
    private Map<FarmingMode, Integer> byMode;

    public int resolve(FarmingMode mode) {
        if (fixed != null) return fixed;
        if (byMode != null && mode != null) {
            return byMode.get(mode);
        }
        throw new IllegalStateException("Cost not resolvable");
    }

    public Integer getFixed() {
        return fixed;
    }

    public void setFixed(Integer fixed) {
        this.fixed = fixed;
    }

    public Map<FarmingMode, Integer> getByMode() {
        return byMode;
    }

    public void setByMode(Map<FarmingMode, Integer> byMode) {
        this.byMode = byMode;
    }
}
