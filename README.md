# _Vaultee_

Keep an inventory of your properties, with an automatic versioning feature, to maintain a history of changes.

Check [this post](http://www.apertoire.net/vaultee) for more details.

## Dependencies

- [`vert.x 1.3.1-final`](http://vertx.io/)
- [`postgresql 9.2`](http://www.postgresql.org/)
- [`openjdk 7`](http://openjdk.java.net/)
- [`angularjs`](http://www.angularjs.org/)
- [`rsvp.js`](http://github.com/tildeio/rsvp.js)
- [`jquery`](http://www.jquery.com/)
- [`bootstrap`](http://twitter.github.com/bootstrap)
- [`bootswatch`](http://bootswatch.com/)
- [`mvn`](http://maven.apache.org/)

Additionally, the vert-x java verticles require the following libraries

- [`jackson-annotations-2.1.1.jar`](http://wiki.fasterxml.com/JacksonDownload)
- [`jackson-core-2.1.1.jar`](http://wiki.fasterxml.com/JacksonDownload)
- [`jackson-databind-2.1.1.jar`](http://wiki.fasterxml.com/JacksonDownload)
- [`jbcrypt-0.3m.jar`](http://mvnrepository.com/artifact/org.mindrot/jbcrypt/0.3m)
- [`joda-time-2.1.jar`](http://joda-time.sourceforge.net/installation.html)
- [`jsoup-1.7.2.jar`](http://jsoup.org/download)
- [`postgresql-9.2-1002.jdbc4.jar`](http://jdbc.postgresql.org/download.html)

## Quick Start

Go to [vaultee.herokuapp.com](https://vaultee.herokuapp.com/) for a live demo

## Getting Started

### Installation

- Install vert.x
- Install postgresql
- Install openjdk
- Fork/Clone this repository

### Setup

Create a file name "environment" in the main folder and setup the following environment variables (works in osx/linux):

	export PORT=9000
	export DATABASE_URL=postgres://user:password@host:port/database

Replace as appropiate for your dev setup

Create the postgresql user and database

    # Login to PostgreSQL
    psql -d template1

    # Create the user. (change $password to a real password)
    template1=# CREATE USER apertoire WITH PASSWORD '$password';

    # Create the Vaultee production database & grant all privileges on database
    template1=# CREATE DATABASE vaultee OWNER apertoire;

    # Quit the database session
    template1=# \q

Initialize the database schema and load some default data

	# run scripts from the sql folder
	
	# drop everything
	psql -U apertoire -d vaultee -f vaultee_drop.sql
	
	# create tables
	psql -U apertoire -d vaultee -f vaultee_create.sql
	
	# load some data
	psql -U apertoire -d vaultee -f vaultee_load.sql


Build java code

	# run maven from the vaultee folder
	mvn clean install
	
### Running the app

If everything went fine with the steps above, and the ant build completed successfully, you may now run the app

	# invoke the shell script in the main vaultee folder
	./play.sh
	
You can login with the default user/password

	User: demo@apertoire.net
	Password: secret


## Additional Notes

- This is not production level code, a lot of debug logging happen both in the server and the client
- When adding an item to one of your assets (properties), it's expecting to receive an amazon url for the description field. Under the hood, it will scan amazon to retrieve the item's description and automatically store it in the database


## License

This code is released under the _LGPL-3.0_ license. Check LICENSE file for more details.