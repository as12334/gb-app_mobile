package so.wwb.gamebox.mobile.my.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;


/**
 * 表单验证对象
 */
@Comment("发送消息提交校验")
public class PlayerAdvisoryForm implements IForm {

    private String result_advisoryTitle;

    private String result_advisoryContent;

    @Comment("发送标题")
    @NotBlank(message = "operation.advisoryMessage.title.notBlank")
    @Length(min = 4, max = 100, message = "operation.advisoryMessage.title.length")
    public String getResult_advisoryTitle() {
        return result_advisoryTitle;
    }

    public void setResult_advisoryTitle(String result_advisoryTitle) {
        this.result_advisoryTitle = result_advisoryTitle;
    }

    @Comment("发送内容")
    @NotBlank(message = "operation.advisoryMessage.content.notBlank")
    @Length(min = 10, max = 20000, message = "operation.advisoryMessage.content.length")
    public String getResult_advisoryContent() {
        return result_advisoryContent;
    }

    public void setResult_advisoryContent(String result_advisoryContent) {
        this.result_advisoryContent = result_advisoryContent;
    }






}