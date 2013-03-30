# _Vaultee_

Keep an inventory of your properties, with an automatic versioning feature, to maintain a history of changes.

Check [this post](www.apertoire.net/vaultee) for more details.

## Dependencies

- [`vert.x 1.3`](http://github.com/vert-x)
- [`postgresql 9.1`](http://www.postgresql.org/)
- [`java 7`](http://www.oracle.com/java)
- [`angularjs`](http://www.angularjs.org/)
- [`rsvp.js`](http://github.com/tildeio/rsvp.js)
- [`jquery`](http://www.jquery.com/)
- [`bootstrap`](http://twitter.github.com/bootstrap)
- [`bootswatch`](http://bootswatch.com/)
- [`ant`](http://ant.apache.org/)

Additionally, the vert-x java verticles require the following libraries

- [`jackson-annotations-2.1.1.jar`](http://wiki.fasterxml.com/JacksonDownload)
- [`jackson-core-2.1.1.jar`](http://wiki.fasterxml.com/JacksonDownload)
- [`jackson-databind-2.1.1.jar`](http://wiki.fasterxml.com/JacksonDownload)
- [`jbcrypt-0.3m.jar`](http://mvnrepository.com/artifact/org.mindrot/jbcrypt/0.3m)
- [`joda-time-2.1.jar`](http://joda-time.sourceforge.net/installation.html)
- [`jsoup-1.7.2.jar`](http://jsoup.org/download)
- [`postgresql-9.1-902.jdbc4.jar`](http://jdbc.postgresql.org/download.html)


## Getting Started

### Installation

- Install vert.x
- Install postgresql
- Instlla java
- Fork/Clone this repository

### Setup

Copy the java libraries listed above (dependencies) in a lib folder underneath the main vaultee folder

	- vaultee
		- lib
			- jbcrypt.0.3m.jar
			- ...

Create the postgresql user and database

    # Login to PostgreSQL
    psql -d template1

    # Create the user. (change $password to a real password)
    template1=# CREATE USER apertoire WITH PASSWORD '$password';

    # Create the Vaultee production database & grant all privileges on database
    template1=# CREATE DATABASE vaultee OWNER apertoire;

    # Quit the database session
    template1=# \q

Edit vaultee/src/vaultee_server.js (password needs to match the one defined in the previous step)

	var dalConf = {
		host: '<address of your postgresql server>',
		username: 'apertoire',
		password: '$password'
	}
	
Initialize the database schema and load some default data

	# run scripts from staging/sql folder
	
	# drop everything
	psql -U apertoire -d vaultee -f vaultee_drop.sql
	
	# create tables
	psql -U apertoire -d vaultee -f vaultee_create.sql
	
	# load some data
	psql -U apertoire -d vaultee -f vaultee_load.sql

Edit build.xml to change the path to vert libraries

	  <path id="core-lib-classpath">
	    <fileset dir="${core-lib}">
    	  <include name="*.jar"/>
	    </fileset>
    	<pathelement location="<path-to-vertx>/vertx/lib/vertx-core-1.3.0.final.jar" />
	    <pathelement location="<path-to-vertx>/vertx/lib/vertx-platform-1.3.0.final.jar" />
	    <pathelement location="<path-to-vertx>/vertx/lib/vertx-lang-java.1.3.0.jar" />
    	<pathelement location="<path-to-vertx>/vertx/lib/vertx-lang-jruby.1.3.0.jar" />
	    <pathelement location="<path-to-vertx>/vertx/lib/vertx-lang-rhino.1.3.0.jar" />
	    <pathelement location="<path-to-vertx>/vertx/lib/rhino-1.7R4.jar" />
	    <pathelement location="<path-to-vertx>/vertx/lib/netty-3.5.8.Final.jar" />
	    <pathelement location="<path-to-vertx>/vertx/lib/jackson-core-asl-1.9.4.jar" />
	    <pathelement location="<path-to-vertx>/vertx/lib/jackson-mapper-asl-1.9.4.jar" />
	    <pathelement location="<path-to-vertx>/vertx/lib/netty-3.5.8.Final.jar" />
	  </path>  
	

Build java code

	# run ant from the vaultee folder
	ant
	
### Running the app

If everything went fine with the steps above, and the ant build completed successfully, you may now run the app

	# invoke the shell script in the main vaultee folder
	./play.sh
	
You can login with the default user/password

	User: demo@demo.org
	Password: secret


## Additional Notes

- This is not production level code, a lot of debug logging happen both in the server and the client
- When adding an item to one of your assets (properties), it's expecting to receive an amazon url for the description field. Under the hood, it will scan amazon to retrieve the item's description and automatically store it in the database


## License

This code is released under the _LGPL-3.0_ license. Check LICENSE file for more details.