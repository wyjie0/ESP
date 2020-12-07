pragma solidity ^0.4.24;

contract Provenance {
    // 文件操作类型：分别代表创建0、修改1、转让2、销毁3操作
    enum Operation {create, modify, transfer, destroy}

    // 操作用户身份：创建者0、修改者1
    enum Role {creator, editor}

    // 文件操作结构
    struct FileOperation {
        address operator; // 操作者
        Role role; // 操作者身份
        uint256 operateTime; // 操作时间
        Operation opeate; // 操作类型
        string data; // 交易的数据字段
    }

    // 文件结构
    struct File {
        uint256 fileID; // 文件编号
        string fileName; // 文件名（不能重名）
        string fileType; // 文件类型
        uint256 fileSize; // 文件大小
        uint256 stageNumber; // 阶段编号
        uint256 stageCount; // 阶段数量
    }

    mapping(uint256 => mapping(uint256 => File)) public files; // 所有文件（ID-阶段编号-文件结构）
    mapping(uint256 => mapping(uint256 => FileOperation)) public stages; // 所有阶段（ID-阶段编号-操作结构）
    uint256 public fileNumber = 0; // 文件在集合中的索引

    event eventCreate (uint256 _fileNumber, uint256 _stageNumber, address _creator, string _fileName, string _fileType, uint256 _fileSize, string _data); // 创建通知事件
    event eventEdit (uint256 _fileNumber, uint256 _stageNumber, address __editor, Role _role, Operation _opeate, string _fileName, string _fileType, uint256 _fileSize, string _data); // 编辑通知事件

    function Create(
        string memory _fileName,
        string memory _fileType,
        uint256 _fileSize,
        string memory _data
    ) public
    returns (uint256 _fileNumber, uint256 _stageNumber) {
        // 文件编号从1开始
        fileNumber++;
        FileOperation memory _fileOperation = FileOperation({
            operator : msg.sender,
            role : Role.creator,
            operateTime : now,
            opeate : Operation.create,
            data : _data
            });
        // 文件1的第一阶段属性
        files[fileNumber][1].fileID = fileNumber;
        files[fileNumber][1].fileName = _fileName;
        files[fileNumber][1].fileType = _fileType;
        files[fileNumber][1].fileSize = _fileSize;
        files[fileNumber][1].stageNumber = 1;
        files[fileNumber][1].stageCount = 1;

        stages[fileNumber][1] = _fileOperation;

        _fileNumber = fileNumber;
        _stageNumber = 1;
        // 通知客户端
        emit eventCreate(1, 1, msg.sender, _fileName, _fileType, _fileSize, _data);
    }

    function Edit(
        uint256 _lastFileNumber,
        uint256 _lastStageNumber,
        Role _role,
        Operation _opeate,
        string memory _fileName,
        string memory _fileType,
        uint256 _fileSize,
        string memory _data
    ) public {
        require(
            _lastFileNumber > 0 && _lastStageNumber > 0,
            "File must have been created."
        );

        //        require(
        //            files[_lastFileNumber][_lastStageNumber].fileID != 0 && files[_lastFileNumber][_lastStageNumber + 1].fileID == 0,
        //            "Incorrect file stage number."
        //        );

        // 文件_lastFileNumber的第_lastStageNumber+1阶段属性
        files[_lastFileNumber][_lastStageNumber + 1].fileID = _lastFileNumber;
        files[_lastFileNumber][_lastStageNumber + 1].fileName = _fileName;
        files[_lastFileNumber][_lastStageNumber + 1].fileType = _fileType;
        files[_lastFileNumber][_lastStageNumber + 1].fileSize = _fileSize;
        files[_lastFileNumber][_lastStageNumber + 1].stageNumber = _lastStageNumber + 1;
        files[_lastFileNumber][_lastStageNumber + 1].stageCount = files[_lastFileNumber][_lastStageNumber].stageCount + 1;
        FileOperation memory _fileOperation = FileOperation({
            operator : msg.sender,
            role : _role,
            operateTime : now,
            opeate : _opeate,
            data : _data
            });
        stages[_lastFileNumber][_lastStageNumber + 1] = _fileOperation;

        //        _fileNumber = _lastFileNumber;
        //        _stageNumber = _lastStageNumber + 1;

        // 通知客户端
        emit eventEdit(_lastFileNumber, _lastStageNumber + 1, msg.sender, _role, _opeate, _fileName, _fileType, _fileSize, _data);
    }

    function getData(
        uint256 _fileNumber,
        uint256 __stageNumber
    ) public view
    returns (string _data) {
        _data = stages[_fileNumber][__stageNumber].data;
    }
}