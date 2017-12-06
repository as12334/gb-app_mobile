<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resComRoot}/themes/public-mod.css">
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@ include file="../common/LeftMenu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav mui-bar-blue mui-bar-mine">
            <soul:button target="leftMenu" text="" opType="function" cssClass="mui-icon mui-action-menu mui-icon-bars mui-pull-left"/>
            <h1 class="mui-title">我的</h1>
            <%--<a href="" class="mui-icon icon-gift mui-pull-right"></a>--%>
            <soul:button target="goTab" skip="1" dataHref="/discounts/index.html?skip=1" isLeft="false" text="" opType="function" cssClass="mui-icon icon-gift mui-pull-right"/>
            <soul:button target="${root}/message/gameNotice.html" text="" opType="href" cssClass="mui-icon icon-message mui-pull-right" >
                <span class="mui-badge mui-badge-danger unReadCount"></span>
            </soul:button>
            <%--<a href="" class="mui-icon icon-message mui-pull-right">
                <span class="mui-badge mui-badge-danger unReadCount"></span>
            </a>--%>
        </header>
        <div class="mui-content mui-scroll-wrapper mui-content-without-footer-address mine-content">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <!--用户信息显示区域-->
                <div class="mui-row">
                    <div class="user-info-wrap">
                        <div class="gb-userinfo">
                            <div class="ct">
                                <img src="${resRoot}/images/avatar.png" class="avatar">
                                <p></p>
                                <%--<a class="btn mui-btn mui-btn-outlined user-logout">退出登录</a>--%>
                                <soul:button target="logout" text="退出登录" opType="function" cssClass="btn mui-btn mui-btn-outlined user-logout"></soul:button>
                            </div>
                            <p class="money-info">
			            <span class="span">
			                <span>钱包</span>
			                <span class="green"></span>
			            </span>
                                <span class="span">
			                <span>总资产</span>
			                <span class="orange"></span>
			            </span>
                            </p>
                        </div>
                    </div>
                </div>
                <!--九宫格导航区域-->
                <div class="mui-row">
                    <ul class="mui-list-unstyled list-mine-item">
                        <li class="mui-col-xs-4">
                            <soul:button target="${root}/wallet/deposit/index.html" text="" opType="href" cssClass="" >
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico1.png" class="mine-item-img"/>
						        </span>
                                <p>存款</p>
                            </soul:button>
                        </li>
                        <li class="mui-col-xs-4">
                            <soul:button target="${root}/wallet/withdraw/index.html" text="" opType="href">
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico2.png" class="mine-item-img"/>
						        </span>
                                <p>取款</p>
                                <span class="ext-info withdrawAmount"></span>
                            </soul:button>
                        </li>
                        <li class="mui-col-xs-4">
                            <soul:button target="${root}/transfer/index.html" text="" opType="href" cssClass="">
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico3.png" class="mine-item-img"/>
						        </span>
                                <p>额度转换</p>
                                <p>
                                    <small id="transferAmount"></small>
                                </p>
                            </soul:button>
                        </li>
                    </ul>
                    <ul class="mui-list-unstyled list-mine-item"><!--第二行-->
                        <li class="mui-col-xs-4">
                            <soul:button target="${root}/fund/record/index.html" text="" opType="href" cssClass="">
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico4.png" class="mine-item-img"/>
						        </span>
                                <p>资金记录</p>
                            </soul:button>
                        </li>
                        <li class="mui-col-xs-4">
                            <soul:button target="${root}/fund/betting/index.html" text="" opType="href" cssClass="">
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico5.png" class="mine-item-img"/>
						        </span>
                                <p>投注记录</p>
                            </soul:button>
                        </li>
                        <li class="mui-col-xs-4">
                            <soul:button target="${root}/promo/myPromo.html" text="" opType="href" cssClass="">
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico6.png" class="mine-item-img"/>
						        </span>
                                <p>优惠记录</p>
                                <span class="ext-info preferentialAmount"></span>
                            </soul:button>
                        </li>
                    </ul>
                    <c:set var="len" value="0"/>
                    <ul class="mui-list-unstyled list-mine-item"><!--第三行-->
                        <c:if test="${isCash}">
                            <li class="mui-col-xs-4">
                                <soul:button target="${root}/bankCard/page/addCard.html" text="" opType="href" cssClass="">
                                    <span class="item-img-wrap">
							            <img src="${resRoot}/images/my-ico7.png" class="mine-item-img"/>
                                    </span>
                                    <p>银行卡</p>
                                    <span id="bankImg" class="ext-info with-icon "> </span>
                                    <c:set var="len" value="${len+1}"/>
                                </soul:button>
                            </li>
                        </c:if>
                        <c:if test="${isBit}">
                            <li class="mui-col-xs-4">
                                <soul:button target="${root}/bankCard/page/addBtc.html" text="" opType="href" cssClass="">
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico12.png" class="mine-item-img"/>
						        </span>
                                    <p>比特币钱包</p>
                                    <p>
                                        <small id="btcNumber"></small>
                                    </p>
                                </soul:button>
                            </li>
                            <c:set var="len" value="${len+1}"/>
                        </c:if>
                        <li class="mui-col-xs-4">
                            <soul:button target="${root}/message/gameNotice.html?isSendMessage=true" text="" opType="href" cssClass="">
                                <span class="item-img-wrap">
                                    <img src="${resRoot}/images/my-ico9.png" class="mine-item-img"/>
                                </span>
                                <p>${views.mine_auto['申请优惠']}</p>
                            </soul:button>
                        </li>
                        <c:if test="${len!=2}">
                            <li class="mui-col-xs-4">
                                <soul:button target="${root}/passport/securityPassword/edit.html" text="" opType="href" cssClass="">
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico11.png" class="mine-item-img"/>
						        </span>
                                    <p>修改安全密码</p>
                                </soul:button>
                            </li>
                        </c:if>
                    </ul>
                    <ul class="mui-list-unstyled list-mine-item"><!--第四行-->
                        <c:if test="${len==2}">
                            <li class="mui-col-xs-4">
                                <soul:button target="${root}/passport/securityPassword/edit.html" text="" opType="href" cssClass="">
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico11.png" class="mine-item-img"/>
						        </span>
                                    <p>修改安全密码</p>
                                </soul:button>
                            </li>
                        </c:if>
                        <li class="mui-col-xs-4">
                            <soul:button target="${root}/my/password/editPassword.html" text="" opType="href" cssClass="">
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico10.png" class="mine-item-img"/>
						        </span>
                                <p>修改登录密码</p>
                            </soul:button>
                        </li>
                        <%--<li class="mui-col-xs-4">
                            <a href="">
						        <span class="item-img-wrap">
							        <img src="${resRoot}/images/my-ico8.png" class="mine-item-img"/>
						        </span>
                                <p>推荐好友</p>
                                <span class="ext-info recomdAmount"></span>
                            </a>
                        </li>--%>
                    </ul>
                </div>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
        <!--footer-->
        <%@ include file="../common/Footer.jsp" %>
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/common/Head.js"></script>
<script type="text/javascript" src="${resRoot}/js/membercentre/MemberCentre.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>