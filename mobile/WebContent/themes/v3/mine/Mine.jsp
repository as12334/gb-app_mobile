<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/default/style1.css?v=${rcVersion}" />
</head>

<body class="mine_page">
<!-- 菜单容器 -->
<%@ include file="../common/LeftMenu.jsp" %>
<!-- 主页面容器 -->
<div class="mui-scroll-wrapper">
    <div class="mui-scroll">
        <div class="user_info_part">
            <!-- 主页面标题 -->
            <header class="mui-bar mui-bar-nav mui-bar-mine" >
                <soul:button target="leftMenu" text="" opType="function"
                             cssClass="mine_settings"/>
                <h1 class="mui-title">我的</h1>
                <soul:button target="${root}/message/gameNotice.html" text="" opType="href"
                             cssClass="msg_notice">
                    <sup class="unReadCount"></sup>
                </soul:button>
            </header>
            <!--用户信息显示区域-->
            <div class="user_portrait_part login-name _user-login-time">
                <i></i>
                <span></span>
                <b></b>
                <div class="dis-no login-name">
                    <a href="#"class="part-a">${views.themes_auto['登录']} / ${views.themes_auto['注册']}</a>
                </div>
            </div>
        </div>
        <div class="content_container">
            <!-- 主界面具体展示内容 -->
            <ul class="mui-content function_nav" >
                <!--总资产 & 钱包余额-->
                <li class="mui-row user_asset_info">
                    <div class="mui-col-xs-6 total_asset">
                        <i></i>
                        <span>${views.themes_auto['总资产']}/</span>
                    </div>
                    <div class="mui-col-xs-6 wallet_balance">
                        <i></i>
                        <span>${views.themes_auto['钱包余额']}/</span>
                    </div>
                </li>

                <!--充值 & 提现-->
                <li class="mui-row charge_withdraw">
                    <soul:button target="goTab" skip="0" dataHref="/wallet/v3/deposit/index.html" text=""
                                 opType="function" cssClass="mui-col-xs-6 charge_item">
                        <i></i>
                        <span>${views.themes_auto['充值']}</span>
                    </soul:button>
                    <soul:button target="${root}/wallet/withdraw/index.html" text="" opType="href"
                                 cssClass="mui-col-xs-6 withdraw_item">
                        <i></i>
                        <span>${views.themes_auto['提现']}</span>
                        <small class="_withdraw-amount"></small>
                    </soul:button>
                </li>

                <!--投注记录 & 资金账户-->
                <li class="mui-row func_nav_item sep_top">
                    <soul:button target="${root}/fund/betting/index.html" text="" opType="href" cssClass="mui-col-xs-6 func_nav_label">
                        <i class="bet_record_icon"></i>
                        <span class="func_nav_name">
                        <span>${views.themes_auto['投注记录']}</span>
                        <small>${views.themes_auto['查看所参与的游戏记录']}</small>
                        </span>
                    </soul:button>
                    <soul:button target="${root}/transfer/index.html" text="" opType="href" cssClass="mui-col-xs-6 func_nav_label">
                        <i class="asset_account_icon"></i>
                        <span class="func_nav_name" >
                        <span>${views.themes_auto['额度转换']}</span>
                        <small>${views.themes_auto['进行资金回收和转入']}</small>
                    </span>
                    </soul:button>
                </li>

                <!--资金记录 & 安全中心-->
                <li class="mui-row func_nav_item">
                    <soul:button target="${root}/fund/record/index.html" text="" opType="href" cssClass="mui-col-xs-6 func_nav_label">
                        <i class="money_record_icon"></i>
                        <span class="func_nav_name">
                        <span>${views.themes_auto['资金记录']}</span>
                        <small id="transferAmount">${views.themes_auto['查看所有资金来往记录']}</small>
                    </span>
                    </soul:button>
                    <soul:button  target="${root}/mine/accountSecurity.html" text="" opType="href" cssClass="mui-col-xs-6 func_nav_label">
                        <i class="security_center_icon"></i>
                        <span class="func_nav_name">
                        <span>${views.themes_auto['账户安全']}</span>
                        <small>${views.themes_auto['保护您的账户安全']}</small>
                    </span>
                    </soul:button>
                </li>
                <!--优惠记录 & 消息中心-->
                <li class="mui-row func_nav_item sep_btm three_items">
                    <soul:button target="${root}/promo/myPromo.html" text="" opType="href" cssClass="mui-col-xs-6 func_nav_label">
                        <i class="promo_record_icon"></i>
                        <span class="func_nav_name ">
                        <span>${views.themes_auto['优惠记录']}</span>
                        <small class="preferentialAmount">${views.themes_auto['查看所参与的优惠纪录']}</small>
                    </span>
                    </soul:button>
                    <soul:button target="${root}/message/gameNotice.html" text="" opType="href" cssClass="mui-col-xs-6 func_nav_label">
                        <i class="msg_center_icon"></i>
                        <span class="func_nav_name">
                        <span>${views.themes_auto['消息中心']}</span>
                        <small>${views.themes_auto['查看历史消息内容']}</small>
                    </span>
                    </soul:button>
                </li>

                <!--分享朋友 & 申请优惠-->
                <li class="mui-row func_nav_item sep_btm">
                    <soul:button target="${root}/recommend.html" text="" opType="href" cssClass="mui-col-xs-6 func_nav_label">
                        <i class="share_to_friend_icon"></i>
                        <span class="func_nav_name">
                        <span>${views.themes_auto['分享朋友']}</span>
                        <small class="recomdAmount" style="display:block; ">${views.themes_auto['查看分享奖励']}</small>
                    </span>
                    </soul:button>
                    <soul:button target="${root}/message/gameNotice.html?isSendMessage=true" text=""
                                 opType="href" cssClass="mui-col-xs-6 func_nav_label">
                        <i class="send_apply_icon"></i>
                        <span class="func_nav_name">
                        <span>${views.themes_auto['发送申请']}</span>
                        <small class="processing">${views.themes_auto['发送优惠申请优惠']}</small>
                    </span>
                    </soul:button>
                </li>
                <!--绑定手机-->
                <li class="mui-row func_nav_item last_item">
                    <soul:button target="${root}/help/bindMobile.html" text=""
                                 opType="href" cssClass="mui-col-xs-6 func_nav_label">
                        <img src="${resRoot}/images/my_ico_11.png" class="dis-in"style="transform: none; width: 33px; height:31px" alt="">
                        <%--<i class="clear_cache_icon"></i>--%>
                        <span class="func_nav_name">
                        <span>${views.themes_auto['绑定手机']}</span>
                        <small>${views.themes_auto['绑定手机']}</small>
                    </span>
                    </soul:button>
                </li>
            </ul>
        </div>
    </div>
</div>

<!--footer-->
<%@ include file="../common/Footer.jsp" %>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/membercentre/MemberCentre.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
