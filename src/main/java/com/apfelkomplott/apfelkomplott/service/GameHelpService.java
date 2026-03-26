package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.controller.dto.GameGuideDto;
import com.apfelkomplott.apfelkomplott.controller.dto.PhaseHelpDto;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameHelpService {

    public GameGuideDto buildGuide() {
        GameGuideDto guide = new GameGuideDto();
        guide.setTitle("How To Play Apfelkomplott");
        guide.setOverview("You manage an apple plantation over 15 rounds. Each round brings events, selling, harvesting, investments, and card effects that shape your farm.");
        guide.setWinCondition("Reach the end of round 15 without Economy, Environment, or Health dropping to -3.");
        guide.setSetupSteps(List.of(
                "Choose a farming mode to start a new game.",
                "Check the current round, money, and your three score tracks: Economy, Environment, and Health.",
                "Look at the market cards and event phase before advancing."
        ));
        guide.setBeginnerTips(List.of(
                "Do not buy every card you see. Keep enough money for future rounds.",
                "Short-term cards help immediately, while long-term cards shape future scoring.",
                "Watch all three score tracks, because the game can end early if one of them falls too low.",
                "When a scoring popup appears, read it before continuing because it explains what just changed."
        ));
        guide.setPhases(List.of(
                phase(GamePhase.MOVE_MARKER, "Start Of Round",
                        "Reset the round and get ready for the next sequence.",
                        "Advance to begin the event step for the new round.",
                        "This clears last-round popups and prepares the next turn."),
                phase(GamePhase.DRAW_EVENT, "Event Choice",
                        "Draw event options and choose what happens this round.",
                        "Open the event choices and select one option.",
                        "Events can change money, harvest, apple prices, or future card costs."),
                phase(GamePhase.REFILL_CARDS, "Refill Market",
                        "Prepare the production card market.",
                        "Review the new market row after the refill.",
                        "The available short-term and long-term cards shape your next investment decisions."),
                phase(GamePhase.SELL, "Sell Apples",
                        "Sell apples already placed in sales stands.",
                        "Advance to convert ready apples into money.",
                        "This is your direct cash income for the round."),
                phase(GamePhase.DELIVER, "Deliver Apples",
                        "Move apples from transport toward sales stands.",
                        "Advance after checking whether your sales stands have space.",
                        "Delivery helps turn harvested apples into future income."),
                phase(GamePhase.HARVEST, "Harvest",
                        "Harvest apples from mature trees.",
                        "Advance to harvest and fill transport crates if space is available.",
                        "Harvest is affected by tree maturity and by event-based harvest loss."),
                phase(GamePhase.ROTATE, "Rotate Trees",
                        "Move plantation growth forward by one step.",
                        "Advance so each tree ages into its next field position.",
                        "Rotation controls when trees mature and when old trees leave the plantation."),
                phase(GamePhase.INTERMEDIATE_SCORING, "Intermediate Scoring",
                        "Apply round-based score changes.",
                        "Read the scoring popup, then continue once you understand the result.",
                        "This is where empty logistics or wasted apples can punish your economy."),
                phase(GamePhase.INVEST, "Invest",
                        "Spend money on farm improvements.",
                        "Choose whether to buy trees, logistics, sales capacity, or other upgrades.",
                        "Investments improve your future harvest and selling potential."),
                phase(GamePhase.CARD_SCORING, "Production Card Scoring",
                        "Apply active long-term card effects for the round.",
                        "Read the popup if card scoring appears, then continue.",
                        "Long-term cards often create the biggest strategic effects over time.")
        ));
        return guide;
    }

    public PhaseHelpDto buildCurrentPhaseHelp(GameState state) {
        if (state == null) {
            return new PhaseHelpDto(
                    null,
                    "Welcome",
                    "Start a game and use this help panel whenever you feel lost.",
                    "Open the full guide first, then choose a farming mode and begin round 1.",
                    "This gives new players a quick onboarding point before they make their first decision."
            );
        }

        return switch (state.getCurrentPhase()) {
            case MOVE_MARKER -> phase(GamePhase.MOVE_MARKER, "Start Of Round",
                    "You are beginning a new round.",
                    "Advance to reveal this round's event step.",
                    "This is the clean reset point before new round effects happen.");
            case DRAW_EVENT -> phase(GamePhase.DRAW_EVENT, "Choose An Event",
                    "Pick one event option for this round.",
                    "Open the event options, compare them, and select one.",
                    "Your choice can change money, harvest output, or future prices.");
            case REFILL_CARDS -> phase(GamePhase.REFILL_CARDS, "Check The Market",
                    "New production cards are being prepared.",
                    "Look at the refreshed market before planning purchases.",
                    "The right card now can be more valuable than a random purchase later.");
            case SELL -> phase(GamePhase.SELL, "Cash In Apples",
                    "Sell apples already waiting in sales stands.",
                    "Advance to convert sell-ready apples into money.",
                    "Selling funds your future investments.");
            case DELIVER -> phase(GamePhase.DELIVER, "Move Apples Forward",
                    "Transport apples from crates toward sales stands.",
                    "Advance after checking how much stand space you have left.",
                    "Good delivery flow improves next round's selling.");
            case HARVEST -> phase(GamePhase.HARVEST, "Collect Harvest",
                    "Mature trees will produce apples now.",
                    "Advance and make sure you have transport capacity available.",
                    "If logistics are full, you can lose value through waste.");
            case ROTATE -> phase(GamePhase.ROTATE, "Grow The Plantation",
                    "Your trees age by one field position.",
                    "Advance to move the plantation timeline forward.",
                    "Tree growth determines when they become productive and when they disappear.");
            case INTERMEDIATE_SCORING -> phase(GamePhase.INTERMEDIATE_SCORING, "Read The Score Popup",
                    "This step applies the round's economy penalties or bonuses.",
                    "Read the reasons shown in the popup before continuing.",
                    "This is the clearest explanation of why your score changed.");
            case INVEST -> phase(GamePhase.INVEST, "Choose An Investment",
                    "Spend money to improve the farm.",
                    "Pick upgrades carefully instead of spending everything immediately.",
                    "Balanced growth is safer than overspending on one area.");
            case CARD_SCORING -> phase(GamePhase.CARD_SCORING, "Resolve Long-Term Effects",
                    "Your active long-term cards are being scored.",
                    "Read the popup if it appears, then continue to the next round.",
                    "This shows the payoff from your strategic card choices.");
        };
    }

    private PhaseHelpDto phase(GamePhase phase, String title, String goal, String whatToDo, String whyItMatters) {
        return new PhaseHelpDto(phase, title, goal, whatToDo, whyItMatters);
    }
}
