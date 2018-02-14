<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<head>
    <%@include file="../include/include.head.jsp" %>
</head>
<body class="online-pay-sz">
<header class="mui-bar mui-bar-nav">
    <a class="mui-icon mui-icon mui-icon-left-nav mui-pull-left mui-action-back"></a>
    <h1 class="mui-title">${views.themes_auto['数字货币支付']}</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <div class="mui-row">
            <div class="mui-input-group mine-form">
                <div class="bank-selector">
                    <div class="ct">
                        <ul>
                            <c:forEach var="i" items="${userDigiccyList}" varStatus="vs">
                                <li><a data-rel='{"opType":"function","target":"changeCurrency","id":"digiccy${i.currency}"}' class="${vs.index==0?'active':''}">${dicts.digiccy.digiccy_currency[i.currency]}</a></li>
                            </c:forEach>
                            <div class="clearfix"></div>
                        </ul></div>
                </div>
            </div>
        </div>
        <c:forEach items="${userDigiccyList}" var="i" varStatus="vs">
            <div id="digiccy${i.currency}">
                <div class="mui-row xzzf-wrap" style="${vs.index==0?'':'display:none'}" name="account${i.currency}">
                    <div class="mui-input-group mine-form m-t-sm">
                        <div class="mui-input-row title-wrap">
                            <span class="title">${dicts.digiccy.digiccy_currency[i.currency]}</span>
                            <span class="mui-pull-right m-r-sm">${views.themes_auto['余额']}&nbsp; <span class="text-green ye-num"><fmt:formatNumber value="${empty i.amount?0:i.amount}" pattern="#.########"/></span>
                                <a data-rel='{"opType":"function","target":"refresh","currency":"${i.currency}"}' name="refresh" class="mui-btn">${views.themes_auto['刷新']}</a>
                            </span>
                        </div>

                        <div class="mui-input-row" style="${empty i.addressQrcodeUrl?'display:none':''}">
                            <div class="list-xzzf">
                                <img src="${i.addressQrcodeUrl}">
                                <p class="info">${views.themes_auto['扫描二维码完成支付']}</p>
                            </div>
                        </div>
                        <div class="mui-input-row" style="${empty i.address?'display:none':''}">
                            <div class="list-xzzf">
                                <textarea class="textarea" placeholder="" disabled="">${i.address}</textarea>
                                <p class="info">${views.themes_auto['复制数字货币的地址完成支付']}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="mui-row" name="exchange${i.currency}" style="${i.amount>0?'':'display:none'}">
                    <div class="gb-form-foot">
                        <a id="show-t" data-rel='{"opType":"function","target":"exchange","currency":"${i.currency}"}' class="mui-btn mui-btn-primary submit" name="exchange">${views.themes_auto['兑换金额']}</a>
                    </div>
                </div>
                <div name="notAddress${i.currency}" class="mui-row" style="${empty i.address?'':'display:none'}">
                    <div class="mui-input-group mine-form m-t-sm">
                        <div class="mui-input-row">
                            <div class="list-xzzf" style="text-align: center">
                                <p class="info">${views.themes_auto['还未生成地址']}</p>
                            </div>
                        </div>
                    </div>
                    <div class="gb-form-foot">
                        <a data-rel='{"opType":"function","target":"newAddress","currency":"${i.currency}"}' class="mui-btn mui-btn-primary submit" name="newAddress">${views.themes_auto['生成地址']}</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
<%--优化弹窗--%>
<div class="d-none" id="applySale">
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script src="${resComRoot}/js/dist/clipboard.js"></script>
<script src="${resRoot}/js/deposit/ThirdPartyPay.js"></script>
<script src="${resRoot}/js/deposit/DepositCenter.js"></script>
