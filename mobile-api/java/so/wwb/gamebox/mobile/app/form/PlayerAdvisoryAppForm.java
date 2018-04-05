package so.wwb.gamebox.mobile.app.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;

/**
 * Created by legend on 18-1-19.
 */
public class PlayerAdvisoryAppForm implements IForm {

//    result.advisoryType
    private String result_advisoryType;

    private String result_advisoryTitle;

    private String result_advisoryContent;

    @Comment("发送标题")
    @NotBlank(message = "发送标题不得为空")
    @Length(min = 4, max = 100, message = "发送长度需要在4和100之间")
    public String getResult_advisoryTitle() {
        return result_advisoryTitle;
    }

    public void setResult_advisoryTitle(String result_advisoryTitle) {
        this.result_advisoryTitle = result_advisoryTitle;
    }

    @Comment("发送内容")
    @NotBlank(message = "发送内容不得为空")
    @Length(min = 10, max = 20000, message = "发送内容长度需要在10和20000之间")
    public String getResult_advisoryContent() {
        return result_advisoryContent;
    }


    public void setResult_advisoryContent(String result_advisoryContent) {
        this.result_advisoryContent = result_advisoryContent;
    }
    @NotBlank(message = "标题类型未输入")
    public String getResult_advisoryType() {
        return result_advisoryType;
    }

    public void setResult_advisoryType(String result_advisoryType) {
        this.result_advisoryType = result_advisoryType;
    }
}
