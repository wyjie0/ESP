<%@ page import="pojo.DO.User" %>
<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html;charset=gb2312" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${sessionScope.locale!=null}">
    <fmt:setLocale value="${sessionScope.locale}"/>
</c:if>
<fmt:setBundle basename="message"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title><fmt:message key="auditorPage"/></title>
    <!-- Bootstrap -->
    <link href="../ESP4/resources/bootstrap-3.3.7-dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="../ESP4/resources/js/jquery-3.1.0.min.js"></script>
    <script src="../ESP4/resources/js/bignumber.js"></script>
    <script src="../ESP4/resources/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
    <script src="../ESP4/resources/node_modules/web3/dist/web3.min.js"></script>
    <script src="../ESP4/resources/js/audit.js"></script>
    <link href="../ESP4/resources/css/audit.css" rel="stylesheet" type="text/css"/>
</head>
<body onload="onLoad();">
<%
    HttpSession userSession = request.getSession();
    User user = (User) userSession.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login");
    }
    String role = (String) userSession.getAttribute("role");
    if (!role.equals("auditor")) {
        response.sendRedirect("login");
    }
%>
<!-- 页眉部分 -->
<header class="container-fluid navbar-fixed-top">
    <!-- 顶部图片部分 -->
    <div class='box row'>
        <div class='col-md-2'>
            <br>
            <img class='img-responsive' src="../ESP4/resources/img/newlogo.png" alt="<fmt:message key="imageFail"/>">
        </div>
        <div class='col-md-8'>
            <p class='p1'><fmt:message key="title"/></p>
        </div>
        <div class='col-md-2'>
            <table class='table1'>
                <tr>
                    <td><fmt:message key="welcome"/></td>
                </tr>
                <tr>
                    <!-- 用户角色 -->
                    <td id="user_role">
                        <c:choose>
                            <c:when test="${user.role == 'creator'}">
                                <fmt:message key="creator"/>
                            </c:when>
                            <c:when test="${user.role == 'auditor'}">
                                <fmt:message key="auditor"/>
                            </c:when>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <!-- 用户名 -->
                    <td id="user_name">${user.uname}</td>
                </tr>
            </table>
        </div>
    </div>
    <div class="paddingtop row">
        <nav class="navbar navbar-default">
            <div class="container-fluid">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                            data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="index"><fmt:message key="home"/></a>

                </div>
                <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                    <ul class="nav navbar-nav">
                        <li><a href="javascript:void(0);" onclick="clickMode()"><fmt:message key="audit"/></a></li>
                        <li><a href="javascript:void(0);" onclick="clickMode1()"><fmt:message key="see"/></a></li>
                        <li><a href="block_info"><fmt:message key="info"/></a></li>
                    </ul>
                    <div class="navbar-form navbar-left">
                        <input data-toggle="tooltip" title="<fmt:message key="searchTitle"/>" data-placement="top"
                               id="search" type="text" class="form-control"
                               placeholder="<fmt:message key="searchPlaceHolder"/>">
                    </div>
                    <form class="navbar-form navbar-right">
                        <div class="dropdown" style="display: inline; width: 100%" id="language">
                            <a type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"
                               role="button" aria-haspopup="true" aria-expanded="false">
                                <fmt:message key="language"/>&nbsp;<span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu" style="min-width: 100%; max-width: 100%">
                                <li><a onclick="changeLanguage('zh')"><fmt:message key="Chinese"/></a></li>
                                <li><a onclick="changeLanguage('en')"><fmt:message key="English"/></a></li>
                            </ul>
                        </div>
                        &nbsp;&nbsp;
                        <a href="login" class="btn btn-default "><fmt:message key="logout"/></a>
                    </form>
                </div>
            </div>
        </nav>
    </div>
</header>

<!-- 主体部分 -->
<div class="container " style='margin-top: 175px;'>
    <table id="file_table" class="table table-striped row">
        <tr id="tHeader">
            <th class='col-md-1'><fmt:message key="choose"/></th>
            <th class='col-md-2'><fmt:message key="fileName"/></th>
            <th class='col-md-1'><fmt:message key="fileId"/></th>
            <th class='col-md-1'><fmt:message key="fileSize"/></th>
            <th class='col-md-1'><fmt:message key="operateTime"/></th>
            <th class='col-md-2'><fmt:message key="operator"/></th>
            <th class='col-md-1'><fmt:message key="fileStage"/></th>
            <th class="col-md-3"><fmt:message key="transaction"/></th>
        </tr>
    </table>
</div>

