<%--
  Created by IntelliJ IDEA.
  User: bill
  Date: 16-12-26
  Time: 下午10:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    /*跨webview需要手动指定位置*/
    .scroll-popover {
        position: fixed;
        top: 16px;
        right: 6px;
    }

    .scroll-popover .mui-popover-arrow {
        left: auto;
        right: 6px;
    }
</style>

<div class="mui-scroll-wrapper popover-scroll">
    <div class="mui-scroll">
        <ul class="mui-table-view">
            <li class="mui-table-view-cell"><a href="#" value="today">${views.include_auto['今天']}</a></li>
            <li class="mui-table-view-cell"><a href="#" value="yesterday">${views.include_auto['昨天']}</a></li>
            <li class="mui-table-view-cell"><a href="#" value="thisWeek">${views.include_auto['本周']}</a></li>
            <li class="mui-table-view-cell"><a href="#" value="lastWeek">${views.include_auto['上周']}</a></li>
            <li class="mui-table-view-cell"><a href="#" value="thisMonth">${views.include_auto['本月']}</a></li>
            <%--<li class="mui-table-view-cell"><a href="#" value="lastMonth">${views.include_auto['上月']}</a></li>--%>
            <li class="mui-table-view-cell"><a href="#" value="7days">${views.include_auto['最近7天']}</a></li>
            <li class="mui-table-view-cell"><a href="#" value="30days">${views.include_auto['最近30天']}</a></li>
        </ul>
    </div>
</div>