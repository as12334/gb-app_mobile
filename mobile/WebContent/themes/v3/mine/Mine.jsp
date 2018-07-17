<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/default/style1.css?v=${rcVersion}"/>
</head>

<body>
<!-- 菜单容器 -->
<%@ include file="../common/LeftMenu.jsp" %>
<!-- 主页面容器 -->
<div class="mui-inner-wrap">
    <!-- 主页面标题 -->
    <header class="mui-bar mui-bar-nav mui-bar-blue mui-bar-mine">
        <soul:button target="leftMenu" text="" opType="function"
                     cssClass="mui-icon mui-action-menu mui-icon-bars mui-pull-left"/>
        <h1 class="mui-title">${views.themes_auto['我的']}</h1>
        <soul:button target="goTab" skip="1" dataHref="/discounts/index.html?skip=1" isLeft="false" text=""
                     opType="function" cssClass="mui-icon icon-gift mui-pull-right"/>
        <soul:button target="${root}/message/gameNotice.html" text="" opType="href"
                     cssClass="mui-icon icon-message mui-pull-right">
            <span class="mui-badge mui-badge-danger unReadCount"></span>
        </soul:button>
    </header>
    <main class="mui-content part-r"style="    padding-bottom: 53px;">
        <!-- 主界面具体展示内容 -->
        <!--用户信息显示区域-->
        <div class="mui-row part-r"style="margin: 2px 0;padding: 0;">
            <img src="${resRoot}/images/my_ico_12.png" class="mui-col-xs-12" alt="">
        </div>
        <div class="mui-row part-a"style="width: 100%;top: 8%;">
            <div class=" mui-col-xs-6"style="">
                <img src="${resRoot}/images/my_ico_14.png" class="head-portrait dis-in" alt="">
                <div class="dis-in login-name _user-login-time">
                    <span></span>
                    <h6 class="mt10"></h6>
                    <small></small>
                </div>
                <div class="dis-no login-name">
                    <a href="#"class="part-a">${views.themes_auto['登录']} / ${views.themes_auto['注册']}</a>
                </div>
            </div>
            <div  class=" mui-col-xs-6 part-r"style="">
                <div></div>
                <ul class="bor-radius part-a"style="">
                    <li class="pr10 _total-assets"style="border-bottom: 1px solid #ddd;">
                        <small>${views.themes_auto['总资产']}/</small>
                        <span style="color: #1b75d9;"></span>
                    </li>
                    <li class="pr10 _purse-balance"style="">
                        <span  style="color: #0bba87;"></span>
                        <small>${views.themes_auto['钱包余额']}/</small>
                    </li>
                </ul>
            </div>
        </div>

        <!--图标导航区域-->
        <ui class="mui-row"style="color: #000;">
            <li class="mui-col-xs-6 mt10  mui-text-center">
                <soul:button target="goTab" skip="0" dataHref="/wallet/v3/deposit/index.html" text=""
                             opType="function" cssClass="">
                    <img src="${resRoot}/images/my_ico_1.png" class="dis-in" alt="">
                    <div style="margin-top: 2%;"class="dis-in mui-text-left">
                        <div>${views.themes_auto['充值']}</div>
                        <small></small>
                    </div>
                </soul:button>
            </li>
            <li class="mui-col-xs-6 mt10 mui-text-center ">
                <soul:button target="${root}/wallet/withdraw/index.html" text="" opType="href">
                    <img src="${resRoot}/images/my_ico_2.png" class="dis-in" alt="">
                    <div class="dis-in mui-text-left"style="">
                        <div>${views.themes_auto['提现']}</div>
                        <small class="_withdraw-amount" style="display:block; "></small>
                    </div>
                </soul:button>
            </li>
            <li class="mui-col-xs-6 mt10 mui-text-left border-bottom">
                <soul:button target="${root}/fund/betting/index.html" text="" opType="href" cssClass="pl10 pt10">
                    <img src="${resRoot}/images/my_ico_3.png" class="dis-in" alt="">
                    <div class="dis-in " style="">
                        <div>${views.themes_auto['投注记录']}</div>
                        <small style="display:block; ">${views.themes_auto['查看所参与的游戏记录']}</small>
                    </div>
                </soul:button>
            </li>
            <li class="mui-col-xs-6 mt10 mui-text-left  border-bottom">
                <soul:button target="${root}/transfer/index.html" text="" opType="href" cssClass="pl10 pt10">
                    <img src="${resRoot}/images/my_ico_4.png" class="dis-in" alt="">
                    <div class="dis-in " style="">
                        <div>${views.themes_auto['额度转换']}</div>
                        <small id="transferAmount" style="display:block; ">${views.themes_auto['进行资金回收和转入']}</small>
                    </div>
                </soul:button>
            </li>
            <li class="mui-col-xs-6 mui-text-left border-bottom">
                <soul:button target="${root}/fund/record/index.html" text="" opType="href" cssClass="pl10 pt10">
                    <img src="${resRoot}/images/my_ico_5.png" class="dis-in" alt="">
                    <div style=""class="dis-in ">
                        <div>${views.themes_auto['资金记录']}</div>
                        <small style="display:block; ">${views.themes_auto['查看所有资金来往记录']}</small>
                    </div>
                </soul:button>
            </li>
            <li class="mui-col-xs-6 mui-text-left border-bottom">
                <soul:button  target="${root}/mine/accountSecurity.html" text="" opType="href" cssClass="pl10 pt10">
                    <img src="${resRoot}/images/my_ico_6.png" class="dis-in" alt="">
                    <div style=""class="dis-in ">
                        <div>${views.themes_auto['账户安全']} <span class="mui-badge mui-badge-danger">${views.themes_auto['低']}</span></div>
                        <small style="display:block; ">${views.themes_auto['保护您的账户安全']}</small>
                    </div>
                </soul:button>
            </li>
            <li class="mui-col-xs-6 mb10 mui-text-left">
                <soul:button target="${root}/promo/myPromo.html" text="" opType="href" cssClass="pl10 pt10">
                    <img src="${resRoot}/images/my_ico_7.png" class="dis-in" alt="">
                    <div style="" class="dis-in ">
                        <div>${views.themes_auto['优惠记录']}</div>
                        <small style="display:block; " class="preferentialAmount">${views.themes_auto['查看所参与的优惠纪录']}</small>
                    </div>
                </soul:button>
            </li>
            <li class="mui-col-xs-6 mb10 mui-text-left ">
                <soul:button target="${root}/message/gameNotice.html" text="" opType="href"
                             cssClass="pl10 pt10">
                    <img src="${resRoot}/images/my_ico_8.png" class="dis-in" alt="">
                    <div style="" class="dis-in ">
                        <div>${views.themes_auto['消息中心']}</div>
                        <small style="display:block; ">${views.themes_auto['查看历史消息内容']}</small>
                    </div>
                </soul:button>
            </li>
            <li class="mui-col-xs-6 mui-text-left border-bottom">
                <soul:button target="${root}/recommend.html" text="" opType="href" cssClass="pl10 pt10">
                    <img src="${resRoot}/images/my_ico_10.png" class="dis-in" alt="">
                    <div style="" class="dis-in ">
                        <div>${views.themes_auto['分享朋友']}</div>
                        <small  class="recomdAmount" style="display:block; ">${views.themes_auto['查看分享奖励']}</small>
                    </div>
                </soul:button>
            </li>
            <li class="mui-col-xs-6 mui-text-left border-bottom">
                <soul:button target="${root}/message/gameNotice.html?isSendMessage=true" text=""
                             opType="href" cssClass="pl10 pt10">
                    <img src="${resRoot}/images/my_ico_9.png" class="dis-in" alt="">
                    <div style="" class="dis-in ">
                        <div>${views.themes_auto['发送申请']}</div>
                        <small style="display:block; ">${views.themes_auto['发送优惠申请优惠']}</small>
                    </div>
                </soul:button>
            </li>

            <c:if test="${len !=2}">
                <li class="mui-col-xs-6 mui-text-left " id="phone">
                    <a data-rel='{"target":"${root}/help/bindMobile.html","opType":"href"}' class="pl10 pt10">
                        <img src="${resRoot}/images/my_ico_11.png" class="dis-in"style="transform: none;" alt="">
                        <div style="" class="dis-in">
                            <div>${views.themes_auto['绑定手机']}</div>
                            <small style="display:block; ">${views.themes_auto['绑定手机']}</small>
                        </div>
                    </a>
                </li>
            </c:if>
        </ui>
    </main>  <!--mui-content 闭合标签-->
    <!--footer-->
    <%@ include file="../common/Footer.jsp" %>
    <!-- off-canvas backdrop -->
    <div class="mui-off-canvas-backdrop"></div>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/membercentre/MemberCentre.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>