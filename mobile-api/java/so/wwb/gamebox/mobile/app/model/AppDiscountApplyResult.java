package so.wwb.gamebox.mobile.app.model;

import java.util.List;

/**
 * Created by ed on 17-12-29.
 * 活动申请结果
 */
public class AppDiscountApplyResult {
    private String  actibityTitle;//活动标题
    private String applyResult;//申请结果
    private Integer  status;//申请状态 1:成功，2：失败，3：部分成功
    private List applyDetails;//参与条件列表
    private String Tips; //活动当前参与人数 / 派奖时间

    public String getActibityTitle() {
        return this.actibityTitle;
    }

    public void setActibityTitle(String actibityTitle) {
        this.actibityTitle = actibityTitle;
    }

    public String getApplyResult() {
        return this.applyResult;
    }

    public void setApplyResult(String applyResult) {
        this.applyResult = applyResult;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List getApplyDetails() {
        return applyDetails;
    }

    public void setApplyDetails(List applyDetails) {
        this.applyDetails = applyDetails;
    }

    public String getTips() {
        return Tips;
    }

    public void setTips(String tips) {
        Tips = tips;
    }
}
