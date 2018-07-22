Accounts money transfer API
===========================

This project implements API for money transfers between accounts. 
Example requests can be found in JUnit test cases. 
There is a lot of work to be done in order to make this API close to the real thing. 
But for the purposes of example it's enough I guess.

Frameworks used
----------------

Apache camel for routing

cxfrs for REST requests processing

mybatis for DataSource configuration

Apache Derby for in-memory database instantiation

Google gson for json/object transformation

Usage
----------------

Run 'local_run_jar.sh'(Linux/MacOS) or 'local_run_jar.bat'(Windows) script to run the application. 
It will package an application and run it. There are data presets in database, that will allow you 
to test an application with postman or some other tool.

