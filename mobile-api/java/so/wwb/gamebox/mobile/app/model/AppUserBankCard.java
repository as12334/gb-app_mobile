package so.wwb.gamebox.mobile.app.model;

import java.util.Date;

/**
 * Created by ed on 18-1-29.
 */
public class AppUserBankCard {
    /** 主键 */
    private Integer id;
    /** 外键：sys_user表主键 */
    private Integer userId;
    /** 卡主名 */
    private String bankcardMasterName;
    /** 银行卡号 */
    private String bankcardNumber;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 使用次数 */
    private Integer useCount;
    /** 使用状态 0:未使用 1：使用过 */
    private Boolean useStauts;
    /** 0非默认 1：默认 */
    private Boolean isDefault;
    /** 银行卡名称 */
    private String bankName;
    /** 开户行 */
    private String bankDeposit;
    /** 没有匹配到银行自行输入的银行信息 */
    private String customBankName;
    /*银行卡号类型 1-银行卡 2-比特币*/
    private String type;
    /** 银行卡图片地址路径*/
    private String bankUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getBankcardMasterName() {
        return bankcardMasterName;
    }

    public void setBankcardMasterName(String bankcardMasterName) {
        this.bankcardMasterName = bankcardMasterName;
    }

    public String getBankcardNumber() {
        return bankcardNumber;
    }

    public void setBankcardNumber(String bankcardNumber) {
        this.bankcardNumber = bankcardNumber;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Boolean getUseStauts() {
        return useStauts;
    }

    public void setUseStauts(Boolean useStauts) {
        this.useStauts = useStauts;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankDeposit() {
        return bankDeposit;
    }

    public void setBankDeposit(String bankDeposit) {
        this.bankDeposit = bankDeposit;
    }

    public String getCustomBankName() {
        return customBankName;
    }

    public void setCustomBankName(String customBankName) {
        this.customBankName = customBankName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBankUrl() {
        return bankUrl;
    }

    public void setBankUrl(String bankUrl) {
        this.bankUrl = bankUrl;
    }
}
