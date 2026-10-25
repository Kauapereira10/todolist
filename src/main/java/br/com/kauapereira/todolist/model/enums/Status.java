package br.com.kauapereira.todolist.model.enums;

public enum Status {
    TO_DO,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean canTransitionTo(Status target) {
        return switch (this) {
            case TO_DO, IN_PROGRESS ->
                target == TO_DO || target == IN_PROGRESS
                        || target == COMPLETED || target == CANCELLED;
            case COMPLETED, CANCELLED ->
                target == TO_DO;
        };
    }

}
