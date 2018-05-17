<%@ page contentType="text/html;charset=UTF-8" %>
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
               data-rel='{"opType":"function","target":"continueDeposit"}'>${views.deposit_auto['仍要继续']}</a>
            <a class="next-btn"
               data-rel='{"opType":"function","target":"goToDepositPage"}'>${views.deposit_auto['重新存款']}</a>
        </div>
    </div>
</div>
<!--存款帮助：-->
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
        <a data-rel='{"opType":"function","target":"closeHelpBox"}' class="closeHelpBox">+</a>
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
        <a data-rel='{"opType":"function","target":"closeHelpBox"}' class="closeHelpBox">+</a>
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
        <a data-rel='{"opType":"function","target":"closeHelpBox"}' class="closeHelpBox">+</a>
    </div>
</div>
</div>



<%@ include file="../../common/Footer.jsp" %>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/swiper.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/dist/clipboard.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositCenter.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/BaseDeposit.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositOnline.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositCompany.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositScancode.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositBitcoin.js?v=${rcVersion}"></script>