package so.wwb.gamebox.mobile.app.model;

/**
 * Created by legend on 18-1-28.
 */
public class AdvisoryMessageReplyListApp {

    private Long replyTime; //回复时间

    private String replyTitle; //回复标题
    private String replyContent; //回复内容


    public Long getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Long replyTime) {
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
