package dev.aponder.astracontrol.gui;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Per-player GUI state: which menu they currently have open, plus an optional
 * pending chat-input capture (used by flows like "enter placeholder" that need a
 * free-text prompt instead of a click).
 */
public final class MenuSession {

    /**
     * A single outstanding request for the player's next chat line, e.g. entering a
     * placeholder string or a maintenance reason.
     */
    public record PendingChatInput(String promptDescription, Consumer<String> onInput, Runnable onCancel) {
    }

    private final UUID playerId;
    private BaseMenu currentMenu;
    private PendingChatInput pendingChatInput;
    private UUID selectedTargetPlayer;

    public MenuSession(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID playerId() {
        return playerId;
    }

    public BaseMenu currentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(BaseMenu menu) {
        this.currentMenu = menu;
    }

    public void clearCurrentMenu() {
        this.currentMenu = null;
    }

    public boolean hasPendingChatInput() {
        return pendingChatInput != null;
    }

    public PendingChatInput pendingChatInput() {
        return pendingChatInput;
    }

    public void awaitChatInput(String promptDescription, Consumer<String> onInput, Runnable onCancel) {
        this.pendingChatInput = new PendingChatInput(promptDescription, onInput, onCancel);
    }

    public void clearPendingChatInput() {
        this.pendingChatInput = null;
    }

    public UUID selectedTargetPlayer() {
        return selectedTargetPlayer;
    }

    public void setSelectedTargetPlayer(UUID selectedTargetPlayer) {
        this.selectedTargetPlayer = selectedTargetPlayer;
    }
}
