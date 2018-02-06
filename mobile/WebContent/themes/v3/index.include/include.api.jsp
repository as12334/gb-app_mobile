<%--@elvariable id="siteApiTypes" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteApiType>"--%>
<%--@elvariable id="fish" type="java.util.Map<java.lang.Integer,java.lang.String>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<%--<section class="api-grid">
    <c:forEach var="apiType" items="${siteApiTypes}" varStatus="status">
        <c:set var="show" value="${empty path && status.index == 0 ? 'active':''}"/>
        <c:set var="type" value=""/>
        <c:choose>
            <c:when test="${apiType.apiTypeId == 1}">
                <c:set var="type" value="live"/>
            </c:when>
            <c:when test="${apiType.apiTypeId == 2}">
                <c:set var="type" value="casino"/>
            </c:when>
            <c:when test="${apiType.apiTypeId == 3}">
                <c:set var="type" value="sports"/>
            </c:when>
            <c:when test="${apiType.apiTypeId == 4}">
                <c:set var="type" value="lottery"/>
            </c:when>
            <c:when test="${apiType.apiTypeId == 5}">
                <c:set var="type" value="Chess"/>
            </c:when>
        </c:choose>
        <ul class="mui-table-view mui-grid-view mui-grid-9 ${show}" data-list="${type}">
            <c:choose>
                <c:when test="${apiType.apiTypeId==2 || apiType.apiTypeId==5}">
                    <c:forEach var="i18n" items="${apiType.apiTypeRelations}">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a data-rel='{"target":"${root}/game/getGameByApiId.html?search.apiId=${i18n.apiId}&search.apiTypeId=${i18n.apiTypeId}","opType":"href"}'>
                                <span class="api-item api-icon-${i18n.apiTypeId}-${i18n.apiId}"></span> <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">${i18n.apiName}</div>
                            </a>
                        </li>
                    </c:forEach>
                </c:when>
                <c:when test="${apiType.apiTypeId==4}">
                    <!--彩票导航-->
                    <%@include file="../game/Lottery.jsp" %>
                </c:when>
                <c:otherwise>
                    <c:forEach var="i18n" items="${apiType.apiTypeRelations}">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a data-rel='{"dataApiId":"${i18n.apiId}","dataApiTypeId":"${i18n.apiTypeId}","dataApiName":"${i18n.apiName}","dataStatus":"${i18n.apiStatus}","target":"goApiGame","opType":"function"}' class="_api">
                                <span class="api-item api-icon-${i18n.apiTypeId}-${i18n.apiId}"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">${i18n.apiName}</div>
                            </a>
                        </li>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </ul>
    </c:forEach>
    <ul class="mui-table-view mui-grid-view mui-grid-9" data-list="fish">
        <c:forEach var="i" items="${fish}">
            <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                <a data-rel='{"target":"${root}/game/getGameByApiId.html?search.apiId=${i.key}&search.apiTypeId=2&search.gameType=Fish","opType":"href"}'>
                    <span class="api-item api-icon-2-${i.key}"></span>
                    <div class="mui-media-body">${i.value}</div>
                </a>
            </li>
        </c:forEach>
        <c:if test="${fn:length(fish)<=0}">
            <div class="deficiency-nots">${views.themes_auto['没有找到符合的游戏']}</div>
        </c:if>
    </ul>
    <!--关于我们-->
    &lt;%&ndash;<div class="mui-table-view mui-grid-view mui-grid-9 ${not empty path && path == 'about' ? 'active':''}" data-list="about">
        <c:if test="${not empty about}">
            <c:set var="c" value="${about.content == null ? '' : about.content}" />
            ${c.replace("${company}", siteName)}
        </c:if>
    </div>&ndash;%&gt;
    <!--注册条款-->
    &lt;%&ndash;<div class="mui-table-view mui-grid-view mui-grid-9 ${not empty path && path == 'terms' ? 'active':''}" data-list="terms">
        <c:if test="${not empty terms}">
            ${not empty terms.value ? terms.value : terms.defaultValue}
        </c:if>
    </div>&ndash;%&gt;
