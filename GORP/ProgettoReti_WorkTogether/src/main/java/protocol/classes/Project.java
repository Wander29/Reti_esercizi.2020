package protocol.classes;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.util.List;

public class Project implements Serializable {
    private static final long serialVersionUID = 42L;

    public String getProjectName() {
        return projectName;
    }

    public List<Card> getToDoCards() {
        return toDoCards;
    }

    public List<Card> getInProgressCards() {
        return inProgressCards;
    }

    public List<Card> getToBeRevisedCards() {
        return toBeRevisedCards;
    }

    public List<Card> getDoneCards() {
        return doneCards;
    }

    protected String projectName;
    protected List<Card> toDoCards;
    protected List<Card> inProgressCards;
    protected List<Card> toBeRevisedCards;
    protected List<Card> doneCards;

    public Project(
            String name,
            List<Card> toDo,
            List<Card> inProgress,
            List<Card> toBeRevised,
            List<Card> done
    ) {
        this.projectName        = name;
        this.toDoCards          = toDo;
        this.inProgressCards    = inProgress;
        this.toBeRevisedCards   = toBeRevised;
        this.doneCards          = done;
    }

}
