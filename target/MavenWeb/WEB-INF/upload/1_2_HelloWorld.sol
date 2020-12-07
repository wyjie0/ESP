pragma solidity ^0.4.23;

contract HelloWorld {
	string TEXT;
	function main() public view returns(string) {
		return TEXT;
	}
	
	function setText(string _text) public {
		TEXT = _text;
	}
}