</section>--%>

<div class="swiper-container nav-slide-content  api-grid">
    <div class="swiper-wrapper">
        <c:forEach var="apiType" items="${siteApiTypes}" varStatus="status">
            <c:set var="show" value="${status.index == 0 ? 'active':''}"/>
            <c:set var="type" value=""/>
            <c:choose>
                <c:when test="${apiType.apiTypeId == 1}">
                    <c:set var="type" value="live"/>
                </c:when>
                <c:when test="${apiType.apiTypeId == 2}">
                    <c:set var="type" value="casino"/>
                </c:when>
                <c:when test="${apiType.apiTypeId == 3}">
                    <c:set var="type" value="sports"/>
                </c:when>
                <c:when test="${apiType.apiTypeId == 4}">
                    <c:set var="type" value="lottery"/>
                </c:when>
                <c:when test="${apiType.apiTypeId == 5}">
                    <c:set var="type" value="chess-and-card"/>
                </c:when>
            </c:choose>
            <%--<ul class="mui-table-view mui-grid-view mui-grid-9 ${show}" data-list="${type}">
                <c:choose>
                    <c:when test="${apiType.apiTypeId==2 || apiType.apiTypeId==5}">
                        <c:forEach var="i18n" items="${apiType.apiTypeRelations}">
                            <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                <a data-rel='{"target":"${root}/game/getGameByApiId.html?search.apiId=${i18n.apiId}&search.apiTypeId=${i18n.apiTypeId}","opType":"href"}'>
                                    <span class="api-item api-icon-${i18n.apiTypeId}-${i18n.apiId}"></span> <!--根据class的不同来显示api图标-->
                                    <div class="mui-media-body">${i18n.apiName}</div>
                                </a>
                            </li>
                        </c:forEach>
                    </c:when>
                    <c:when test="${apiType.apiTypeId==4}">
                        <!--彩票导航-->
                        <%@include file="../game/Lottery.jsp" %>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="i18n" items="${apiType.apiTypeRelations}">
                            <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                <a data-rel='{"dataApiId":"${i18n.apiId}","dataApiTypeId":"${i18n.apiTypeId}","dataApiName":"${i18n.apiName}","dataStatus":"${i18n.apiStatus}","target":"goApiGame","opType":"function"}' class="_api">
                                    <span class="api-item api-icon-${i18n.apiTypeId}-${i18n.apiId}"></span>  <!--根据class的不同来显示api图标-->
                                    <div class="mui-media-body">${i18n.apiName}</div>
                                </a>
                            </li>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </ul>--%>
            <div class="swiper-slide slide-${type}">
                <ul class="mui-table-view mui-grid-view mui-grid-9 ${show}">

                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                        <a href="#">
                            <span class="api-item api-icon-1-10"></span>  <!--根据class的不同来显示api图标-->
                            <div class="mui-media-body">BBIN</div>
                        </a>
                    </li>
                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                        <a href="#">
                            <span class="api-item api-icon-1-24"></span>  <!--根据class的不同来显示api图标-->
                            <div class="mui-media-body">OPUS</div>
                        </a>
                    </li>
                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                        <a href="#">
                            <span class="api-item api-icon-1-9"></span>  <!--根据class的不同来显示api图标-->
                            <div class="mui-media-body">AG</div>
                        </a>
                    </li>
                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                        <a href="#">
                            <span class="api-item api-icon-1-16"></span>  <!--根据class的不同来显示api图标-->
                            <div class="mui-media-body">EBET</div>
                        </a>
                    </li>
                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                        <a href="#">
                            <span class="api-item api-icon-1-7"></span>  <!--根据class的不同来显示api图标-->
                            <div class="mui-media-body">OG</div>
                        </a>
                    </li>
                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                        <a href="#">
                            <span class="api-item api-icon-1-17"></span>  <!--根据class的不同来显示api图标-->
                            <div class="mui-media-body">SA</div>
                        </a>
                    </li>
                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                        <a href="#">
                            <span class="api-item api-icon-1-5"></span>  <!--根据class的不同来显示api图标-->
                            <div class="mui-media-body">GD</div>
                        </a>
                    </li>
                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                        <a href="#">
                            <span class="api-item api-icon-1-1"></span>  <!--根据class的不同来显示api图标-->
                            <div class="mui-media-body">DS</div>
                        </a>
                    </li>
                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                        <a href="#">
                            <span class="api-item api-icon-1-33"></span>  <!--根据class的不同来显示api图标-->
                            <div class="mui-media-body">SUNBET</div>
                        </a>
                    </li>
                </ul>
            </div>
        </c:forEach>
        <%--<div class="swiper-slide slide-live">
            <ul class="mui-table-view mui-grid-view mui-grid-9 active">
                <div class="deficiency-nots">没有找到符合的游戏</div>
                <!--api加载时的样式-->
                <div class="api-loading">
                    <div class="loader api-loader">
                        <div class="loader-inner ball-pulse api-div">
                            <div></div>
                            <div></div>
                            <div></div>
                        </div>
                    </div>
                </div>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-1-10"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">BBIN</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-1-24"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">OPUS</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-1-9"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">AG</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-1-16"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">EBET</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-1-7"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">OG</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-1-17"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">SA</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-1-5"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">GD</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-1-1"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">DS</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-1-33"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">SUNBET</div>
                    </a>
                </li>
            </ul>
        </div>
        <div class="swiper-slide slide-casino" >
            <ul class="mui-table-view mui-grid-view mui-grid-9 active" >
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="casino-list.html">
                        <span class="api-item api-icon-2-26"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">PNG</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-2-20"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">BSG</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-2-6"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">PT</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-2-25"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">SG</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-2-9"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">AG</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-2-10"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">BBIN</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-2-15"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">HB</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-2-3"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">MG</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-2-27"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">DT</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-2-31"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">GNS</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-2-32"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">PRG</div>
                    </a>
                </li>
            </ul>
        </div>
        <div class="swiper-slide slide-lottery" >
            <ul class="mui-table-view mui-grid-view mui-grid-9 active" data-list="lottery">
                <!--彩票导航-->
                <div class="lottery-nav">
                    <div class="mui-scroll-wrapper mui-slider-indicatorcode mui-segmented-control mui-segmented-control-inverted" data-scroll="7">
                        <div class="mui-scroll">
                            <ul class="mui-list-unstyled mui-clearfix mui-bar-tab">
                                <li><a href="javascript:void(0);" class="mui-tab-item mui-active api-icon-4-22-2">一指通彩票</a></li>
                                <li><a href="javascript:void(0);" class="mui-tab-item api-icon-4-2">KG彩票</a></li>
                                <li><a href="javascript:void(0);" class="mui-tab-item api-icon-4-10">BBIN彩票</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="lottery-content"><!--彩票内容切换-->
                    <div  class="mui-control-content mui-active">
                        <div class="deficiency-nots">没有找到符合的游戏</div>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-AHfast3.png" alt="" class="lottery-img">
                                <div class="mui-media-body">安徽快3</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-BJkeno.png" alt="" class="lottery-img">
                                <div class="mui-media-body">北京快乐8</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-CQssc.png" alt="" class="lottery-img">
                                <div class="mui-media-body">重庆时时彩</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-fc3d.png" alt="" class="lottery-img">
                                <div class="mui-media-body">福彩3D</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-ffc.png" alt="" class="lottery-img">
                                <div class="mui-media-body">两分时时彩</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-GDklsf.png" alt="" class="lottery-img">
                                <div class="mui-media-body">快乐10分</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-GXfast3.png" alt="" class="lottery-img">
                                <div class="mui-media-body">广西快3</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-HBfast3.png" alt="" class="lottery-img">
                                <div class="mui-media-body">湖北快3</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-JSfast3.png" alt="" class="lottery-img">
                                <div class="mui-media-body">江苏快3</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-mark6.png" alt="" class="lottery-img">
                                <div class="mui-media-body">六合彩</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-pk10.png" alt="" class="lottery-img">
                                <div class="mui-media-body">北京PK10</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-sfc.png" alt="" class="lottery-img">
                                <div class="mui-media-body">三分时时彩</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-ssc.png" alt="" class="lottery-img">
                                <div class="mui-media-body">分分时时彩</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-tcPl3.png" alt="" class="lottery-img">
                                <div class="mui-media-body">排列3</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-TJssc.png" alt="" class="lottery-img">
                                <div class="mui-media-body">天津时时彩</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-wfc.png" alt="" class="lottery-img">
                                <div class="mui-media-body">五分时时彩</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-XJssc.png" alt="" class="lottery-img">
                                <div class="mui-media-body">新疆时时彩</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-xyFt.png" alt="" class="lottery-img">
                                <div class="mui-media-body">幸运飞艇</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/lt-xyNc.png" alt="" class="lottery-img">
                                <div class="mui-media-body">幸运农场</div>
                            </a>
                        </li>
                    </div>
                    <div class="mui-control-content">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/kg_c6.png" alt="" class="lottery-img">
                                <div class="mui-media-body">快乐彩</div>
                            </a>
                        </li>
                    </div>
                    <div class="mui-control-content">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c1.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c2.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c3.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c4.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c5.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c6.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c7.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c8.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c9.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c10.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c11.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c12.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c13.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c14.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c15.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <img src="../../mobile-v3/images/lottery/bb_c16.png" alt="" class="lottery-img">
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                    </div>
                </div><!--彩票内容切换结束-->
            </ul>
        </div>
        <div class="swiper-slide slide-sports" >
            <ul class="mui-table-view mui-grid-view mui-grid-9 active" >
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-3-12"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">HG</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-3-19"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">SHABA</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <span class="api-item api-icon-3-4"></span>  <!--根据class的不同来显示api图标-->
                        <div class="mui-media-body">IM</div>
                    </a>
                </li>
            </ul>
        </div>
        <div class="swiper-slide slide-fish" >
            <ul class="mui-table-view mui-grid-view mui-grid-9 active">
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/fish/f-ag.png" alt="" class="fish-img" />
                        <div class="mui-media-body">AG捕鱼</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/fish/f-pt.png" alt="" class="fish-img" />
                        <div class="mui-media-body">PT深海大赢家</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/fish/f-gns.png" alt="" class="fish-img" />
                        <div class="mui-media-body">GNS寻宝捕鱼</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/fish/f-gg.png" alt="" class="fish-img" />
                        <div class="mui-media-body">GG捕鱼</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/fish/f-bb-dr.png" alt="" class="fish-img" />
                        <div class="mui-media-body">BB捕鱼达人</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/fish/f-bb-ds.png" alt="" class="fish-img" />
                        <div class="mui-media-body">BB捕鱼大师</div>
                    </a>
                </li>
            </ul>
        </div>
        <div class="swiper-slide slide-chess-and-card" >
            <ul class="mui-table-view mui-grid-view mui-grid-9 active">
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/card/sg.png" alt="" class="card-img" />
                        <div class="mui-media-body">三公</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/card/zjh.png" alt="" class="card-img" />
                        <div class="mui-media-body">扎金花</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/card/qznn.png" alt="" class="card-img" />
                        <div class="mui-media-body">抢庄牛牛</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/card/dz.png" alt="" class="card-img" />
                        <div class="mui-media-body">德州扑克</div>
                    </a>
                </li>
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a href="#">
                        <img src="../../mobile-v3/images/card/ebg.png" alt="" class="card-img" />
                        <div class="mui-media-body">二八杠</div>
                    </a>
                </li>
            </ul>
        </div>--%>
    </div>
</div>