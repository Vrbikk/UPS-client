package client;

/**
 * Created by vrbik on 29.10.16.
 */
public class Question {

    public int questionId;
    public int points;
    public boolean avaible;
    public String category;

    public Question(int questionId, int points, boolean avaible, String category) {
        this.questionId = questionId;
        this.points = points;
        this.avaible = avaible;
        this.category = category;
    }
}
