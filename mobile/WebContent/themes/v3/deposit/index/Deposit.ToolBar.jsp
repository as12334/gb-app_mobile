<%@ page contentType="text/html;charset=UTF-8" %>
<div class="list_pay" id="list_pay">
    <c:if test="${map['online']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"online","url":"/wallet/v3/deposit/online/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay online-pay"></i>
                <div class="pay_nam">
                    ${views.deposit['online_deposit']}
                </div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['company']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"company","url":"/wallet/v3/deposit/company/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay wangyin"></i>
                <div class="pay_nam">网银支付</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['wechat']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"wechatpay","url":"/wallet/v3/deposit/wechatpay/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay weichat-pay"></i>
                <div class="pay_nam">微信支付</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['alipay']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"alipay","url":"/wallet/v3/deposit/alipay/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay alipay"></i>
                <div class="pay_nam">支付宝支付</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['qq']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"qqwallet","url":"/wallet/v3/deposit/qqwallet/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay qq-wallet"></i>
                <div class="pay_nam">QQ钱包</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['jd']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"jdwallet","url":"/wallet/v3/deposit/jdwallet/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay jingdong-wallet"></i>
                <div class="pay_nam">京东钱包</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['bd']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"bdwallet","url":"/wallet/v3/deposit/bdwallet/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay baidu-wallet"></i>
                <div class="pay_nam">百度钱包</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['bitcoin']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"bitcoin","url":"/wallet/v3/deposit/bitcoin/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay bitcoin-pay"></i>
                <div class="pay_nam">比特币</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['onecodepay']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"onecodepay","key":"company","url":"/wallet/v3/deposit/onecodepay/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay yima-pay"></i>
                <div class="pay_nam">一码付</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['unionpay']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"unionpay","url":"/wallet/v3/deposit/unionpay/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="i== 0con-pay union-pay"></i>
                <div class="pay_nam">银联扫码</div>
            </div>
        </div>
    </c:if>
    <c:if test="${isFastRecharge}">
        <div class="list_pay_item_w" data-rel='{"opType":"href","target":"${rechargeUrlParam}"}'>
            <div class="list_pay_item">
                <i class="icon-pay kuaichong"></i>
                <div class="pay_nam">快充中心</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['counter']>0}">
        <div class="list_pay_item_w" href="#" data-rel='{"payType":"counter","url":"/wallet/v3/deposit/counter/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay wangyin-dep"></i>
                <div class="pay_nam">柜台存款</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['easy']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"easypay","url":"/wallet/v3/deposit/easypay/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay yishou-pay"></i>
                <div class="pay_nam">易收付</div>
            </div>
        </div>
    </c:if>
    <c:if test="${map['other']>0}">
        <div class="list_pay_item_w"  href="#" data-rel='{"payType":"other","url":"/wallet/v3/deposit/other/index.html","opType":"function","target":"toolBarClick"}'>
            <div class="list_pay_item">
                <i class="icon-pay other-pay"></i>
                <div class="pay_nam">其它</div>
            </div>
        </div>
    </c:if>
</div>