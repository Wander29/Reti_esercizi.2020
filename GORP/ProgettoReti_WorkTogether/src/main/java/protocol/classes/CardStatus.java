package protocol.classes;

import java.io.Serializable;

public enum CardStatus implements Serializable {
    NEW,
    TO_DO,
    IN_PROGRESS,
    TO_BE_REVISED,
    DONE
}