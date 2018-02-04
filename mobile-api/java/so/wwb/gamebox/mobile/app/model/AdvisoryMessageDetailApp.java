package so.wwb.gamebox.mobile.app.model;

import java.util.Date;
import java.util.List;

/**
 * Created by legend on 18-1-15.
 */
public class AdvisoryMessageDetailApp {

    private String advisoryTitle; //咨询标题
    private String advisoryContent; //　咨询内容

    private String questionType;  //问题类型player.question_type(提问,追问)
    private Long advisoryTime; // 提问  时间

    private List<AdvisoryMessageReplyListApp> replyList; // 回复的一个循环列表


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

    public Long getAdvisoryTime() {
        return advisoryTime;
    }

    public void setAdvisoryTime(Long advisoryTime) {
        this.advisoryTime = advisoryTime;
    }

    public List<AdvisoryMessageReplyListApp> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<AdvisoryMessageReplyListApp> replyList) {
        this.replyList = replyList;
    }
}
