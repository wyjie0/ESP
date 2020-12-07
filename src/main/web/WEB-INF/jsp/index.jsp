<%@ page import="pojo.DO.User" %>
<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html;charset=UTF-8" %>
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
    <title><fmt:message key="ownerPage"/></title>
    <link href="../ESP4/resources/bootstrap-3.3.7-dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="../ESP4/resources/js/jquery-3.1.0.min.js"></script>
    <script src="../ESP4/resources/js/bignumber.js"></script>
    <script src="../ESP4/resources/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
    <script src="../ESP4/resources/node_modules/web3/dist/web3.min.js"></script>
    <script src="../ESP4/resources/js/index.js"></script>
    <link href="../ESP4/resources/css/index.css" rel="stylesheet" type="text/css"/>
</head>
<body onload="onLoad();">
<%
    HttpSession userSession = request.getSession();
    User user = (User) userSession.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login");
    }
    String role = (String) userSession.getAttribute("role");
    if (!role.equals("owner")) {
        response.sendRedirect("login");
    }
%>
<!-- 页眉部分 -->
<div class="container-fluid navbar-fixed-top">
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
    <!-- 导航部分 -->
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
                        <li>
                            <a href="#"><fmt:message key="mine"/></a>
                        <li>
                        <li class="dropdown">
                            <a class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
                               aria-expanded="false"><fmt:message key="upload"/><span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="#" onclick="clickModeUpload()"><fmt:message key="upload"/></a></li>
                                <li><a href="#" onclick="clickModeEdit()"><fmt:message key="edit"/></a></li>
                            </ul>
                        </li>
                        <li><a onclick="clickModeDownload()"><fmt:message key="download"/></a></li>
                        <li><a onclick="clickModeTrans()"><fmt:message key="transfer"/></a></li>
                        <li><a onclick="clickModeDelet()"><fmt:message key="delete"/></a></li>
                        <li><a href="block_info"><fmt:message key="info"/></a></li>
                        <li>
                            <div class="navbar-form navbar-left">
                                <input data-toggle="tooltip" title="<fmt:message key="searchTitle"/>"
                                       data-placement="top"
                                       id="search" type="text" class="form-control"
                                       placeholder="<fmt:message key="searchPlaceHolder"/>">
                            </div>
                        </li>
                    </ul>
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
                        <a href="login" class="btn btn-default"><fmt:message key="logout"/></a>
                    </form>
                </div>
            </div>
        </nav>
    </div>
</div>

<!-- 主体部分 -->
<div class="container" style='margin-top: 175px;'>
    <table id="file_table" class="table table-striped row">
        <tr id="tHeader">
            <th class="col-md-1" style="text-align: center"><fmt:message key="fileId"/></th>
            <th class="col-md-2" style="text-align: center"><fmt:message key="fileName"/></th>
            <th class="col-md-1" style="text-align: center"><fmt:message key="fileSize"/></th>
            <th class="col-md-2" style="text-align: center"><fmt:message key="operateTime"/></th>
            <th class="col-md-2" style="text-align: center"><fmt:message key="operator"/></th>
            <th class="col-md-1" style="text-align: center"><fmt:message key="fileStage"/></th>
            <th class="col-md-3" style="text-align: center"><fmt:message key="transaction"/></th>
        </tr>
    </table>
</div>

<!-- 页脚部分 -->
<footer class="container-fluid">
    <div class="foot row">
        <div class="copyright">
            <p class="footer"><fmt:message key="foot"/></p>
            <p class="footer"><fmt:message key="developers"/></p>
        </div>
    </div>
</footer>

<!--上传模态框-->
<div class="modal fade" id="myModal">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel"><fmt:message key="upload"/></h4>
            </div>
            <div class="modal-body ">
                <div class='row'>
                    <!-- 文件上传 -->
                    <form class="navbar-form navbar-left" id="upload" name="upload_file" action="upload" method="post"
                          enctype="multipart/form-data">
                        <p class='col-md-4'><fmt:message key="uploadChoose"/></p>
                        <p class='col-md-5'><input id="uploadFileName" type="file" name="file"/></p>
                        <p class='col-md-3'><input id="upload_file" type="submit" value="<fmt:message key="upload"/>"/>
                        </p>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="close"/></button>
            </div>
        </div>
    </div>
</div>

