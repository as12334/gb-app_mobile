package so.wwb.gamebox.mobile.app.model;

import java.util.Date;

/**
 * Created by legend on 18-1-14.
 */
public class AdvisoryMessageApp {

    private Integer id; //主键

    private String advisoryTitle; //标题
    private String advisoryContent; //　内容

    private String advisoryTime; // 时间

    private String replyTitle; // 回复标题

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

    public String getAdvisoryTime() {
        return advisoryTime;
    }

    public void setAdvisoryTime(String advisoryTime) {
        this.advisoryTime = advisoryTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReplyTitle() {
        return replyTitle;
    }

    public void setReplyTitle(String replyTitle) {
        this.replyTitle = replyTitle;
    }
}
