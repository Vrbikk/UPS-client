package client;

/**
 * Created by vrbik on 29.10.16.
 */
public class Question {

    public int questionId;
    public int points;
    public boolean avaible;

    public Question(int questionId, int points, boolean avaible) {
        this.questionId = questionId;
        this.points = points;
        this.avaible = avaible;
    }
}