<!--修改模态框-->
<div class="modal fade" id="myModal4">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel4"><fmt:message key="edit"/></h4>
            </div>
            <div class="modal-body ">
                <div class="row">
                    <!-- 文件编辑 -->
                    <form class="navbar-form navbar-left" id="edit" name="edit_file" action="edit" method="post"
                          enctype="multipart/form-data">
                        <div class='row'>
                            <p class='col-md-4'><fmt:message key="inputFileId"/></p>
                            <input class='col-md-6 form-control' type="text" id="editFid" name="fid"
                                   placeholder="<fmt:message key="inputFileId"/>"/>
                        </div>
                        <hr/>
                        <div class='row'>
                            <p class='col-md-4'><fmt:message key="editNewFile"/></p>
                            <p class='col-md-5'><input id="editFileName" type="file" name="file"/></p>
                            <p class='col-md-3'>
                                <button id="edit_file" type="submit" class="btn btn-default"><fmt:message
                                        key="edit"/></button>
                            </p>
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="close"/></button>
            </div>
        </div>
    </div>
</div>

<!--下载模态框-->
<div class="modal fade" id="myModal1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel1"><fmt:message key="downloadFile"/></h4>
            </div>
            <div class="modal-body ">
                <div class="row">
                    <!-- 下载文件form  -->
                    <form id="download" class="navbar-form navbar-left" action="download" method="post">
                        <input type="text" class="form-control" name="fid"
                               placeholder="<fmt:message key="inputFileId"/>">
                        <button type="submit" class="btn btn-default"><fmt:message key="download"/></button>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="close"/></button>
            </div>
        </div>
    </div>
</div>

<!--转让模态框-->
<div class="modal fade" id="myModal2">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel2"><fmt:message key="transferFile"/></h4>
            </div>
            <div class="modal-body ">
                <div class="row">
                    <!--转让-->
                    <form id="transfer" class="navbar-form navbar-left" action="transfer" method="post">
                        <div class='row'>
                            <div class='col-md-1'></div>
                            <input type="text" id="transferFid" name="fid" class="form-control col-md-4"
                                   placeholder="<fmt:message key="inputFileId"/>">
                            <div class='col-md-3'></div>
                            <input type="text" id="transferUname" name="uname" class="form-control col-md-4"
                                   placeholder="<fmt:message key="inputUserName"/>">
                            <div class='col-md-1'></div>
                        </div>
                        <br/>
                        <div class='row'>
                            <div class='col-md-1'></div>
                            <select class="form-control col-md-4" name="operate" id="">
                                <option value="" selected="selected"><fmt:message key="inputTransfer"/></option>
                                <option value="read"><fmt:message key="read"/></option>
                                <option value="write"><fmt:message key="write"/></option>
                                <option value="own"><fmt:message key="own"/></option>
                            </select>
                            <div class='col-md-4'></div>
                            <button class='col-md-2' id="transfer_file" type="submit" form="transfer"
                                    class="btn btn-default"><fmt:message key="transfer"/>
                            </button>
                            <div class='col-md-1'></div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="close"/></button>
            </div>
        </div>
    </div>
</div>

<!--删除模态框-->
<div class="modal fade" id="myModal3">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel3"><fmt:message key="deleteFile"/></h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <!--删除-->
                    <form id="delete" class="navbar-form navbar-left" action="delete" method="post">
                        <input type="text" name="fid" id="deleteFid" class="form-control"
                               placeholder="<fmt:message key="inputFileId"/>">
                        <button type="submit" id="delete_file" form="delete" class="btn btn-default"><fmt:message
                                key="delete"/></button>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="close"/></button>
            </div>
        </div>
    </div>
</div>

