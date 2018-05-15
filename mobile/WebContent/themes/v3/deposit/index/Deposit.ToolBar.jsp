<%@ page contentType="text/html;charset=UTF-8" %>
<div class="list_pay" id="list_pay">
    <c:if test="${map['online']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"online","key":"online","url":"/wallet/deposit/online/index.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="icon-pay online-pay"></i>
                <div class="pay_nam">
                    ${views.deposit['online_deposit']}
                </div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['company']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"company","key":"company","url":"/wallet/v3/deposit/company/depositCash.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="icon-pay wangyin"></i>
                <div class="pay_nam">网银支付</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['wechat']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"wechatpay","key":"company","url":"/wallet/v3/deposit/online/scan/scanCode/wechatpay.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="icon-pay weichat-pay"></i>
                <div class="pay_nam">微信支付</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['alipay']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"alipay","key":"company","url":"/wallet/v3/deposit/online/scan/scanCode/alipay.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="icon-pay alipay"></i>
                <div class="pay_nam">支付宝支付</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['qq']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"qqwallet","key":"company","url":"/wallet/v3/deposit/online/scan/scanCode/qqwallet.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="icon-pay qq-wallet"></i>
                <div class="pay_nam">QQ钱包</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['jd']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"jdwallet","key":"company","url":"/wallet/v3/deposit/online/scan/scanCode/jdwallet.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="icon-pay jingdong-wallet"></i>
                <div class="pay_nam">京东钱包</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['bd']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"bdwallet","key":"company","url":"/wallet/v3/deposit/online/scan/scanCode/bdwallet.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="icon-pay baidu-wallet"></i>
                <div class="pay_nam">百度钱包</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['bitcoin']>0}">
        <div class="list_pay_item_w">
            <div class="list_pay_item">
                <i class="icon-pay bitcoin-pay"></i>
                <div class="pay_nam">比特币</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['onecodepay']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"onecodepay","key":"company","url":"/wallet/v3/deposit/online/scan/scanCode/onecodepay.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="icon-pay yima-pay"></i>
                <div class="pay_nam">一码付</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['unionpay']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"unionpay","key":"company","url":"/wallet/v3/deposit/online/scan/scanCode/unionpay.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="i== 0con-pay union-pay"></i>
                <div class="pay_nam">银联扫码</div>
            </div>
        </div>
    </c:if>
    <c:if test="${isFastRecharge}">
        <div class="list_pay_item_w">
            <div class="list_pay_item">
                <i class="icon-pay kuaichong"></i>
                <div class="pay_nam">快充中心</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['counter']>0}">
        <div class="list_pay_item_w">
            <div class="list_pay_item">
                <i class="icon-pay wangyin-dep"></i>
                <div class="pay_nam">柜台存款</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['easy']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"easypay","key":"company","url":"/wallet/v3/deposit/online/scan/scanCode/easypay.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="icon-pay yishou-pay"></i>
                <div class="pay_nam">易收付</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['other']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"other","key":"company","url":"/wallet/v3/deposit/online/scan/scanCode/other.html","opType":"function","target":"amountInput"}'>
            <div class="list_pay_item">
                <i class="icon-pay other-pay"></i>
                <div class="pay_nam">其它</div>
            </div>
        </div>
    </c:if>
</div>