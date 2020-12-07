pragma solidity ^0.4.24;

// 一个合约代表一个股票
contract StockDeal {
    uint StockName;
    address issuer;
    uint IssueAmount;
    mapping (address => uint256) public User;

    function createStock (
        uint _name,
        uint _amount
    ) public {
        StockName = _name;
        issuer = msg.sender;
        IssueAmount = _amount;
        User[issuer] = IssueAmount;
    }

    function distributeByIssuer (
        address _target,
        uint _amount
    ) public {
        User[_target] = _amount;
        User[issuer] -= _amount;
    }

    function getStockAmount (
        address _owner
    ) public constant returns (uint _amount) {
        _amount = User[_owner];
    }

}
