<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="Deposit.head.jsp" %>
<div class="mui-content mui-scroll-wrapper deposit-2-content">
    <div class="mui-scroll">
        <c:if test="${fn:length(map) >0 || !empty digiccyAccountInfo}">
            <div class="deposit_tips">温馨提示：完成存款后，请前往活动大厅申请活动优惠。</div>
            <%@ include file="Deposit.ToolBar.jsp" %>
        </c:if>
        <form id="rechargeForm" onsubmit="return false">
        <div class="mui-row" id="depositInput">
            <div class="ct" style="text-align: center;">
                <p class="text-gray-light">请选择存款方式</p>
            </div>
        </div>
        </form>
        <c:if test="${fn:length(map) >0 || !empty digiccyAccountInfo}">
            <div class="deposit_help">
                <p>温馨提示</p>
                <p>*为了提高对账速度及成功率，当前支付方式已开随机额度，请
                    输入整数存款金额，将随机增加0.11~0.99元！</p>
                <p>*请保留好转账单据作为核对证明。</p>
                <p>*如出现充值失败或充值后未到账等情况，请联系在线客服获取
                    帮助。<a href="javascript:">点击联系在线客服</a></p>
            </div>
        </c:if>
        <c:if test="${fn:length(map) ==0}">
            <div class="deposit_tips">
                支付系统升级中,存款请联系右下角客服
            </div>
        </c:if>
    </div>
<%@ include file="Deposit.footer.jsp" %>