<!--生命周期查看模态框-->
<div class="modal fade" id="myModal5" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document" style="width: 65%">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel5"><fmt:message key="fileId"/><span id="showId"></span></h4>
            </div>
            <div class="modal-body row" style="margin-right: 15px;margin-left: 15px">
                <table id="fileReview" class="table table-striped">
                    <tr id="fileReviewHeader">
                        <th class="col-md-1"><fmt:message key="stage"/></th>
                        <th class="col-md-1"><fmt:message key="operate"/></th>
                        <th class="col-md-4"><fmt:message key="fileName"/></th>
                        <th class="col-md-2"><fmt:message key="fileSize"/></th>
                        <th class="col-md-2"><fmt:message key="operateTime"/></th>
                        <th class="col-md-2"><fmt:message key="operator"/></th>
                    </tr>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="close"/></button>
            </div>
        </div>
    </div>
</div>
<footer class="container-fluid">
    <div class="foot row">
        <div class="copyright">
            <p class="footer"><fmt:message key="foot"/></p>
            <p class="footer"><fmt:message key="developers"/></p>
        </div>
    </div>
</footer>

<!--审计模态框-->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel"><fmt:message key="fileAudit"/></h4>
            </div>
            <div class="modal-body row" style="margin-left: 15px;">
                <div class='col-md-4'></div>
                <div class="form-group">
                    <form id="file_audit" class="navbar-form navbar-middle col-md-4">
                        <table id="file_audit_table" class="table table-striped row">
                            <tr id="audit_table_header">
                                <th class='col-md-1'><fmt:message key="auditFile"/></th>
                                <th class='col-md-1'><fmt:message key="fileId"/></th>
                                <th class='col-md-2'><fmt:message key="fileName"/></th>
                            </tr>
                        </table>
                    </form>
                </div>
                <div class='col-md-4'></div>
            </div>
            <div class="modal-footer">
                <button id="postAudition" type="button" class="btn btn-default" data-dismiss="modal"><fmt:message
                        key="auditFile"/></button>
                <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="close"/></button>
            </div>
        </div>
    </div>
</div>

<!--结果查看模态框-->
<div class="modal fade" id="myModal1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document" style="width: 65%">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel1"><fmt:message key="auditResult"/></h4>
            </div>
            <div class="modal-body row" style="margin-right: 15px;margin-left: 15px">
                <table id="auditReview" class="table table-striped">
                    <tr id="auditReviewHeader">
                        <th class="col-md-1"><fmt:message key="fileId"/></th>
                        <th class="col-md-2"><fmt:message key="fileName"/></th>
                        <th class="col-md-2"><fmt:message key="resultOne"/></th>
                        <th class="col-md-2"><fmt:message key="resultTwo"/></th>
                        <th class="col-md-3"><fmt:message key="resultThree"/></th>
                        <th class="col-md-2"><fmt:message key="resultFour"/></th>
                    </tr>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message
                        key="download"/></button>
                <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="close"/></button>
            </div>
        </div>
    </div>
</div>

<!--区块数据查看模态框-->
<div class="modal fade" id="myModal6" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document" style="width: 65%">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel6"><fmt:message key="blockHash"/><span id="showTxHash"></span>
                </h4>
            </div>
            <div class="modal-body row" style="margin-right: 15px;margin-left: 15px;">
                <table id="blockReview" class="table table-striped">
                    <tr id="blockReviewHeader">
                        <th class="col-md-1"><fmt:message key="blockNumber"/></th>
                        <th class="col-md-3"><fmt:message key="sender"/></th>
                        <th class="col-md-3"><fmt:message key="receiver"/></th>
                        <th class="col-md-3"><fmt:message key="blockHash"/></th>
                        <th class="col-md-1"><fmt:message key="txGas"/></th>
                        <th class="col-md-1"><fmt:message key="txNumber"/></th>
                    </tr>
                </table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="close"/></button>
            </div>
        </div>
    </div>
</div>

<script type='text/javascript'>
    function clickMode() {
        $('#myModal').modal({backdrop: 'static', keyboard: false});
    }

    function clickMode1() {
        $('#myModal1').modal({backdrop: 'static', keyboard: false});
    }

