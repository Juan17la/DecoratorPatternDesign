package com.naruto.mission;

import com.naruto.game.GameStateManager;
import com.naruto.game.NinjaRank;
import com.naruto.wave.WaveManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages missions, wave flow, and ninja rank promotions.
 *
 * Promotion ladder:
 *   GENIN   → 30 D-rank completions → Chunin Exam unlocks (3 special missions)
 *   CHUNIN  → Pass all 3 Chunin Exam missions → can do C/B missions
 *   JONIN   → 5+ B-rank completions → Jounin Promotion Mission unlocks (A-rank, ≤3 lives lost)
 *   ELITE_JONIN → any rank: >80% success rate AND 100+ total missions
 */
public class MissionManager {

    private static final int D_RANK_FOR_CHUNIN_EXAM = 30;
    private static final int B_RANK_FOR_JONIN_PROMO =  5;

    private final List<Mission> missions       = new ArrayList<>();
    private final List<Mission> specialMissions = new ArrayList<>();

    private Mission     activeMission;
    private WaveManager waveManager;
    private int         livesAtMissionStart;

    private int     dRankCompleted   = 0;
    private int     bRankCompleted   = 0;
    private int     chuninExamPassed = 0;  // 0..3
    private boolean chuninExamAdded  = false;
    private boolean joninPromoAdded  = false;
    private boolean joninPromoDone   = false;

    public MissionManager() {
        seedMissions();
    }

    // ── mission pool ──────────────────────────────────────────────────────────

    private void seedMissions() {
        missions.add(new Mission("d1", "Patrol the Border",     MissionRank.D, 3,  50));
        missions.add(new Mission("d2", "Find the Lost Scroll",  MissionRank.D, 3,  50));
        missions.add(new Mission("d3", "Escort the Merchant",   MissionRank.D, 3,  55));
        missions.add(new Mission("d4", "Clear the Road",        MissionRank.D, 3,  55));
        missions.add(new Mission("d5", "Stop the Troublemaker", MissionRank.D, 3,  60));

        missions.add(new Mission("c1", "Intercept the Bandits", MissionRank.C, 4, 100));
        missions.add(new Mission("c2", "Protect the Bridge",    MissionRank.C, 4, 110));
        missions.add(new Mission("c3", "Hunt the Criminals",    MissionRank.C, 4, 120));

        missions.add(new Mission("b1", "Defend the Village",    MissionRank.B, 5, 200));
        missions.add(new Mission("b2", "Hunt the Missing-Nin",  MissionRank.B, 5, 220));
        missions.add(new Mission("b3", "Rogue Ninja Pursuit",   MissionRank.B, 5, 240));

        missions.add(new Mission("a1", "Eliminate the Jonin",   MissionRank.A, 6, 350));
        missions.add(new Mission("a2", "Stop the Kiri Assault", MissionRank.A, 6, 380));
    }

    private void addChunInExam() {
        if (chuninExamAdded) return;
        chuninExamAdded = true;
        specialMissions.add(new Mission(
            "exam1", "Chunin Exam — Stage 1",
            MissionRank.D, MissionType.CHUNIN_EXAM_1, 6, 0, Mission.NO_LIMIT));
        specialMissions.add(new Mission(
            "exam2", "Chunin Exam — Stage 2",
            MissionRank.C, MissionType.CHUNIN_EXAM_2, 5, 0, Mission.NO_LIMIT));
        specialMissions.add(new Mission(
            "exam3", "Chunin Exam — Stage 3",
            MissionRank.C, MissionType.CHUNIN_EXAM_3, 4, 0, 5));
    }

    private void addJoninPromo() {
        if (joninPromoAdded) return;
        joninPromoAdded = true;
        specialMissions.add(new Mission(
            "jonin_promo", "Jounin Promotion Trial",
            MissionRank.A, MissionType.JONIN_PROMOTION, 6, 0, 3));
    }

    // ── start / complete ──────────────────────────────────────────────────────

    public boolean startMission(String missionId) {
        Mission m = findById(missionId);
        if (m == null) return false;

        GameStateManager state = GameStateManager.getInstance();
        if (!canAccess(m, state.getPlayerRank())) return false;

        activeMission       = m;
        livesAtMissionStart = state.getLives();
        waveManager         = new WaveManager(m.getTotalWaves(), m.getRank(), m.getType());
        return true;
    }

