<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>

<head>
    <title>${views.mine_auto['个人资料']}</title>
    <%@ include file="/include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css" />
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css" />
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.dtpicker.css" />
</head>
<body class="gb-theme mine-page">
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <c:set value="${ (empty noticeContactWayMap['110'].contactValue && noticeContactWayMap['110'].status ne 22) ||
                        (empty noticeContactWayMap['201'].contactValue && noticeContactWayMap['201'].status ne 22) ||
                        empty sysUser.realName ||
                        empty sysUser.sex ||
                        empty sysUser.birthday ||
                        empty sysUserProtectionVo.result.question1 ||
                        (empty noticeContactWayMap['304'].contactValue && noticeContactWayMap['304'].status ne 22) ||
                        (empty noticeContactWayMap['301'].contactValue && noticeContactWayMap['301'].status ne 22) }" var="showTips"></c:set>
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
                <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
                <h1 class="mui-title">${views.mine_auto['个人资料']}</h1>
            </header>
            <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
                <div class="mui-scroll">
                    <form id="editForm">
                        <div id="validateRule" style="display: none">${validateRule}</div>
                        <div class="mui-row">
                            <div class="gb-form-notice">
                                <p>
                                    <img src="${resRoot}/images/ico-notice.png" height="12px;">
                                    <c:choose>
                                        <c:when test="${showTips}">
                                        ${views.mine_auto['完善资料能提升账户安全']}
                                        </c:when>
                                        <c:otherwise>
                                        ${views.mine_auto['为最大程度保护您账户安全']}
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                        <div class="mui-row">
                            <div class="mui-input-group mine-form">
                                <div class="mui-input-row"><label>${views.mine_auto['账号']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right text-gray">
                                            ${soulFn:overlayString(sysUser.username)}
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row"><label>${views.mine_auto['真实姓名']}</label>
                                    <div class="ct">
                                        <c:choose>
                                            <c:when test="${not empty sysUser.realName}">

                                                    <p class="mui-text-right text-gray">
                                                        ${soulFn:overlayName(sysUser.realName)}
                                                        <%--<input name="result.realName" value="${sysUser.realName}" type="hidden">--%>
                                                    </p>

                                            </c:when>
                                            <c:otherwise>
                                                <input type="text" class="field-input" placeholder="${views.mine_auto['请输入真实姓名']}" name="result.realName">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <div class="mui-input-row"><label>${views.mine_auto['性别']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right text-gray">
                                            <c:choose>
                                                <c:when test="${not empty sysUser.sex}">
                                                    ${dicts.common.sex[sysUser.sex]}
                                                </c:when>
                                                <c:otherwise>
                                                    <a class="mui-btn mui-btn-link gb-input-link text-gray" id="sexSelect" style="font-size:12px;">
                                                        ${views.mine_auto['请设置']}
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                            <input name="result.sex" value="${sysUser.sex}" id="sex" type="hidden"/>
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row"><label>${views.mine_auto['出生日期']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right text-gray">
                                            <c:choose>
                                                <c:when test="${not empty sysUser.birthday}">
                                                    ${soulFn:formatDateTz(sysUser.birthday,DateFormat.DAY,timeZone)}
                                                    <%--<input type="hidden" name="result.birthday" value="${soulFn:formatDateTz(sysUser.birthday,DateFormat.DAY,timeZone)}"/>--%>
                                                </c:when>
                                                <c:otherwise>
                                                    <a class="mui-btn mui-btn-link gb-input-link text-gray" id="birthdaySelect" style="font-size:12px;"
                                                       data-options='{"type":"date","beginYear":1949,"endYear":2050}'>
                                                        ${views.mine_auto['请设置']}
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                            <input name="result.birthday" id="birthday" value="${soulFn:formatDateTz(sysUser.birthday, DateFormat.DAY,timeZone)}" type="hidden" />
                                        </p>
                                    </div>
                                </div>
                            </div>
                            <c:choose>
                                <c:when test="${empty sysUserProtectionVo.result.question1 && empty sysUserProtectionVo.result.answer1}">
                                    <div class="mui-input-group mine-form m-t-sm">
                                        <div class="mui-input-row"><label>${views.mine_auto['安全问题']}</label>
                                            <div class="ct">
                                                <div class="mui-pull-right">
                                                    <div id="questionButton" class="gb-select text-gray" style="font-size: 12px;">
                                                        ${views.mine_auto['请选择']}
                                                    </div>

                                                </div>
                                            </div>
                                            <input type="hidden" name="sysUserProtection.question1" id="sysUserProtection.question1" value=""/>
                                        </div>
                                        <div class="mui-input-row"><label>${views.mine_auto['问题答案']}</label>
                                            <div class="ct">
                                                <input type="text" name="sysUserProtection.answer1" maxlength="30" value="${sysUserProtectionVo.result.answer1}"
                                                       class="mui-input" placeholder="${views.register['signUp.sysUserProtection.answer1']}">
                                            </div>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="mui-input-group mine-form m-t-sm">
                                        <div class="mui-input-row">
                                            <label>${views.mine_auto['已设置安全保护问题']}</label>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <div class="mui-input-group mine-form m-t-sm">
                                <div class="mui-input-row"><label>${views.mine_auto['手机号']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right text-gray">
                                            <c:choose>
                                                <c:when test="${noticeContactWayMap['110'].status eq 22}">
                                                    ${views.mine_auto['已被设为最高级别隐私']}
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${fn:length(noticeContactWayMap['110'].contactValue)>0}">
                                                            ${soulFn:overlayString(noticeContactWayMap['110'].contactValue)}
                                                            <c:if test="${noticeContactWayMap['110'].status eq 11}">
                                                                ${views.mine_auto['已验证']}
                                                            </c:if>
                                                            <c:if test="${noticeContactWayMap['110'].status eq 12}">
                                                                ${views.mine_auto['未验证']}
                                                            </c:if>
                                                            <input type="hidden" name="phone.contactValue" value="${noticeContactWayMap['110'].contactValue}">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <input type="text" name="phone.contactValue" class="field-input"  placeholder="${views.mine_auto['请输入']}">
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <input name="phone.id" value="${noticeContactWayMap['110'].id}" type="hidden">
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row"><label>${views.mine_auto['注册邮箱']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right text-gray">
                                            <c:choose>
                                                <c:when test="${noticeContactWayMap['201'].status eq 22}">
                                                    ${views.mine_auto['已被设为最高级别隐私']}
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${fn:length(noticeContactWayMap['201'].contactValue)>0}">
                                                            ${soulFn:overlayString(noticeContactWayMap['201'].contactValue)}
                                                            <c:if test="${noticeContactWayMap['201'].status eq 11}">
                                                                ${views.mine_auto['已验证']}
                                                            </c:if>
                                                            <c:if test="${noticeContactWayMap['201'].status eq 12}">
                                                                ${views.mine_auto['未验证']}
                                                            </c:if>
                                                            <input type="hidden" name="email.contactValue" value="${noticeContactWayMap['201'].contactValue}">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <input type="text" name="email.contactValue"  class="field-input" placeholder="${views.mine_auto['请输入']}">
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <input name="email.id" value="${noticeContactWayMap['201'].id}" type="hidden">
                                                </c:otherwise>
                                            </c:choose>

                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row"><label>${views.mine_auto['微信']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right text-gray">
                                            <c:choose>
                                                <c:when test="${noticeContactWayMap['304'].status eq '22'}">
                                                    ${views.mine_auto['已被设为最高级别隐私']}
                                                    <input type="hidden" name="weixin.contactValue"
                                                           value="${noticeContactWays gt 0?noticeContactWayMap["304"].contactValue:''}">
                                                    <input type="hidden" value="${noticeContactWayMap["304"].id}" name="weixin.id">
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${empty noticeContactWayMap['304'].contactValue}">
                                                            <input type="text" name="weixin.contactValue"
                                                                   class="field-input"
                                                                   value="${noticeContactWays gt 0?noticeContactWayMap["304"].contactValue:''}"
                                                                   maxlength="30" placeholder="${views.mine_auto['请输入']}">
                                                            <input type="hidden" value="${noticeContactWayMap["304"].id}" name="weixin.id">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="">${soulFn:overlayString(noticeContactWayMap["304"].contactValue)}</span>
                                                            <input type="hidden" name="weixin.contactValue"
                                                                   value="${noticeContactWays gt 0?noticeContactWayMap["304"].contactValue:''}">
                                                            <input type="hidden" value="${noticeContactWayMap["304"].id}" name="weixin.id">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>


                                </div>
                                <div class="mui-input-row"><label>QQ</label>
                                    <div class="ct">
                                        <p class="mui-text-right text-gray">
                                            <c:choose>
                                                <c:when test="${noticeContactWayMap['301'].status eq '22'}">
                                                    ${views.mine_auto['已被设为最高级别隐私']}
                                                    <input type="hidden" name="qq.contactValue"
                                                           value="${noticeContactWays gt 0?noticeContactWayMap["301"].contactValue:''}">
                                                    <input type="hidden" value="${noticeContactWayMap["301"].id}" name="qq.id">
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${empty noticeContactWayMap['301'].contactValue}">
                                                            <input type="text" name="qq.contactValue" class="field-input"
                                                                   value="${noticeContactWays gt 0?noticeContactWayMap["301"].contactValue:''}"
                                                                   maxlength="30" placeholder="${views.mine_auto['请输入']}">
                                                            <input type="hidden" value="${noticeContactWayMap["301"].id}" name="qq.id">
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${soulFn:overlayString(noticeContactWayMap["301"].contactValue)}
                                                            <input type="hidden" name="qq.contactValue"
                                                                   value="${noticeContactWays gt 0?noticeContactWayMap["301"].contactValue:''}">
                                                            <input type="hidden" value="${noticeContactWayMap["301"].id}" name="qq.id">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <c:if test="${showTips}">
                            <div class="mui-row">
                                <div class="gb-form-foot">
                                    <button class="mui-btn mui-btn-primary submit" id="updatePerson">${views.mine_auto['确认']}</button>
                                </div>
                            </div>
                        </c:if>
                    </form>
                </div>
            </div>
            <div class="mui-off-canvas-backdrop"></div>
            <div class="masker"></div>
        </div>
    </div>
</body>
</html>

<%@ include file="../include/include.base.js.common.jsp" %>
<script type="text/javascript" src="${root}/mobile/message_<%=SessionManagerCommon.getLocale().toString()%>.js?v=${rcVersion}"></script>

<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/plugins/jquery.validate/jquery.validate.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/jquery/jquery.validate.extend.mobile.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/global.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}" type="text/javascript" charset="utf-8"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}" type="text/javascript" charset="utf-8"></script>
<script src="${resRoot}/js/mui/mui.dtpicker.js?v=${rcVersion}" type="text/javascript" charset="utf-8"></script>
<script src="${resRoot}/js/my/PersonalInfo.js?v=${rcVersion}"></script>
<script>
    var language = '${language.replace('_','-')}';
    var isLogin = '${isLogin}';
    if(os == 'app_ios'){
        $('.mui-action-back').on('tap', function () {
            goBack();
        })
    }
</script>
