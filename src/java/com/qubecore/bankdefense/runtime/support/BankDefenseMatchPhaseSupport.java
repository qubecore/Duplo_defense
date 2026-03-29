package com.qubecore.bankdefense.runtime.support;

import com.qubecore.bankdefense.runtime.BankDefenseRuntime.GameState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.MatchState;
import com.qubecore.bankdefense.runtime.BankDefenseRuntime.WaveState;

public final class BankDefenseMatchPhaseSupport {
    private BankDefenseMatchPhaseSupport() {
    }

    public static boolean isPrepared(MatchState state) {
        return state != null && state.gameStarted;
    }

    public static boolean isWaitingForSetup(MatchState state) {
        return state != null && state.gameState == GameState.Ready && !state.gameStarted;
    }

    public static boolean isBuildPhase(MatchState state) {
        return state != null && state.gameState == GameState.Ready && state.waveState == WaveState.BuildPhase;
    }

    public static boolean canBuild(MatchState state, boolean allowDuringWave) {
        return state != null && (allowDuringWave || isBuildPhase(state));
    }

    public static boolean canStartPreparedWave(MatchState state) {
        return isPrepared(state) && isBuildPhase(state);
    }

    public static boolean isFinished(MatchState state) {
        return state != null && (state.gameState == GameState.Victory || state.gameState == GameState.Defeat);
    }
}