<!--生命周期查看模态框-->
<div class="modal fade" id="myModal5" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document" style="width: 65%">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel5"><fmt:message key="fileId"/>:<span id="showId"></span></h4>
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
    var jsonAddress = "../../ESP4/build/contracts/Provenance.json";
    var web3js = "";
    var web3 = new Web3(new Web3.providers.HttpProvider("http://127.0.0.1:7545"));
    $(document).ready(function () {
        //初始化meta mask的web3环境
        //合约地址可由后台返回
        //network不一致，导致无法使用metamask内置的web3，故使用本地web3库
        var CONTRACT_ADDRESS = "${contract}";
        if (typeof web3 !== 'undefined') {
            //     // Use Mist/MetaMask's provider
            //     web3js = new Web3(web3.currentProvider);
            //     console.log(web3js);
            // } else {
            console.log('No web3? You should consider trying MetaMask!');
            web3js = new Web3(new Web3.providers.HttpProvider("http://localhost:7545"));
        }

        //if不能嵌套
        <c:if test="${uploadCode =='1' && operate== 'upload'}">
        upload();
        <c:set var="uploadCode" value="0" scope="session"></c:set>
        </c:if>

        <c:if test="${editCode =='1' && operate== 'edit'}">
        edit();
        <c:set var="editCode" value="0" scope="session"></c:set>
        </c:if>

        <c:if test="${deleteCode =='1' && operate== 'delete'}">
        destroy();
        <c:set var="deleteCode" value="0" scope="session"></c:set>
        </c:if>

        <c:if test="${transferCode =='1' && operate== 'transfer'}">
        transfer();
        <c:set var="transferCode" value="0" scope="session"></c:set>
        </c:if>

        <c:if test="${operate == 'rejected'}">
        alert("<fmt:message key="rejected"/>");
        </c:if>

        function upload() {
            //加定时器无法实现
            $.getJSON(jsonAddress, function (data) {
                //创建合约
                var Provenance = web3js.eth.contract(data.abi).at(CONTRACT_ADDRESS);
                var param = { //扣取gas的账户地址
                    from: web3js.eth.accounts[0],
                    gas: 3000000 //不限制gas值会产生out of gas的错误
                };
                //调用合约函数
                var myBlockHash;
                Provenance.Create("${file.fileName}", "${file.fileType}", "${file.fileSize}", "${file.txData}",
                    param, function (error, result) {
                        if (!error) {
                            alert("<fmt:message key="createSuccess"/>");
                            console.log("txHash" + result);
                            web3js.eth.getTransaction(result, function (error, tempResult) {
                                if (!error) {
                                    myBlockHash = tempResult.blockHash;
                                    console.log("myBlockHash" + myBlockHash);
                                    $.ajax({
                                        url: "server?txHash=" + result + "&blockHash=" + myBlockHash,
                                        type: "post",
                                        async: false,
                                        success: function (code) {
                                            if (code == "0") {
                                                alert("<fmt:message key="databaseSuccess"/>");
                                            } else if (code == "1") {
                                                alert("<fmt:message key="databaseFail"/>");
                                            } else if (code == "2") {
                                                alert("<fmt:message key="fileEmpty"/>");
                                            }
                                            location.reload(true);
                                        }
                                    });
                                } else {
                                    console.error(error);
                                }
                            });
                        } else {
                            alert("<fmt:message key="createFail"/>");
                            //打印详细出错信息
                            console.log(error);
                        }
                    });
            });
        }

        function edit() {
            $.getJSON(jsonAddress, function (data) {
                //创建合约
                var Provenance = web3js.eth.contract(data.abi).at(CONTRACT_ADDRESS);
                var param = { //扣取gas的账户地址
                    from: web3js.eth.accounts[0],
                    gas: 3000000 //不限制gas值会产生out of gas的错误
                };
                //调用合约函数
                //文件id，上个阶段号，用户角色，操作，文件名，文件类型，大小
                //运行出错没有提示，第二个参数是本次新产生的stage
                var myBlockHash;
                Provenance.Edit("${file.fid}", "${file.fileStage-1}", 1, 1,
                    "${file.fileName}", "${file.fileType}", "${file.fileSize}", "${file.txData}", param,
                    function (error, result) {
                        if (!error) {
                            alert("<fmt:message key="editSuccess"/>");
                            console.log("txHash" + result);
                            web3js.eth.getTransaction(result, function (error, tempResult) {
                                if (!error) {
                                    console.log(tempResult);
                                    myBlockHash = tempResult.blockHash;
                                    console.log("myBlockHash" + myBlockHash);
                                    $.ajax({
                                        url: "server?txHash=" + result + "&blockHash=" + myBlockHash,
                                        type: "post",
                                        async: false,
                                        success: function (code) {
                                            if (code == "0") {
                                                alert("<fmt:message key="databaseSuccess"/>");
                                            } else if (code == "1") {
                                                alert("<fmt:message key="databaseFail"/>");
                                            } else if (code == "2") {
                                                alert("<fmt:message key="fileEmpty"/>");
                                            }
                                            location.reload(true);

                                        }
                                    })
                                } else {
                                    console.log(error);
                                }

                            });
                        } else {
                            alert("<fmt:message key="editFail"/>");
                            //打印详细出错信息
                            console.log(error);
                        }
                    });
            });
        }

        //delete是关键字
        function destroy() {
            $.getJSON(jsonAddress, function (data) {
                //创建合约
                var Provenance = web3js.eth.contract(data.abi).at(CONTRACT_ADDRESS);
                var param = { //扣取gas的账户地址
                    from: web3js.eth.accounts[0],
                    gas: 3000000 //不限制gas值会产生out of gas的错误
                };
                //调用合约函数
                //文件id，上个阶段号，用户角色，操作，文件名，文件类型，大小
                //运行出错没有提示，第二个参数是本次新产生的stage
                var myBlockHash;
                //3表示删除操作
                Provenance.Edit("${file.fid}", "${file.fileStage-1}", 1, 3,
                    "", "", 0, "${file.txData}", param,
                    function (error, result) {
                        if (!error) {
                            alert("<fmt:message key="deleteSuccess"/>");
                            console.log("txHash" + result);
                            web3js.eth.getTransaction(result, function (error, tempResult) {
                                if (!error) {
                                    console.log(tempResult);
                                    myBlockHash = tempResult.blockHash;
                                    console.log("myBlockHash" + myBlockHash);
                                    $.ajax({
                                        url: "server?txHash=" + result + "&blockHash=" + myBlockHash,
                                        type: "post",
                                        async: false,
                                        success: function (code) {
                                            if (code == "0") {
                                                alert("<fmt:message key="databaseSuccess"/>");
                                            } else if (code == "1") {
                                                alert("<fmt:message key="databaseFail"/>");
                                            } else if (code == "2") {
                                                alert("<fmt:message key="fileEmpty"/>");
                                            }
                                            location.reload(true);

                                        }
                                    })
                                } else {
                                    console.log(error);
                                }

                            });
                        } else {
                            alert("<fmt:message key="deleteFail"/>");
                            //打印详细出错信息
                            console.log(error);
                        }
                    });
            });
        }

        function transfer() {
            $.getJSON(jsonAddress, function (data) {
                //创建合约
                var Provenance = web3js.eth.contract(data.abi).at(CONTRACT_ADDRESS);
                var param = { //扣取gas的账户地址
                    from: web3js.eth.accounts[0],
                    gas: 3000000 //不限制gas值会产生out of gas的错误
                };
                //调用合约函数
                //文件id，上个阶段号，用户角色，操作，文件名，文件类型，大小
                //运行出错没有提示，第二个参数是本次新产生的stage
                var myBlockHash;
                Provenance.Edit("${file.fid}", "${file.fileStage-1}", 1, 2,
                    "${file.fileName}", "${file.fileType}", "${file.fileSize}", "${file.txData}", param,
                    function (error, result) {
                        if (!error) {
                            alert("<fmt:message key="transferSuccess"/>");
                            console.log("txHash" + result);
                            web3js.eth.getTransaction(result, function (error, tempResult) {
                                if (!error) {
                                    console.log(tempResult);
                                    myBlockHash = tempResult.blockHash;
                                    console.log("myBlockHash" + myBlockHash);
                                    $.ajax({
                                        url: "server?txHash=" + result + "&blockHash=" + myBlockHash,
                                        type: "post",
                                        async: false,
                                        success: function (code) {
                                            if (code == "0") {
                                                alert("<fmt:message key="databaseSuccess"/>");
                                            } else if (code == "1") {
                                                alert("<fmt:message key="databaseFail"/>");
                                            } else if (code == "2") {
                                                alert("<fmt:message key="fileEmpty"/>");
                                            }
                                            location.reload(true);

                                        }
                                    })
                                } else {
                                    console.log(error);
                                }

                            });
                        } else {
                            alert("<fmt:message key="transferFail"/>");
                            //打印详细出错信息
                            console.log(error);
                        }
                    });
            });
        }

        $("#upload_file").bind({
            click: function () {
                //正确性校验
                if ("${user.uid}" == "") {
                    alert("<fmt:message key="loginFail"/>");
                    return false;
                } else if ($("#uploadFileName").val() == "") {
                    alert("<fmt:message key="fileNameEmpty"/>");
                    return false;
                } else {
                    return true;
                }
            }
        });

        $("#edit_file").bind({
            click: function () {
                //正确性校验
                if ("${user.uid}" == "") {
                    alert("<fmt:message key="loginFail"/>");
                    return false;
                } else if ($("#editFid").val() == "") {
                    alert("<fmt:message key="fileIdEmpty"/>");
                    return false;
                } else if ($("#editFileName").val() == "") {
                    alert("<fmt:message key="fileNameEmpty"/>");
                    return false;
                } else {
                    return true;
                }
            }
        });

        $("#delete_file").bind({
            click: function () {
                //正确性校验
                if ("${user.uid}" == "") {
                    alert("<fmt:message key="loginFail"/>");
                    return false;
                } else if ($("#deleteFid").val() == "") {
                    alert("<fmt:message key="fileIdEmpty"/>");
                    return false;
                } else {
                    return true;
                }
            }
        });

        $("#transfer_file").bind({
            click: function () {
                //正确性校验
                if ("${user.uid}" == "") {
                    alert("<fmt:message key="loginFail"/>");
                    return false;
                } else if ($("#transferFid").val() == "") {
                    alert("<fmt:message key="fileNameEmpty"/>");
                    return false;
                } else if ($("#transferUname").val() == "") {
                    alert("<fmt:message key="userNameEmpty"/>");
                    return false;
                } else {
                    return true;
                }
            }
        });
    });
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
        c0.closest("tr").setAttribute(//设置当前行的权限属性
            "bianji", file.write
        );
        c0.closest("tr").setAttribute(//设置当前行的权限属性
            "duqu", file.read
        );
        c0.closest("tr").setAttribute(//设置当前行的权限属性
            "zhuanrang", file.own
        );
        c0.innerHTML = file.fid;
        c1.innerHTML = file.fileName;
        c2.innerHTML = file.fileSize;
        c3.innerHTML = file.operateDateString;
        c4.innerHTML = file.newOperatePID;
        txHash = new BigNumber(file.txHash).toString(16);
        var temp = JSON.stringify(txHash);
        c5.innerHTML = "<a onclick = 'clickModeReview(" + file.fid + ");' >" + see + "</a>";
        c6.innerHTML = "<a onclick = 'clickModeBlock(" + temp + ");' >" + txHash + "</a>";
        //自动换行
        c1.style.cssText = 'word-wrap: break-word; word-break: break-all;';
        c4.style.cssText = 'word-wrap: break-word; word-break: break-all;';
        c6.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    }

    //获取用户搜索输入
    var searchText;
    $("#search").change(function () {
        searchText = $("#search").val();
        console.log(searchText);
        if (searchText == "<fmt:message key="edit"/>") {
            $("#file_table tr").not("#tHeader").hide();//保证表头显示
            $("#file_table tr[bianji='true']").show();
            $("#file_table tr[zhuanrang='true']").show();
        } else if (searchText == "<fmt:message key="download"/>") {
            $("#file_table tr").not("#tHeader").hide();//保证表头显示
            $("#file_table tr[duqu='true']").show();
            $("#file_table tr[bianji='true']").show();
            $("#file_table tr[zhuanrang='true']").show();
        } else if (searchText == "<fmt:message key="transfer"/>") {
            $("#file_table tr").not("#tHeader").hide();//保证表头显示
            $("#file_table tr[zhuanrang='true']").show();
        } else if (searchText == "<fmt:message key="delete"/>") {
            $("#file_table tr").not("#tHeader").hide();//保证表头显示
            $("#file_table tr[zhuanrang='true']").show();
        } else {
            $("#file_table tr").not("#tHeader").hide();//保证表头显示
            $('#file_table tr:contains(' + searchText + ')').show();//只显示搜索了的
        }
    });

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

    function onLoad() {
        $.ajax({
            url: "retrieve?operate=myFile",
            type: "get",
            dataType: "json",
            success: function (data) {
                loadFile(data);
            },
            error: function (XMLResponse) {
                alert("<fmt:message key="error"/>" + XMLResponse.responseText);
            }
        });
    }
</script>
</body>
</html>