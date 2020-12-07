var fId;
var txHash;

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

//插入单列
//初始化表格
function loadBlock(txHash) {
    $("#showTxHash").html(txHash);
    var x = document.getElementById("blockReview").insertRow(1);
    var c0 = x.insertCell(0);
    var c1 = x.insertCell(1);
    var c2 = x.insertCell(2);
    var c3 = x.insertCell(3);
    var c4 = x.insertCell(4);
    var c5 = x.insertCell(5);
    txHash = "0x" + txHash;
    web3js.eth.getTransactionReceipt(txHash, function (error, tempResult) {
        if (!error) {
            console.log(tempResult);
            c0.innerHTML = tempResult.blockNumber;
            c1.innerHTML = tempResult.from;
            c2.innerHTML = tempResult.to;
            c3.innerHTML = tempResult.transactionHash;
            c4.innerHTML = tempResult.gasUsed;
            c5.innerHTML = tempResult.transactionIndex;
        } else {
            console.log(error);
        }

    });
    //自动换行
    c0.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c1.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c2.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c3.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c4.style.cssText = 'word-wrap: break-word; word-break: break-all;';
    c5.style.cssText = 'word-wrap: break-word; word-break: break-all;';
}


//拉取文件列表
function loadFile(list) {
    for (var i = 0; i < list.length; i++) {
        insRow("file_table", list[i], i + 1);
    }
}

$(document).ready(function () {
    $(function () {
        $("[data-toggle='tooltip']").tooltip();
    });
    // //获取用户搜索输入 移动至index.jsp

    //模态框被隐藏时
    //清空table
    $("#myModal5").on('hidden.bs.modal', function () {
        //清空数据
        fId = null;
        $("#fileReviewHeader").nextAll().remove();
    });
});

//点击事件
function clickModeUpload() {
    $('#myModal').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function clickModeEdit() {
    $('#myModal4').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function clickModeDownload() {
    $('#myModal1').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function clickModeTrans() {
    $('#myModal2').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function clickModeDelet() {
    $('#myModal3').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function clickModeBlock(hashStr) {
    loadBlock(hashStr);
    $('#myModal6').modal({
        backdrop: 'static',
        keyboard: false
    });

    // $('#myModal6').on('hidden.bs.modal', function () {
    //     $("#blockReviewHeader+tr").empty();
    // });
}
