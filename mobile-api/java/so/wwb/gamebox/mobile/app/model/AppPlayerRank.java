package so.wwb.gamebox.mobile.app.model;

/**
 * Created by ed on 18-1-30.
 */
public class AppPlayerRank {
    /** 提现单笔上限金额 */
    private Integer withdrawMaxNum;
    /** 提现单笔下限金额 */
    private Integer withdrawMinNum;

    public Integer getWithdrawMaxNum() {
        return withdrawMaxNum;
    }

    public void setWithdrawMaxNum(Integer withdrawMaxNum) {
        this.withdrawMaxNum = withdrawMaxNum;
    }

    public Integer getWithdrawMinNum() {
        return withdrawMinNum;
    }

    public void setWithdrawMinNum(Integer withdrawMinNum) {
        this.withdrawMinNum = withdrawMinNum;
    }
}
