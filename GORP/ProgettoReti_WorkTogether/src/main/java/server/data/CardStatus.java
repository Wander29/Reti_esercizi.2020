package server.data;

import java.io.Serializable;

public enum CardStatus implements Serializable {
    TO_DO,
    IN_PROGRESS,
    TO_BE_REVISED,
    DONE
}