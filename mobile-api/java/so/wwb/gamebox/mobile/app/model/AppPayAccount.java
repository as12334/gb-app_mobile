package so.wwb.gamebox.mobile.app.model;

/**
 * Created by ed on 17-12-29.
 */
public class AppPayAccount {
    /**  */
    private Integer id;
    /**
     * 账户名称
     */
    private String payName;
    /**
     * 账号
     */
    private String account;
    /**
     * 姓名
     */
    private String fullName;
    /**
     * 类型（1公司入款；2线上支付）(字典表pay_account_type)
     */
    private String type;
    /**
     * 账户类型（1银行账户；2第三方账户）(字典表pay_account_account_type)
     */
    private String accountType;
    /**
     * 渠道(bank表的bank_name）
     */
    private String bankCode;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 单笔存款最小值
     */
    private Integer singleDepositMin;
    /**
     * 单笔存款最大值
     */
    private Integer singleDepositMax;
    /**
     * 第三方自定义名称
     */
    private String customBankName;
    /**
     * 开户行
     */
    private String openAcountName;
    /**
     * 第三方入款账户二维码图片路径
     */
    private String qrCodeUrl;
    /**
     * 备注内容
     */
    private String remark;
    /**
     * 是否开启随机额度
     */
    private Boolean randomAmount;
    /**
     * 别名
     **/
    private String aliasName;
    /**
     * 自定义账号信息
     */
    private String accountInformation;
    /**
     * 自定义账号提示
     */
    private String accountPrompt;
    /**
     * 对应可以存款的存款类型
     */
    private String rechargeType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public Integer getSingleDepositMin() {
        return singleDepositMin;
    }

    public void setSingleDepositMin(Integer singleDepositMin) {
        this.singleDepositMin = singleDepositMin;
    }

    public Integer getSingleDepositMax() {
        return singleDepositMax;
    }

    public void setSingleDepositMax(Integer singleDepositMax) {
        this.singleDepositMax = singleDepositMax;
    }

    public String getCustomBankName() {
        return customBankName;
    }

    public void setCustomBankName(String customBankName) {
        this.customBankName = customBankName;
    }

    public String getOpenAcountName() {
        return openAcountName;
    }

    public void setOpenAcountName(String openAcountName) {
        this.openAcountName = openAcountName;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getRandomAmount() {
        return randomAmount;
    }

    public void setRandomAmount(Boolean randomAmount) {
        this.randomAmount = randomAmount;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getAccountInformation() {
        return accountInformation;
    }

    public void setAccountInformation(String accountInformation) {
        this.accountInformation = accountInformation;
    }

    public String getAccountPrompt() {
        return accountPrompt;
    }

    public void setAccountPrompt(String accountPrompt) {
        this.accountPrompt = accountPrompt;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