</script>
<script type="text/javascript" src="../ESP4/resources/js/headroom.js"></script>
<script type="text/javascript" src="../ESP4/resources/js/jQuery.headroom.js"></script>
<script type="text/javascript">
    $(".navbar-fixed-top").headroom();

    function changeLanguage(language) {
        window.location.href = "index?code=" + language;
        alert(language);
        //为什么执行两遍？
        <%
            String code = request.getParameter("code");
            String locale = session.getAttribute("locale").toString();
                  System.out.println(code);
                  System.out.println(session.getAttribute("locale"));
                  if (!("en".equals(code)&&"en_US".equals(locale))||
                  !("zh".equals(code)&&"zh_CN".equals(locale))){
                      if ("en".equals(code)) {
                          session.setAttribute("locale", new Locale("en", "US"));
                      } else if ("zh".equals(code)) {
                          session.setAttribute("locale", new Locale("zh", "CN"));
                      }
                  } else{
                      session.setAttribute("locale", new Locale("en", "US"));
                  }
                  System.out.println(code);
                  System.out.println(session.getAttribute("locale"));
        %>
        if (location.href.indexOf('#reloaded') == -1) {
            location.href = location.href + "?code=" + language + "#reloaded";
            location.reload();
        }
    }

    function onLoad() {
        $.ajax({
            url: "retrieve?operate=all",
            type: "get",
            dataType: "json",
            success: function (data) {
                loadFile(data, status);
            },
            error: function (XMLResponse) {
                alert("<fmt:message key="notFile"/>" + XMLResponse.responseText);
            }
        });
    }

    function clickModeReview(id) {
        fId = id;
        $.ajax({
            type: "get",
            url: "retrieve?operate=stage&fid=" + fId,
            async: true,
            dataType: "json",
            success: function (data) {
                console.log("<fmt:message key="getStageSuccess"/>");
                loadLife(data);
                $('#myModal5').modal({
                    backdrop: 'static',
                    keyboard: false
                });
            },
            error: function (XMLResponse) {
                alert("<fmt:message key="error"/>" + XMLResponse.responseText);
            }
        });
    }

    var jsonAddress = "../../ESP4/build/contracts/Provenance.json";
    var CONTRACT_ADDRESS = "${contract}";
    var web3 = new Web3(new Web3.providers.HttpProvider("http://127.0.0.1:7545"));
    $("#postAudition").click(function () {
        $.getJSON(jsonAddress, function (data) {
            //创建合约
            var Provenance = web3.eth.contract(data.abi).at(CONTRACT_ADDRESS);
            var param = { //扣取gas的账户地址
                from: web3.eth.accounts[0],
                gas: 3000000 //不限制gas值会产生out of gas的错误
            };
            var audit = {};
            $(".auditFile:checked").each(
                function () {
                    //记录fid fileName
                    var str = $(this).val();
                    var index = str.indexOf('_');
                    audit = {};
                    audit["fid"] = str.substring(0, index);
                    audit["fileName"] = str.substring(index + 1, str.length);
                    var stageCount = 0;
                    var txData = [];
                    for (var i = 1; Provenance.stages(audit["fid"], i)[4] != 0; i++) {
                        stageCount++;
                        txData.push(Provenance.stages(audit["fid"], i)[4]);
                    }
                    audit["stageCount"] = stageCount;
                    audit["txData"] = txData;
                    audits.push(audit);
                });
            json = JSON.stringify(audits);
            // console.log(json);
            // //去掉中括号
            // var standardJson = json.slice(1, json.length - 1);
            // console.log("standardJson" + standardJson);

            $.ajax({
                url: "audit",
                type: "post",
                traditional: true,
                data: json,
                success: function (auditResult) {
                    alert("<fmt:message key="auditSuccess"/>");
                    var auditsObj = eval('(' + json + ')');
                    console.log(auditsObj);
                    console.log(auditResult);
                    loadAuditResult(auditsObj, auditResult);
                },
                error: function (XMLResponse) {
                    alert("<fmt:message key="error"/>" + XMLResponse.responseText);
                }
            });
        });
    });

    //加载首页文件列表
    //插入单行文件
    function insRow(id, file, rowIndex) {
        var see = "<fmt:message key="see"/>";
        var x = document.getElementById(id).insertRow(rowIndex);
        var c0 = x.insertCell(0);
        var c1 = x.insertCell(1);
        var c2 = x.insertCell(2);
        var c3 = x.insertCell(3);
        var c4 = x.insertCell(4);
        var c5 = x.insertCell(5);
        var c6 = x.insertCell(6);
        var c7 = x.insertCell(7);
        c0.closest("tr").setAttribute(//设置当前行的权限属性
            "bianji", file.write
        );
        c0.closest("tr").setAttribute(//设置当前行的权限属性
            "duqu", file.read
        );
        c0.closest("tr").setAttribute(//设置当前行的权限属性
            "zhuanrang", file.own
        );
        c0.innerHTML = '<input type="checkbox" name = "choose" id="' + file.fid + '" fileName = "' + file.fileName + '" >';
        c1.innerHTML = file.fileName;
        c2.innerHTML = file.fid;
        c3.innerHTML = file.fileSize;
        c4.innerHTML = file.operateDateString;
        c5.innerHTML = file.newOperatePID;
        txHash = new BigNumber(file.txHash).toString(16);
        var temp = JSON.stringify(txHash);
        c6.innerHTML = "<a onclick = 'clickModeReview(" + file.fid + ");' >" + see + "</a>";
        c7.innerHTML = "<a onclick = 'clickModeBlock(" + temp + ");' >" + txHash + "</a>";
        //自动换行
        c1.style.cssText = 'word-wrap: break-word; word-break: break-all;';
        c5.style.cssText = 'word-wrap: break-word; word-break: break-all;';
        c7.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    }
</script>
</body>
</html>