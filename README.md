# GitReports
Generating developer activity reports and diffs from Git repository. 

## Usage

For generating activity reports (.csv) for all contributors for a certain period:

`java -jar gitreports.jar --activity <repo URL> --user <git username> --password <git password> --from <date> --to <date> `

For generating diffs for all contributors for a certain period:

`java -jar gitreports.jar --activity <repo URL> --user <git username> --password <git password> --from <date> --to <date> `

## Building

`mvn package`
