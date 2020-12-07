var auditFileId = [];
var auditFileName = [];
var fId;
var audits = [];
var json = "";

//插入文件生命周期单列
function insLifeRow(id, file, rowIndex) {
    //文件ID
    $("#showId").html(file.fid);
    var x = document.getElementById(id).insertRow(rowIndex);
    var c0 = x.insertCell(0);
    var c1 = x.insertCell(1);
    var c2 = x.insertCell(2);
    var c3 = x.insertCell(3);
    var c4 = x.insertCell(4);
    var c5 = x.insertCell(5);
    c0.innerHTML = file.fileStage;
    c1.innerHTML = file.fileState;
    c2.innerHTML = file.fileName;
    c3.innerHTML = file.fileSize;
    c4.innerHTML = file.operateDateString;
    c5.innerHTML = file.newOperatePID;
    //自动换行
    c2.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c5.style.cssText = 'word-wrap: break-word; word-break: break-all;';
}

//初始化生命周期表格
function loadLife(data) {
    for (var i = 0; i < data.length; i++) {
        insLifeRow("fileReview", data[i], i + 1);
    }
}

//加载文件
function loadFile(list) {
    for (var i = 0; i < list.length; i++) {
        insRow("file_table", list[i], i + 1);
    }
}

//加载被选中待审计文件
//获取被选中的文件id和文件名
function getFileList(select) {
    $(select).each(
        function () {
            auditFileId.push($(this).attr("id"));
            auditFileName.push($(this).attr("fileName"));
        }
    );
}

//插入单行预审计文件
function insAuditRow(fId, fName, rowIndex) {
    var y = document.getElementById("file_audit_table").insertRow(rowIndex);
    var c00 = y.insertCell(0);
    var c01 = y.insertCell(1);
    var c02 = y.insertCell(2);
    var value = fId + '_' + fName;
    console.log("value " + value);
    c00.innerHTML = '<input type="checkbox" class = "auditFile" name="fileToAudit" value="' + value + '" checked="checked">';
    c01.innerHTML = fId;
    c02.innerHTML = fName;
}

//模态框显示预审计文件
function loadAuditFile(fIdList, fNameList) {
    for (var i = 0; i < fIdList.length; i++) {
        insAuditRow(fIdList[i], fNameList[i], i + 1);
    }
}

$(document).ready(function () {
    //模态框显示时，加载table
    //审计文件加载
    $("#myModal").on('shown.bs.modal', function () {
        getFileList(":checked");
        //		console.log("获取文件id"+auditFileId);
        loadAuditFile(auditFileId, auditFileName);
    });
    //模态框被隐藏时
    //清空table
    $("#myModal").on('hidden.bs.modal', function () {
        //清空数据
        auditFileId = [];
        auditFileName = [];
        $("#audit_table_header").nextAll().remove();
    });
    //看溯源审计日志
    $("#myModal1").on('shown.bs.modal', function () {
    });
    //	模态框被隐藏时
    //清空溯源记录审计结果
    $("#myModal1").on('hidden.bs.modal', function () {
        $("#auditReviewHeader").nextAll().remove();
    });
    $(function () {
        $("[data-toggle='tooltip']").tooltip();
    });
    //获取用户搜索输入
    var searchText;
    $("#search").change(function () {
        searchText = $("#search").val();
        console.log(searchText);
        //审计不需要根据权限搜索
        // if (searchText == "编辑") {
        //     $("#file_table tr").not("#tHeader").hide();//保证表头显示
        //     $("#file_table tr[bianji='true']").show();
        //     $("#file_table tr[zhuanrang='true']").show();
        // } else if (searchText == "下载") {
        //     $("#file_table tr").not("#tHeader").hide();//保证表头显示
        //     $("#file_table tr[duqu='true']").show();
        //     $("#file_table tr[bianji='true']").show();
        //     $("#file_table tr[zhuanrang='true']").show();
        // } else if (searchText == "转让") {
        //     $("#file_table tr").not("#tHeader").hide();//保证表头显示
        //     $("#file_table tr[zhuanrang='true']").show();
        // } else if (searchText == "删除") {
        //     $("#file_table tr").not("#tHeader").hide();//保证表头显示
        //     $("#file_table tr[zhuanrang='true']").show();
        // } else {
        //     $("#file_table tr").not("#tHeader").hide();//保证表头显示
        //     $('#file_table tr:contains(' + searchText + ')').show();//只显示搜索了的
        // }
        $("#file_table tr").not("#tHeader").hide();//保证表头显示
        $('#file_table tr:contains(' + searchText + ')').show();//只显示搜索了的
    });
    //	模态框被隐藏时
    //清空table
    $("#myModal5").on('hidden.bs.modal', function () {
        //清空数据
        fId = null;
        $("#fileReviewHeader").nextAll().remove();
    });

    $("#myModal1").on('hidden.bs.modal', function () {
        //清空数据
        fId = null;
        $("#fileReviewHeader").nextAll().remove();
    });
});

//初始化表格
function loadBlock(txHash) {
    var web3 = new Web3(new Web3.providers.HttpProvider("http://127.0.0.1:7545"));
    $("#showTxHash").html(txHash);
    var x = document.getElementById("blockReview").insertRow(1);
    var c0 = x.insertCell(0);
    var c1 = x.insertCell(1);
    var c2 = x.insertCell(2);
    var c3 = x.insertCell(3);
    var c4 = x.insertCell(4);
    var c5 = x.insertCell(5);
    txHash = "0x" + txHash;
    c0.innerHTML = web3.eth.getTransactionReceipt(txHash).blockNumber;
    c1.innerHTML = web3.eth.getTransactionReceipt(txHash).from;
    c2.innerHTML = web3.eth.getTransactionReceipt(txHash).to;
    c3.innerHTML = web3.eth.getTransactionReceipt(txHash).transactionHash;
    c4.innerHTML = web3.eth.getTransactionReceipt(txHash).gasUsed;
    c5.innerHTML = web3.eth.getTransactionReceipt(txHash).transactionIndex;
    //自动换行
    c0.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c1.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c2.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c3.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c4.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c5.style.cssText = 'word-wrap: break-word; word-break: break-all;';
}

function clickModeBlock(hashStr) {
    loadBlock(hashStr);
    $('#myModal6').modal({
        backdrop: 'static',
        keyboard: false
    });

    $('#myModal6').on('hidden.bs.modal', function () {
        $("#blockReviewHeader+tr").empty();
    });
}

//插入审计结果单列
function insAuditResultRow(id, file, result, rowIndex) {
    var x = document.getElementById(id).insertRow(rowIndex);
    var c0 = x.insertCell(0);
    var c1 = x.insertCell(1);
    var c2 = x.insertCell(2);
    var c3 = x.insertCell(3);
    var c4 = x.insertCell(4);
    var c5 = x.insertCell(5);
    console.log(file.fid);
    console.log(file.fileName);
    c0.innerHTML = file.fid;
    c1.innerHTML = file.fileName;
    c2.innerHTML = result.checkPn;
    c3.innerHTML = result.checkSt_n;
    c4.innerHTML = result.checkAmount;
    c5.innerHTML = result.checkRestPn;
    // //自动换行
    c1.style.cssText = 'word-wrap: break-word; word-break: break-all;';
}

function loadAuditResult(auditsObj, auditResultObj) {
    for (var i = 0; i < auditResultObj.length; i++) {
        insAuditResultRow("auditReview", auditsObj[i], auditResultObj[i], i + 1);
    }
}