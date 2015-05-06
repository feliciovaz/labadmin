
Implementation of a servlet that retrieves the list of IP addresses of the ALU lab environment.

Should use Tomcat as the Servlet container.
Tomcat 8.0.9 -> Installed in c:\tomcat-8.0.9

1. Create the project using a maven create archetype command.

mvn archetype:generate -DgroupId=pt.alu.lab -DartifactId=ip-admin -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false

 -> mvn package

2. Import project in Eclipse

 -> import existing maven project
	! ERROR on the index.jsp page: 
	The superclass "javax.servlet.http.HttpServlet" was not found on the Java Build Path
	
	Solution: add in the pom.xml the 'javax.servlet-api'


3. Created source folder in Eclipse project
src/main/java

4. Created servlet IpAdmin

5. startup tomcat
 
 ATTENTION: Do not use double quotes on JAVA_HOME or CATALINA_HOME, even if the path has whitespaces on it.
 
 
 c:\felicio\projects\11 - first-servlet-lab-ip\ip-admin>c:\tomcat-8.0.9\bin\startup.bat
The CATALINA_HOME environment variable is not defined correctly
This environment variable is needed to run this program

 c:\tomcat-8.0.9\bin\setenv.bat 
 set CLASSPATH=c:\Program Files\Java\jdk1.8.0_05\lib\tools.jar;C:\apache-ant-1.9.4\lib\ant.jar;C:\apache-ant-1.9.4\lib\ant-launcher.jar

set JAVA_HOME=c:\Program Files\Java\jdk1.8.0_05
set CATALINA_HOME=c:\tomcat-8.0.9
c:\tomcat-8.0.9\bin\startup.bat

5. How to deploy the application in Tomcat?
Create a server in Eclipse
	
cd c:\felicio\projects\11 - first-servlet-lab-ip\ip-admin
mvn package
copy /Y target\ip-admin.war \tomcat-8.0.9\webapps

6. !! Problems in Eclipse when doing: Maven -> Update project
Errors occurred during the build.
Errors running builder 'Maven Project Builder' on project 'ip-admin'.
Could not calculate build plan: Plugin org.apache.maven.plugins:maven-war-plugin:2.2 or one of its dependencies could not be resolved: Failed to read artifact descriptor for org.apache.maven.plugins:maven-war-plugin:jar:2.2
Plugin org.apache.maven.plugins:maven-war-plugin:2.2 or one of its dependencies could not be resolved: Failed to read artifact descriptor for org.apache.maven.plugins:maven-war-plugin:jar:2.2
Could not calculate build plan: Plugin org.apache.maven.plugins:maven-war-plugin:2.2 or one of its dependencies could not be resolved: Failed to read artifact descriptor for org.apache.maven.plugins:maven-war-plugin:jar:2.2
Plugin org.apache.maven.plugins:maven-war-plugin:2.2 or one of its dependencies could not be resolved: Failed to read artifact descriptor for org.apache.maven.plugins:maven-war-plugin:jar:2.2

with Maven in the command line, everything is working fine!!!

Solution: updated the war plugin to maven-war-plugin:2.3:war
This is done in the POM file.

7. Connect to MySQL Server database in localhost
"c:\Program Files\MySQL\MySQL Server 5.6\bin\mysql.exe" -u felicio -palcatel aluipadmin

mysql> select ipaddr, is_free, description from ip_addresses_tbl;

8. Created a servlet to respond to http://localhost:8080/ip-admin/listall
This servlet fetches all ip addresses on the database and then outputs them as text
on the response.

Updated the web.xml in order to have the mapping between the servlet and the URL

9. Now lets use EL

This can be tricky...
Be sure to change your web.xml in order to support servlet 3.1:
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
  <display-name>My Webapp</display-name>
</web-app>

Additionally insert the following lines on the top of you JSPs:
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

10. Installed tomcat on labv06.lab.alcatel.pt

Created user tomcat / Alcatel!123

Installed also JDK 8.

Deployed the war built on my dev env.

11. Created navigation map (see site-nav.xls)

12. Create a new servlet for vm-admin
	Table in mysql
		use aluipadmin
		
		create table virtual_machines_tbl(
			name varchar(10) not null,
			description varchar(30) not null,
			os varchar(30) not null,
			owner varchar(15) not null,
			PRIMARY KEY (name));
			
		create table virtual_machines_roles_tbl(
			role varchar(30) not null,
			vm_name varchar(10) not null,
			FOREIGN KEY (vm_name));
			
		create accounts_tbl(
			type enum(os, db, web) not null,
			username varchar(15) not null,
			password varchar(15) not null,
			resource varchar(15) not null);
			
	Create servlet class VirtualMachineAdmin
	
