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
                    帮助。<a href="javascript:" data-rel='{"target":"loadCustomer","opType":"function" }'>点击联系在线客服</a></p>
            </div>
        </c:if>
        <c:if test="${fn:length(map) ==0}">
            <div class="deposit_tips">
                支付系统升级中,存款请联系右下角客服
            </div>
        </c:if>
    </div>
    <%--连续存款失败后提示弹窗--%>
    <div class="masker" id="failureHintsMasker" style="display:none;"></div>
    <!--遮罩-->
    <div class="gb-withdraw-box window-ok pro-window" id="failureHints" style="display:none">
        <div class="cont">
            <h3 style="margin-left:40%;margin-top: 5%;margin-bottom: 15px">${views.deposit_auto['消息']}</h3>
            <div class="ok-box">
                <img src="${resRoot}/images/warning.png" style="width: 50px;height: 50px;margin-bottom: 15px" alt=""/>
            </div>
            <i class="ok-icon"></i>
            <div class="cont-text">
                <span style="font-size: 15px ">${views.deposit_auto['多次失败提示']}</span>
            </div>
            <input type="hidden" id="channel"/>
            <div class="pro-btn">
                <a class="agin-btn"
                   data-rel='{"opType":"function","target":"baseDeposit.showActivity"}'>${views.deposit_auto['仍要继续']}</a>
                <a class="next-btn"
                   data-rel='{"opType":"function","target":"goToDepositPage"}'>${views.deposit_auto['重新存款']}</a>
            </div>
        </div>
    </div>
    <%--微信反扫教程：--%>
    <div id="depositHelpBox10" class="depositHelpBox" style="display: none">
        <div class="swiper-container">
            <div class="swiper-wrapper wechat-fansao">
                <div class="swiper-slide step-1"><img src="${resRoot}/images/wechat-step-1.png"/>
                    <div class="circle"></div>
                </div>
                <div class="swiper-slide step-2"><img src="${resRoot}/images/wechat-step-2.png"/>
                    <div class="circle"></div>
                </div>
                <div class="swiper-slide step-3"><img src="${resRoot}/images/wechat-step-3.png"/>
                    <div class="circle"></div>
                </div>
            </div>
            <div class="swiper-pagination swiper-pagination-bullets"></div>
            <a data-rel='{"opType":"function","target":"depositScanCode.closeReScanCourse"}' class="closeHelpBox">+</a>
        </div>
    </div>
    <!--qq反扫教程：-->
    <div id="depositHelpBox12" class="depositHelpBox" style="display:none;">
        <div class="swiper-container">
            <div class="swiper-wrapper qq-fansao" style="">
                <div class="swiper-slide step-1"><img src="${resRoot}/images/qq-step-1.png" alt=""/>
                    <div class="circle"></div>
                </div>
                <div class="swiper-slide step-2"><img src="${resRoot}/images/qq-step-2.png" alt=""/>
                    <div class="circle"></div>
                </div>
                <div class="swiper-slide step-3"><img src="${resRoot}/images/qq-step-3.png" alt=""/>
                    <div class="circle"></div>
                </div>
            </div>
            <div class="swiper-pagination"></div>
            <a data-rel='{"opType":"function","target":"depositScanCode.closeReScanCourse"}' class="closeHelpBox">+</a>
        </div>
    </div>
    <%--支付宝反扫教程：--%>
    <div id="depositHelpBox11" class="depositHelpBox" style="display:none;">
        <div class="swiper-container">
            <div class="swiper-wrapper alipay-fansao" style="">
                <div class="swiper-slide step-1"><img src="${resRoot}/images/alipay-step-1.png" alt=""/>
                    <div class="circle"></div>
                </div>
                <div class="swiper-slide step-2"><img src="${resRoot}/images/alipay-step-2.png" alt=""/>
                    <div class="circle"></div>
                </div>
            </div>
            <div class="swiper-pagination"></div>
            <a data-rel='{"opType":"function","target":"depositScanCode.closeReScanCourse"}' class="closeHelpBox">+</a>
        </div>
    </div>
</div>
<%@ include file="Deposit.footer.jsp" %>