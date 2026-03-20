package com.naruto.mission;

public enum MissionType {
    NORMAL,
    CHUNIN_EXAM_1,   // D-rank enemies, 6 waves
    CHUNIN_EXAM_2,   // Mixed D+C enemies, 5 waves
    CHUNIN_EXAM_3,   // C-rank only, 4 waves — must lose fewer than 5 lives
    JONIN_PROMOTION  // A-rank mission, 6 waves — must lose 3 or fewer lives
}
