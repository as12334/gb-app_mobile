<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@ include file="../common/LeftMenu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <%@include file="../common/Head.jsp" %>
        <div class="mui-content mui-scroll-wrapper" id="pullrefresh">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <!--优惠列表-->
                <section class="promo">
                    <div class="promo-sorts">
                        <soul:button activityType="" target="activityType" text="全部" opType="function"
                                     cssClass="mui-btn btn-promo-sort active"/>
                    </div>
                    <ul class="promo-list mui-list-unstyled">
                    </ul>
                </section>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
        <!--footer-->
        <%@ include file="../common/Footer.jsp" %>
    </div>
</div>
<input value="1" id="pageNumber" hidden>
<input value="1" id="lastPageNumber" hidden>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/common/Head.js"></script>
<script type="text/javascript" src="${resRoot}/js/discounts/Promo.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>