package protocol.classes;

import server.data.ServerProject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Project implements Serializable {
    private static final long serialVersionUID = 01L;

    public String getProjectName() {
        return projectName;
    }

    public Map<String,Card> getToDoCards() {
        return toDoCards;
    }

    public Map<String,Card> getInProgressCards() {
        return inProgressCards;
    }

    public Map<String,Card> getToBeRevisedCards() {
        return toBeRevisedCards;
    }

    public Map<String,Card> getDoneCards() {
        return doneCards;
    }

    protected String projectName;
    protected Map<String,Card> toDoCards;
    protected Map<String,Card> inProgressCards;
    protected Map<String,Card> toBeRevisedCards;
    protected Map<String,Card> doneCards;

    public Project(
            String name,
            Map<String,Card> toDo,
            Map<String,Card> inProgress,
            Map<String,Card> toBeRevised,
            Map<String,Card> done
    ) {
        this.projectName        = name;
        this.toDoCards          = toDo;
        this.inProgressCards    = inProgress;
        this.toBeRevisedCards   = toBeRevised;
        this.doneCards          = done;
    }

    public Project() {}

    public Project(ServerProject sp) {
        this.toDoCards          = sp.getToDoCards();
        this.inProgressCards    = sp.getInProgressCards();
        this.toBeRevisedCards   = sp.getToBeRevisedCards();
        this.doneCards          = sp.getDoneCards();
    }

    public Card getCardFromAnyList(String name) {
        if(this.toDoCards.containsKey(name)) {
            return this.toDoCards.get(name);
        }
        else if(this.inProgressCards.containsKey(name)) {
            return this.inProgressCards.get(name);
        }
        else if(this.toBeRevisedCards.containsKey(name)) {
            return this.toBeRevisedCards.get(name);
        }
        else if(this.doneCards.containsKey(name)) {
            return this.doneCards.get(name);
        }

        return null;
    }

}
