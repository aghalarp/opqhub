OPQHub
======

OPQHUb is a cloud based power quality aggregation and analysis service.

Building OPQHub
========

### Installing the Play Framework
* OPQHub uses version 2.2.2 of the Play Framework and can be downloaded at [http://downloads.typesafe.com/play/2.2.2/play-2.2.2.zip](http://downloads.typesafe.com/play/2.2.2/play-2.2.2.zip)
* Follow the instructions at [https://www.playframework.com/documentation/2.2.x/Installing](https://www.playframework.com/documentation/2.2.x/Installing) to install the Play Framework on your development machine

### Cloning the OPQHub directory
* Clone the OPQHub repository with `git clone https://github.com/openpowerquality/OPQHub.git`

### Testing the Play Framework Installation
1. Open a terminal window and change to the cloned OPQHub directory
2. Type `play`

After running the above command, you should see

           _
     _ __ | | __ _ _  _
    | '_ \| |/ _' | || |
    |  __/|_|\____|\__ /
    |_|            |__/

    play 2.2.2 built with Scala 2.10.3 (running Java 1.8.0_20),    http://www.playframework.com

    > Type "help play" or "license" for more information.
    > Type "exit" or use Ctrl+D to leave this console.

    [OPQHub] $

If you see the above, congrats, you have installed the Play Framework correctly. If you don't see the above, please refer to the Play Framework documentation.

### Compiling OPQHub
1. Open a terminal window and change to the cloned OPQHub directory
2. Enter the Play environment with the `play` command
3. Once in the Play environment, issue the command `clean`
4. To download needed dependencies, issue the command `update`
5. To compile the project, issue the command `compile`
6. Test that the application compiled correctly by completing the following steps in the section, "_Running OPQHub_".

### Running OPQHub
1. Open a terminal window and change to the cloned OPQHub directory
2. Enter the Play environment with the `play` command
3. From within the Play environment, issue the command `~ run [port #]`
4. Navigate your web browser to http://localhost:9000/ (replace 9000 with a different port number if you didn't use the default port number)

The `~` before `run` tells Play to continuously compile when it sees source changes.

`[port #]` should be set to a port that you want OPQHub to run from (9000 by default).

### Configuring the database
The default OPQHub build comes with a built-in H2 in-memory database enabled. This is great for testing purposes, but not so great for long term storage. The Play framework can also be used to connect to MySQL databases. If you plan on using MySQL for data persistence, complete the following steps.

Note that familiarity with MySQL is assumed here. If you have any questions relating to MySQL, please refer to the MySQL [documentation](http://dev.mysql.com/doc/).

1. Download and install MySQL [http://www.mysql.com/](http://www.mysql.com/)
2. Create a database for use by OPQHub named `opqhub`.
3. Create a database user named `opquser`.
4. Edit `conf/application.conf` lines 54 - 57 to reflect the following changes


    db.default.driver=com.mysql.jdbc.Driver
    db.default.url="jdbc:mysql://localhost/opqhub"
    db.default.user="opquser"
    db.default.password="your_password_here"

For further information, see Play's guide on accessing a database from Java at [https://www.playframework.com/documentation/2.2.x/JavaDatabase](https://www.playframework.com/documentation/2.2.x/JavaDatabase).

### Configuring the mailer
OPQHub has a feature that will notify users of power quality events either by e-mail or by SMS. In order for this feature to work, you must have access to an SMTP e-mail server (for instance, Google's mail service).

If you wish to enable this feature in your build, you'll need to have access to an SMTP e-mail account. Edit the file `conf/application.conf` lines 60 - 65 to setup the alert account. The following shows a standard Google account.


    smtp.host="smtp.gmail.com"
    smtp.port=465
    smtp.ssl=true
    smtp.tls=true
    smtp.user="your_alert_email@gmail.com"
    smtp.pass="your_alert_email_password"

### Deploying to production
Todo