mysql> create table ip_addresses_tbl(
			ipaddr varchar(15) not null,
			is_free boolean not null default 1,
			description varchar(100),
			PRIMARY KEY (ipaddr));
			

Features implemented
	List IPs page
	Create IP page
	Successfuly created IP
	Modify links
	Modify page with all context
	Delete links:
		1. Create delete button on Modify form
		2. Implement deletion operation (on servlet + db)
		3. Present deletion message
	Successfuly modified IP
	Successfuly deleted IP
	Show just free IPs
	Show free IPs with different color
	Page list of IPs
	Select and modify (instead of clicking hyperlink)




Main
	+- Add IP form (add IP)
	|	+- Success (submit)
	|	|	+- *Main (main)
	|	|	+- *Add IP form (create another)
	|	+- *Add IP, with error message (submit)
	|	+- *Main (cancel)
	|
	+- Modify IP form (modify link)
	|	+- Success (submit)
	|	|	+- *Main (main)
	|	|	+- *Modify IP form (modify again)
	|	+- *Modify IP, with error message (submit)
	|	+- *Main (cancel)
	|
	+- Delete IP confirmation form (delete link)
		+- *Main (ok)
		+- *Main (cancel)
		+- *Delete IP confirmation form (ok) with error message
		
		
TODO:

- Substitute all println by LOG messages

 
DATABASE SCHEMA
 
 
CREATE TABLE ip_addresses_tbl (
	ipaddr VARCHAR(15) NOT NULL,
	is_free BIT DEFAULT 1 NOT NULL,
	description VARCHAR(100),
	PRIMARY KEY (ipaddr)
) ENGINE=InnoDB;

CREATE TABLE virtual_machines_tbl (
	name VARCHAR(10) NOT NULL,
	description VARCHAR(30) NOT NULL,
	os VARCHAR(30) NOT NULL,
	owner VARCHAR(15) NOT NULL,
	PRIMARY KEY (name)
) ENGINE=InnoDB;

CREATE TABLE virtual_machines_roles_tbl (
	role VARCHAR(50) NOT NULL,
	vm_name VARCHAR(10) not null,
	id BIGINT(20) UNSIGNED NOT NULL auto_increment,
	primary key (id),
	foreign key (vm_name) references virtual_machines_tbl (name)
);

---------------------------

Created labadmin repository in GitHub (felicio.vaz@gmail.com, username: feliciovaz)

$ git remote add origin https://github.com/feliciovaz/labadmin.git
$ git push -u origin master

---------------------------

Anatomy of this servlet web application

- request mapping can be made through the web deployment descriptor file or through the @WebServlet annotation of a servlet class
There are currently two servlet classes:
	- IpAdmin             : supports all operations regarding the IP addresses administration.
	- VirtualMachineAdmin : supports all operations regarding the VM administration.
	
- typically a servlet needs a database support class. This should be injected to the servlet, but for now it is being instantiated 
on the servlet constructor. This is called a closely coupled dependency.
It should be implemented as losely coupled dependency, in the future this can be done with Spring dependency injection. To do this 
an interface should be defined and referenced in the servlet.
But what interface should be supported by such an interface? Maybe it is better to use an ORM like Hibernate.

In this moment the LabDatabase class in fact just supports the VirtualMachineAdmin servlet, IpAdmin servlet does not access it. In 
fact the LabDatabase is instantiated by the VirtualMachineAdmin servlet.
	
The IpAdmin servlet implements its own methods to access the database. In fact the connection string to the database is duplicated
in both servlets.

For sure there should be just a singleton that holds the access to the database. But I am not sure what interface it should provided
besides the: openDb(), closeDb(), isOpen().

- The structure of the servlets is:
		- for doGet() and doPost()
			- get some parameters from the request to check what operation is to be performed
			- do the operation which may be:
				- an access to the database 
				- preparation for the next page to be presented, this is basically setting attributes to be read by JSPs (mainly
				  through EL expressions)
				- or both
			- call the next page rendering (request.getRequestDispatcher(jspPage).forward(request, response)


---------------------------
