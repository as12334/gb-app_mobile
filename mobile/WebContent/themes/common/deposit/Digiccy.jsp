<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<body class="gb-theme mine-page">
<div id="offCanvasWrapper mui-fullscreen" class="mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <%@ include file="/include/include.toolbar.jsp" %>
            <h1 class="mui-title">${views.deposit_auto['比特币支付']}</h1>
        </header>
        <div class="mui-content ${os eq 'app_ios' ? 'mui-scroll-wrapper':''}" ${os eq 'android'?'style="padding-top:0!important"':''}>
            <div class="mui-scroll">
                <div class="mui-row">
                    <div class="mui-input-group mine-form">
                        <div class="bank-selector">
                            <div class="ct">
                                <ul>
                                    <c:forEach var="i" items="${userDigiccyList}" varStatus="vs">
                                        <li><a href="#" data-id="digiccy${i.currency}" class="${vs.index==0?'active':''}">${dicts.digiccy.digiccy_currency[i.currency]}</a></li>
                                    </c:forEach>
                                    <div class="clearfix"></div>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                <c:forEach items="${userDigiccyList}" var="i" varStatus="vs">
                    <div id="digiccy${i.currency}">
                    <div class="mui-row xzzf-wrap" style="${vs.index==0?'':'display:none'}" name="account${i.currency}">
                        <div class="mui-input-group mine-form m-t-sm">
                            <div class="mui-input-row title-wrap">
                                <span class="title">${dicts.digiccy.digiccy_currency[i.currency]}</span>
                                <span class="mui-pull-right m-r-sm">余额&nbsp;<span class="text-green ye-num"><fmt:formatNumber value="${empty i.amount?0:i.amount}" pattern="#.########"/></span>
                                <button type="button" class="mui-btn" name="refresh" currency="${i.currency}">${views.themes_auto['刷新']}</button></span>
                            </div>

                            <div class="mui-input-row" style="${empty i.addressQrcodeUrl?'display:none':''}">
                                <div class="list-xzzf">
                                    <img src="${i.addressQrcodeUrl}"/>
                                    <p class="info">${views.themes_auto['扫描二维码完成支付']}</p>
                                </div>
                            </div>
                            <div class="mui-input-row" style="${empty i.address?'display:none':''}">
                                <div class="list-xzzf">
                                    <textarea class="textarea" readonly>${i.address}</textarea>
                                    <p class="info">${views.themes_auto['复制数字货币的地址完成支付']}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="mui-row" name="exchange${i.currency}" style="${i.amount>0?'':'display:none'}">
                        <div class="gb-form-foot">
                            <a href="#" class="mui-btn mui-btn-primary submit" name="exchange" currency="${i.currency}">${views.themes_auto['兑换金额']}</a>
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
                            <a href="#" class="mui-btn mui-btn-primary submit" name="newAddress" currency="${i.currency}">${views.themes_auto['生成地址']}</a>
                        </div>
                    </div>
                    </div>
                </c:forEach>
            </div>
        </div>
        <%--优化弹窗--%>
        <div class="d-none" id="applySale">

        </div>
    </div>
</div>
<script>
    curl(['site/deposit/Digiccy'], function (Page) {
        page = new Page();
    });
</script>
</body>