package so.wwb.gamebox.mobile.app.model;

import java.util.Date;

/**
 * Created by legend on 18-1-15.
 */
public class AdvisoryMessageDetailApp {

    private String advisoryTitle; //咨询标题
    private String advisoryContent; //　咨询内容

    private String questionType;  //问题类型player.question_type(提问,追问)
    private String advisoryTime; // 提问  时间


    private String replyTime; //回复时间

    private String replyTitle; //回复标题
    private String replyContent; //回复内容

    public String getAdvisoryTitle() {
        return advisoryTitle;
    }

    public void setAdvisoryTitle(String advisoryTitle) {
        this.advisoryTitle = advisoryTitle;
    }

    public String getAdvisoryContent() {
        return advisoryContent;
    }

    public void setAdvisoryContent(String advisoryContent) {
        this.advisoryContent = advisoryContent;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getAdvisoryTime() {
        return advisoryTime;
    }

    public void setAdvisoryTime(String advisoryTime) {
        this.advisoryTime = advisoryTime;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyTitle() {
        return replyTitle;
    }

    public void setReplyTitle(String replyTitle) {
        this.replyTitle = replyTitle;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }
}