    public void completeMission(boolean success) {
        if (activeMission == null) return;

        GameStateManager state = GameStateManager.getInstance();
        int livesLost = livesAtMissionStart - state.getLives();

        if (activeMission.hasLivesLimit() && livesLost > activeMission.getLivesLostLimit()) {
            success = false;
        }

        activeMission.complete();
        state.addMission(success);
        if (success) state.addCoins(activeMission.getCoinReward());

        if (success) {
            trackPromotion(activeMission);
        }

        activeMission = null;
        waveManager   = null;
    }

    // ── promotion logic ───────────────────────────────────────────────────────

    private void trackPromotion(Mission m) {
        GameStateManager state   = GameStateManager.getInstance();
        NinjaRank        current = state.getPlayerRank();

        switch (m.getRank()) {
            case D -> {
                dRankCompleted++;
                if (dRankCompleted >= D_RANK_FOR_CHUNIN_EXAM && !chuninExamAdded) {
                    addChunInExam();
                }
            }
            case B -> {
                bRankCompleted++;
                if (current == NinjaRank.CHUNIN
                        && bRankCompleted >= B_RANK_FOR_JONIN_PROMO
                        && !joninPromoAdded) {
                    addJoninPromo();
                }
            }
            default -> {}
        }

        switch (m.getType()) {
            case CHUNIN_EXAM_1 -> chuninExamPassed = Math.max(chuninExamPassed, 1);
            case CHUNIN_EXAM_2 -> chuninExamPassed = Math.max(chuninExamPassed, 2);
            case CHUNIN_EXAM_3 -> {
                chuninExamPassed = 3;
                if (current == NinjaRank.GENIN) {
                    state.setPlayerRank(NinjaRank.CHUNIN);
                }
            }
            case JONIN_PROMOTION -> {
                joninPromoDone = true;
                if (current.ordinal() < NinjaRank.JONIN.ordinal()) {
                    state.setPlayerRank(NinjaRank.JONIN);
                }
            }
            default -> {}
        }

        checkEliteJonin(state);
    }

    private void checkEliteJonin(GameStateManager state) {
        if (state.getPlayerRank().ordinal() >= NinjaRank.ELITE_JONIN.ordinal()) return;
        if (state.getTotalMissions() >= 100 && state.getSuccessRate() > 0.80) {
            state.setPlayerRank(NinjaRank.ELITE_JONIN);
        }
    }

    // ── access control ────────────────────────────────────────────────────────

    private boolean canAccess(Mission m, NinjaRank rank) {
        return switch (m.getType()) {
            case CHUNIN_EXAM_1   -> chuninExamAdded  && chuninExamPassed < 1;
            case CHUNIN_EXAM_2   -> chuninExamPassed >= 1 && chuninExamPassed < 2;
            case CHUNIN_EXAM_3   -> chuninExamPassed >= 2 && chuninExamPassed < 3;
            case JONIN_PROMOTION -> joninPromoAdded  && !joninPromoDone;
            case NORMAL          -> switch (m.getRank()) {
                case D    -> rank.ordinal() >= NinjaRank.GENIN.ordinal();
                case C, B -> rank.ordinal() >= NinjaRank.CHUNIN.ordinal();
                case A, S -> rank.ordinal() >= NinjaRank.JONIN.ordinal();
            };
        };
    }

    // ── queries ───────────────────────────────────────────────────────────────

    public List<Mission> getAvailableMissions() {
        NinjaRank rank = GameStateManager.getInstance().getPlayerRank();
        List<Mission> result = missions.stream()
            .filter(m -> canAccess(m, rank))
            .collect(Collectors.toCollection(ArrayList::new));
        specialMissions.stream()
            .filter(m -> canAccess(m, rank))
            .forEach(result::add);
        return result;
    }

    public List<Mission>  getMissions()           { return Collections.unmodifiableList(missions); }
    public List<Mission>  getSpecialMissions()    { return Collections.unmodifiableList(specialMissions); }
    public WaveManager    getWaveManager()        { return waveManager; }
    public Mission        getActiveMission()      { return activeMission; }
    public int            getDRankCompleted()     { return dRankCompleted; }
    public int            getBRankCompleted()     { return bRankCompleted; }
    public int            getChunInExamPassed()   { return chuninExamPassed; }
    public boolean        isJoninPromoDone()      { return joninPromoDone; }

    private Mission findById(String id) {
        return Stream.concat(missions.stream(), specialMissions.stream())
            .filter(x -> x.getId().equals(id))
            .findFirst().orElse(null);
    }
}
