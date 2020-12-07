# ESP: Efficient and Secure data Provenance scheme
## Environment required
- JDK 9+ (Using **IntelliJ IDEA** to compile and deploy this **Maven Web** projects is recommended)
- MySQL 5.7+
- Truffle v4.1.10 and Solidity v0.4.24 (the path of truffle had been added into system environment variables)
- Ganache v2.0.1 (a mock blockchain system)
- Tomcat 8.5.40
## Deploy the smart contract
1. Open Ganache client
2. Compile the source code  
`mvn compile`
3. Configure Tomcat server  
set HTTP port as 8888 and URL as http://localhost:8888/ESP4/login  
4. Run Tomcat server  
Ganache generates two transactions and smart contract has been deployed successfully.  

![Smart contract has been deployed by truffle](http://chuantu.xyz/t6/708/1577188519x2890211899.png)

## Create database
Use the MySQL management tools to run the SQL script files **in the sql file folder** to generate five tables, 
among which the user table `t_user` and parameter table `t_params` have initial data, and other tables only have structure.
## Login in the system
The system has two user roles: creator and editor. The creator has two accounts: **owner** and **editor** 
and the auditor has only one account: auditor. The username and password of the above accounts are the same. No registration
function yet :)  

After entering the system index page, you can upload, edit, transfer, delete, download the file.  

![index page](http://chuantu.xyz/t6/708/1577188681x2890191687.png)  

Except for the download operation, every operation on the file will generate a transaction record on the **ganache** blockchain, 
and the file table `t_file` and traceability record table `t_provenance` of the database will add corresponding records.

## _Note_
- File will been uploaded to the directory: `target/MavenWeb/WEB-INF/upload`
- After shutdown the Tomcat server, `t_file` `t_provenanve` `t_rule` tables will be clear to for next test. But the uploaded file 
could not been removed. To prevent duplicate names, you can manually delete files in the directory: `target/MavenWeb/WEB-INF/upload`.
